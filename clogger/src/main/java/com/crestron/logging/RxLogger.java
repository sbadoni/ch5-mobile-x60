package com.crestron.logging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.crestron.mobile.android.common.RxBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxLogger {

    private static final String TAG = RxLogger.class.getName();

    //shows the entire chain of method calls
    private static boolean showMethodChain = false;
    //show the calling method
    private static boolean showCallingMethod = false;
    //the file path
    private String filePath;
    //the length to wait since the last modified in milliseconds
    private static final long LAST_MODIFIED_LENGTH = 900000L;
    //the max size of the file
    private static final long FILE_SIZE_MAX = 5000000L;
    //is the RxBus already set up, don't set it up again
    boolean rxListened = false;
    List<Disposable> disposableList;

    private Context mContext;

    private File mLogFolder;

    private int mFileLogLevelFilter = Log.INFO;

    //no more static except for show method stuff (TODO: ask Colin if keeping normal log is ok to keep static)

    RxLogger(Context context, String filePath) {
        this.mContext = context;
        this.filePath = filePath;
        mLogFolder = new File(filePath);
        disposableList = new ArrayList<>();
    }

    public void init() {

          disposableList.add(RxBus.INSTANCE
                .listen(SetupLogging.LogInfo.class)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> {
                    writeLogMessage(s);
                }));

    }

    public void setFileLogLevelFilter(int newLevel)
    {
        mFileLogLevelFilter = newLevel;
    }

    /**
     * @param filePath the file path for the file
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @param showMethodChain do we want to see the calling chain
     */
    public static void setShowMethodChain(boolean showMethodChain) {
        RxLogger.showMethodChain = showMethodChain;
    }

    /**
     * will always be true if {@link RxLogger#showMethodChain} is true
     *
     * @param showCallingMethod do we want to see the calling method
     */
    public static void setShowCallingMethod(boolean showCallingMethod) {
        RxLogger.showCallingMethod = showCallingMethod;
    }


    /**
     * removes logging from the RxBus
     *
     * @return true if successful, false if unsuccessful
     */
    public boolean deinit() {
        try {
            for (Disposable d : disposableList) {
                d.dispose();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private void  writeLogMessage(SetupLogging.LogInfo logInfo) {

        String fullClassName = "";

        for (int i = 0; i < logInfo.stackTraceElement.length; i++) {
            if (logInfo.stackTraceElement[i].getClassName().compareTo("com.crestron.logging.Logger") == 0) {
                fullClassName = logInfo.stackTraceElement[i+1].getClassName();
                break;
            }
        }

        StringBuilder logMsg = new StringBuilder("[T="+logInfo.threadId+"] ");
        logMsg.append(logInfo.msg);

        writeConsole(logInfo.level, fullClassName,logMsg.toString() );

        //Check if build is debug or not
        if(isDebugBuild()){
            if (mFileLogLevelFilter <= logInfo.level) {
                writeLogFile(fullClassName, logMsg.toString());
            }
        }

    }

    void writeConsole(int level, String label, String msg) {

        switch (level)
        {
            case Log.ASSERT:
            {
                Log.wtf(label, msg);
                break;
            }
            case Log.ERROR:
            {
                Log.e(label, msg);
                break;
            }
            case Log.INFO:
            {
                Log.i(label, msg);
                break;
            }
            case Log.DEBUG:
            {
                Log.d(label, msg);
                break;
            }
            case Log.VERBOSE:
            {
                Log.v(label, msg);
                break;
            }
            case Log.WARN:
            {
                Log.w(label, msg);
                break;
            }
        }

    }

    void writeLogFile(String label, String text) {


        if (!mLogFolder.isDirectory()) {
            Log.e(TAG,mLogFolder + " is not a folder");
            return;
        }

        if (!mLogFolder.canWrite()) {
            Log.e(TAG,mLogFolder + " is not a writable");
            return;
        }

        //get the file
        File logFile = new File(filePath + "/log.txt");
        //if it does not exist
        if (!logFile.exists()) {
            try {
                //create it!
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            //otherwise!
            //if its been more than @LAST_MODIFIED_LENGTH minutes and the size of the file is more than FILE_SIZE_MAX
            if (new Date().getTime() - logFile.lastModified() > LAST_MODIFIED_LENGTH && logFile.length() >= FILE_SIZE_MAX) {
                //rename the file
                logFile.renameTo(new File(filePath + "/log.txt.old"));
                //set up a new file
                logFile = new File(filePath + "/log.txt");
                //delete it just in case it already exists
                logFile.delete();
                try {
                    //create the new file
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        try {
            //Now, we add new info to the file
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            Date now = new Date();
            Date lastM = new Date(logFile.lastModified());
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTime(lastM);
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTime(now);
            int nowDay = nowCal.get(Calendar.DAY_OF_YEAR);
            int lastMDay = lastCal.get(Calendar.DAY_OF_YEAR);
            if (lastMDay < nowDay && now.after(lastM)) {
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setTimeZone(TimeZone.getDefault());
                buf.newLine();
                buf.append("---------------------------------").append(df.format(now)).append("---------------------------------------");
                buf.newLine();
            }
            buf.append(label +": ");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private  boolean isDebugBuild(){
        PackageInfo pinfo;
        try {
            pinfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String versionName =  pinfo.versionName;
            if(BuildConfig.DEBUG || versionName.equalsIgnoreCase("1.0.0")){
                return  true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,"Error fetching versionName");
        }

        return false;

    }

    String rxL(SetupLogging.LogInfo logInfo, RxLogger clazz) {
        showCallingMethod = showCallingMethod || showMethodChain;
        //use "send" if you want to get to the RxBus send method but not including it
        //use "rxL" if you want to include the RxLogger2 in the trace
        String methodEnd = "rxL";
        //the main message to be logged
        String logged = logInfo.msg;
        //the arrow for the stack trace
        String arrow = "═▷\t";
        //if we want to show the calling methods
        if (showMethodChain || showCallingMethod) {
            //the stack trace
            StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
            //the location of the stack
            StringBuilder location = new StringBuilder("\n");
            //lets go through the stack
            for (int i = stackTraceElement.length - 1; i >= 0; i--) {
                //until we get to this method
                if (stackTraceElement[i].getMethodName().compareTo(methodEnd) == 0) {
                    break;
                }
                //get the full class name
                String fullClassName = stackTraceElement[i].getClassName();
                //only add method if its apart of the crestron modules (for now)
                if (fullClassName.contains("crestron")) {
                    //get the method name
                    String methodName = stackTraceElement[i].getMethodName();
                    //get the file name
                    String fileName = stackTraceElement[i].getFileName();
                    //get the line number
                    String lineNumber = String.valueOf(stackTraceElement[i].getLineNumber());
                    //add this to location in a format where we can click on the number in the console
                    location.append(fullClassName)
                            .append(".").append(methodName)
                            .append("(").append(fileName)
                            .append(":").append(lineNumber).append(")");
                    //if the next method is not rxL and i is not 0
                    if (stackTraceElement[i - 1].getMethodName().compareTo(methodEnd) != 0 && i != 0) {
                        //if showCallingMethod (which will only show the very top) is true
                        //and showMethodChain (which shows all method calls up to this one) is false
                        if (showCallingMethod && !showMethodChain) {
                            //stop right here
                            break;
                        } else if (showMethodChain) {
                            //if there are more calls in the chain, get ready to add more
                            char typeOfArrow;
                            if (stackTraceElement[i - 2].getMethodName().compareTo(methodEnd) != 0)
                                typeOfArrow = '╠'; //middle arrow
                            else
                                typeOfArrow = '╚'; //ending arrow
                            location.append("\n\t").append(typeOfArrow).append(arrow);
                        }
                    }
                }
            }
            //add the location to what we will be logging
            logged += location;
        }

        //get the current timezone of the device
        TimeZone tz = TimeZone.getDefault();
        //set up the date formatter
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSZ");
        //set the timezone of the formatter
        df.setTimeZone(tz);
        //format the current time
        String nowAsISO = df.format(new Date());
        //if the log is being logged into the file, don't print it (This can be changed)
      /*  if (clazz instanceof RxLoggerConsole) {
            Logger.println(logInfo.level.level, nowAsISO, logged);
        }*/
        //get the letter of the log
        //String logLevel = logInfo.level.letter;
        //return the log in a formatted way
        return nowAsISO + ": " +  "/" + logged;
    }

    //---------------------------------EVERYTHING BELOW WORKS FINE----------------------------------

    static String log(String msg, int level) {

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("log") == 0) {
                currentIndex = i + 1;
                break;
            }
        }
        currentIndex++;

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String fileName = stackTraceElement[currentIndex].getFileName();
        String lineNumber = String.valueOf(stackTraceElement[currentIndex].getLineNumber());
        String logged = msg + "\tat " + fullClassName + "." + methodName + "(" + fileName + ":" + lineNumber + ")";

        TimeZone tz = TimeZone.getDefault();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSZ");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        Log.println(level, nowAsISO, logged);

        String logLevel = Objects.requireNonNull(Logging.fromLevel(level)).letter;

        return nowAsISO + ": " + logLevel + "/" + logged;

    }


}

package com.crestron.logging;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.crestron.mobile.android.common.RxBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoggingTest {
    CLog cl;
    @Before
    public void setup() {
        if (BuildConfig.DEBUG) {
            cl = new RxLoggerConsole();
        } else {
            cl = new RxLoggerFile("sdcard/");
        }
    }

    public void saveAndClose(String data, FileOutputStream fos) throws IOException {
        fos.write(data.getBytes());
        fos.close();
    }

    @Test
    public void logTest() {

        CLog.w("---------------------------------");
        CLog.wtf("What a Terrible Failure");
        CLog.e("Error");
        CLog.v("Verbose");
        CLog.d("Debug");
        CLog.i("Info");
        CLog.w("Warning");
        CLog.a("Assert");
        CLog.a("Assert");
        CLog.w("Warning");
        CLog.i("Info");
        CLog.d("Debug");
        CLog.v("Verbose");
        CLog.e("Error");
        CLog.wtf("What a Terrible Failure");
        CLog.w("---------------------------------");
        cl.wFile("---------------------------------");
        cl.wtfFile("What a Terrible Failure");
        cl.eFile("Error");
        cl.vFile("Verbose");
        cl.dFile("Debug");
        cl.iFile("Info");
        cl.wFile("Warning");
        cl.aFile("Assert");
        cl.aFile("Assert");
        cl.wFile("Warning");
        cl.iFile("Info");
        cl.dFile("Debug");
        cl.vFile("Verbose");
        cl.eFile("Error");
        cl.wtfFile("What a Terrible Failure");
        cl.wFile("---------------------------------");

        CLog.setShowCallingMethod(true);
        CLog.setShowMethodChain(true);

        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Logging.Critical));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Log.WARN));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Logging.Debug));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Log.INFO));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Logging.Verbose));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Log.ERROR));

        CLog.setShowCallingMethod(false);
        CLog.setShowMethodChain(false);

        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method false", Log.ERROR));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method false", Logging.Debug));

        CLog.setShowCallingMethod(true);
        CLog.setShowMethodChain(false);

        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain false and calling method true", Logging.Info));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain false and calling method true", Logging.Verbose));

        CLog.setShowCallingMethod(false);
        CLog.setShowMethodChain(true);

        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain true and calling method false", Logging.Critical));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain true and calling method false", Logging.Warning));

        CLog.setShowCallingMethod(true);
        CLog.setShowMethodChain(true);

        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Logging.Critical));
        RxBus.INSTANCE.send(new SetupLogging.LogInfo("With method chain and calling method true", Logging.Warning));

    }

}

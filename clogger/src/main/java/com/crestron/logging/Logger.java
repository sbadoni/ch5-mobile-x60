package com.crestron.logging;

import android.content.Context;
import android.util.Log;

import com.crestron.mobile.android.common.RxBus;

public class Logger {

    private static RxLogger rxLogger = null;
    private static final String FILE_LOG_PATH = "";

    public static void init(Context context) {
        rxLogger = new RxLogger(context,context.getFilesDir().getPath());
        rxLogger.init();
    }

    public static void init(Context context, int logLevelFilter) {
        rxLogger = new RxLogger(context,context.getFilesDir().getPath());
        rxLogger.init();
        rxLogger.setFileLogLevelFilter(logLevelFilter);
    }

    public static void setLogLevel(int logLevelFilter) {
        if (rxLogger != null)
            rxLogger.setFileLogLevelFilter(logLevelFilter);
    }

    public static void Info(String msg) {
        SetupLogging.LogInfo setupLogging = new SetupLogging.LogInfo(msg, Log.INFO);
        RxBus.INSTANCE.send(setupLogging);
    }

    public static void Warning(String msg) {
        SetupLogging.LogInfo setupLogging = new SetupLogging.LogInfo(msg, Log.WARN);
        RxBus.INSTANCE.send(setupLogging);
    }

    public static void Verbose(String msg) {
        SetupLogging.LogInfo setupLogging = new SetupLogging.LogInfo(msg, Log.VERBOSE);
        RxBus.INSTANCE.send(setupLogging);
    }

    public static void Debug(String msg) {
        SetupLogging.LogInfo setupLogging = new SetupLogging.LogInfo(msg, Log.DEBUG);
        RxBus.INSTANCE.send(setupLogging);
    }

    public static void Error(String msg) {
        SetupLogging.LogInfo setupLogging = new SetupLogging.LogInfo(msg, Log.ERROR);
        RxBus.INSTANCE.send(setupLogging);
    }

    public static void Critical(String msg) {
        SetupLogging.LogInfo setupLogging = new SetupLogging.LogInfo(msg, Log.ASSERT);
        RxBus.INSTANCE.send(setupLogging);
    }
}

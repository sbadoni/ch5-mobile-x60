package com.crestron.logging;

import android.annotation.SuppressLint;

import com.crestron.mobile.contract.UseCase;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SetupLogging extends UseCase {

    public SetupLogging(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }

    public static class LogInfo {
        String msg;
        int level;
        String dateTime;
        StackTraceElement[] stackTraceElement;
        long threadId = 0;

        /**
         * @param msg   the message
         * @param level the log level {@link Logging}
         */
        public LogInfo(String msg, int level) {
            stackTraceElement = Thread.currentThread().getStackTrace();
            threadId = android.os.Process.myTid();
            SetTime();
            this.msg = msg;
            this.level = level;

        }

        private void SetTime()
        {
            TimeZone tz = TimeZone.getDefault();
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            df.setTimeZone(tz);
            dateTime = df.format(new Date());
        }
    }
}


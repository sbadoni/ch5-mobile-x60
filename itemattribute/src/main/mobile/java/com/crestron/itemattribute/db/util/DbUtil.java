package com.crestron.itemattribute.db.util;

import android.os.Build;

/**
 * Helper class for database interactions.
 */
public class DbUtil {

    /**
     * Determines whether current looper is main app or test.
     *
     * @return boolean
     */
    public static boolean isTestLooper() {
        String buildFingerprint = Build.FINGERPRINT;
        return "robolectric".equals(buildFingerprint);
    }
}

package com.crestron.logging;

public enum Logging {

    Info(android.util.Log.INFO, "I"),
    Warning(android.util.Log.WARN, "W"),
    Verbose(android.util.Log.VERBOSE, "V"),
    Debug(android.util.Log.DEBUG, "D"),
    Error(android.util.Log.ERROR, "E"),
    Critical(android.util.Log.ASSERT, "C");

    int level;
    String letter;

    Logging(int level, String letter) {
        this.level = level;
        this.letter = letter;
    }

    /**
     * to get a {@link Logging} object from its level
     *
     * @param level the level
     * @return the {@link Logging} Object
     */
    public static Logging fromLevel(int level) {
        for (Logging sl : Logging.values()) {
            if (sl.level == level) {
                return sl;
            }
        }
        return null;
    }

    /**
     * to get a {@link Logging} object from its lettter
     *
     * @param letter the letter
     * @return the {@link Logging} object
     */
    public static Logging fromLetter(String letter) {
        for (Logging sl : Logging.values()) {
            if (sl.letter.equals(letter)) {
                return sl;
            }
        }
        return null;
    }

}

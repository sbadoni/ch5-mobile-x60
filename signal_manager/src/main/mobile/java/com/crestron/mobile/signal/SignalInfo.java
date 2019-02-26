package com.crestron.mobile.signal;

/**
 * <h1>SignalInfo class </h1>
 * <p>
 * Represents a user interface signal
 * Does not contain any signal value
 *
 * @author Colm Coady
 * @version 1.0
 */


public class SignalInfo {
    /**
     * Signal name
     */
    private String signalName;

    /**
     * Join id
     */
    private short joinId;

    /**
     * Control join id
     */
    private short controlJoinId;

    public SignalInfo() {

    }

    /**
     * @return Signal name
     */
    public String getSignalName() {
        return signalName;
    }

    /**
     * @return Join name
     */
    short getJoinId() {
        return joinId;
    }

    /**
     * @return Control Join ID
     */
    short getControlJoinId() {
        return controlJoinId;
    }
}


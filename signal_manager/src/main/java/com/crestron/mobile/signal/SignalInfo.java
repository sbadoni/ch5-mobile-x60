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
    private int joinId;

    /**
     * Control join id
     */
    private short controlJoinId;

    /**
     * type
     */
    private String type;

    /**
     * direction
     */
    private String direction;

    public SignalInfo() {

    }

    /**
     * @return Signal name
     */
    public String getSignalName() {
        return signalName;
    }

    /**
     * @param signalName
     */
    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    /**
     * @return Join name
     */
    public int getJoinId() {
        return joinId;
    }

    /**
     * @param joinId
     */
    public void setJoinId(int joinId) {
        this.joinId = joinId;
    }

    /**
     * @return Control Join ID
     */
    short getControlJoinId() {
        return controlJoinId;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * @return
     */
    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return " signalName: " + signalName + " JoinId: " + joinId + " type: " + type + " direction: " + direction;
    }
}


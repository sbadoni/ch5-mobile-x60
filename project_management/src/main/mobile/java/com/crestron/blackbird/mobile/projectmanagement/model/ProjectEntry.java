package com.crestron.blackbird.mobile.projectmanagement.model;

/**
 * Class used to describe the attributes of a downloaded/saved project.
 */
public class ProjectEntry {
    private String hash;
    private String lastAccessedDate;
    private String controlSystemID;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getLastAccessedDate() {
        return lastAccessedDate;
    }

    public void setLastAccessedDate(String lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }

    public String getControlSystemID() {
        return controlSystemID;
    }

    public void setControlSystemID(String controlSystemID) {
        this.controlSystemID = controlSystemID;
    }
}

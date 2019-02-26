package com.crestron.mobile.projectmanagement.model;

/**
 * Java bean to send for project download.
 */
public class ProjectForDownload {
    private String controlSysID;
    private String hostName;
    private String userName;
    private String userPassword;
    private String projectName;
    private boolean useSSL;
    private int httpPort;

    public String getControlSysID() {
        return controlSysID;
    }

    public void setControlSysID(String controlSysID) {
        this.controlSysID = controlSysID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostname) {
        this.hostName = hostname;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int webPortNumber) {
        this.httpPort = webPortNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

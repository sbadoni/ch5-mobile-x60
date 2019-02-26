package com.crestron.mobile.cssettings.model;

import android.support.annotation.NonNull;

/**
 * <h1>ControlSystemEntry Case</h1>
 * <p>
 * Class used to describe the attributes of a control system
 *
 * @author Colm Coady
 * @version 1.0
 */

public class ControlSystemEntry {
    /**
     * Project name that is hosted on the control system.
     */
    private String projectName;

    /**
     * Control System ID to uniquely identify control system
     */
    private String controlSystemID;

    /**
     * User friendly name of control system
     */
    @NonNull
    private String friendlyName = "";

    private boolean useLocalProject;

    /**
     * IP ID
     * Note ipid is in decimal format - should be converted to/from hex for display and editing
     */
    private int ipid;

    /**
     * Use SSL for connection
     */
    private boolean useSSL;

    /**
     * Control system primary ip address/hostname
     */
    private String hostName1;

    /**
     * Control system primary http port
     */
    private int httpPort1;

    /**
     * Control system primary cip port
     */
    private int cipPort1;

    /**
     * Secondary control system address available
     * If address2Enabled is false, the secondary address fields are ignored
     */
    private boolean address2Enabled;

    /**
     * Control system primary ip address/hostname
     */
    private String hostName2;

    /**
     * Control system secondary http port
     */
    private int httpPort2;

    /**
     * Control system secondary cip port
     */
    private int cipPort2;

    private boolean useNotificationWidget;

    /**
     * Control system  user name
     */
    private String userName = "";

    /**
     * Control system  user name
     */
    private String userPassword = "";

    /**
     * Control system  user name
     */
    private String programInstanceId;


    /**
     * @return control system ID
     */
    public String getControlSystemID() {
        return controlSystemID;
    }

    /**
     * @param control system ID
     */
    public void setControlSystemID(String controlSystemID) {
        this.controlSystemID = controlSystemID;
    }

    /**
     * @return control system friendly name
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * @param friendlyName String - system friendly name
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public boolean isUseLocalProject() {
        return useLocalProject;
    }

    public void setUseLocalProject(boolean useLocalProject) {
        this.useLocalProject = useLocalProject;
    }

    /**
     * @return control system Id
     */
    public int getIpid() {
        return ipid;
    }

    /**
     * @param ip Id
     */
    public void setIpid(int ipid) {
        this.ipid = ipid;
    }

    /**
     * @return is SSL set
     */
    public boolean isUseSSL() {
        return useSSL;
    }

    /**
     * @param set SSL
     */
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    /**
     * @return first Host Name
     */
    public String getHostName1() {
        return hostName1;
    }

    /**
     * @param set first Host Name
     */
    public void setHostName1(String hostName1) {
        this.hostName1 = hostName1;
    }

    /**
     * @return first Http Port
     */
    public int getHttpPort1() {
        return httpPort1;
    }

    /**
     * @param set first Http Port
     */
    public void setHttpPort1(int httpPort1) {
        this.httpPort1 = httpPort1;
    }

    /**
     * @return first Cip Port
     */
    public int getCipPort1() {
        return cipPort1;
    }

    /**
     * @param set first cip Port
     */
    public void setCipPort1(int cipPort1) {
        this.cipPort1 = cipPort1;
    }

    /**
     * @return is Second Address enabled
     */
    public boolean isAddress2Enabled() {
        return address2Enabled;
    }

    /**
     * @param set Second Address enabled
     */
    public void setAddress2Enabled(boolean address2Enabled) {
        this.address2Enabled = address2Enabled;
    }

    /**
     * @param set second Host Name
     */
    public String getHostName2() {
        return hostName2;
    }

    /**
     * @return second Host Name
     */
    public void setHostName2(String hostName2) {
        this.hostName2 = hostName2;
    }

    public int getHttpPort2() {
        return httpPort2;
    }

    /**
     * @param second Http Port
     */
    public void setHttpPort2(int httpPort2) {
        this.httpPort2 = httpPort2;
    }

    /**
     * @return get second Cip Port
     */
    public int getCipPort2() {
        return cipPort2;
    }

    /**
     * @param set second Http Port
     */
    public void setCipPort2(int cipPort2) {
        this.cipPort2 = cipPort2;
    }

    public boolean isUseNotificationWidget() {
        return useNotificationWidget;
    }

    public void setUseNotificationWidget(boolean useNotificationWidget) {
        this.useNotificationWidget = useNotificationWidget;
    }

    /**
     * @return get user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param set user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }


    /**
     * @return get user password
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * @param set user password
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * @return get program instance id
     */
    public String getProgramInstanceId() {
        return programInstanceId;
    }

    /**
     * @param programInstanceId String - program instance id
     */
    public void setProgramInstanceId(String programInstanceId) {
        this.programInstanceId = programInstanceId;
    }

    public String getProjectName() {
        return projectName;
    }


}

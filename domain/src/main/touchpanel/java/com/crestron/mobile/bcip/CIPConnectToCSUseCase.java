package com.crestron.mobile.bcip;

/**
 * Created by ccoady on 5/15/2018.
 */

public class CIPConnectToCSUseCase extends CIPOpUseCase {

    public enum Status {
        UNKNOWN, CONNECTED, NOTCONNECTED;
        private String StatusMessage;

        public String getStatusMessage() {
            return StatusMessage;
        }

        public void setStatusMessage(String StatusMessage) {
            this.StatusMessage = StatusMessage;
        }
    }


    public CIPConnectToCSUseCase() {
        super("ConnectionToIplink", 0);
    }

    /**
     * CIPConnectToCSUseCase request inner class
     * <p>
     * Contains the control system entry id
     */
    public class Request extends CIPOpUseCase.Request {
        int ipLinkPort;
        boolean enableIPlinkLogging = false;

        public Request(int ipLinkPort, boolean enableIPlinkLogging) {
            this.ipLinkPort = ipLinkPort;
            this.enableIPlinkLogging = enableIPlinkLogging;
        }

        public void setEnableIPlinkLogging(boolean enableIPlinkLogging) {
            this.enableIPlinkLogging = enableIPlinkLogging;
        }

        public boolean isEnableIPlinkLogging() {
            return enableIPlinkLogging;
        }

        public void setIpLinkPort(int ipLinkPort) {
            this.ipLinkPort = ipLinkPort;
        }

        public int getIpLinkPort() {
            return ipLinkPort;
        }
    }

    /**
     * CIPConnectToCSUseCase response inner class
     * <p>
     * Contains the response to control system connection
     */
    public class Response extends CIPOpUseCase.Response {

        Status responseStatus;

        public Response() {
            responseStatus = Status.UNKNOWN;
        }


        public Response(Status responseStatus) {
            this.responseStatus = responseStatus;
        }

        public Status getResponseStatus() {
            return responseStatus;
        }

        public void setResponseStatus(Status responseStatus) {
            this.responseStatus = responseStatus;
        }


    }
}

package com.crestron.mobile.bcip;

/**
 * Created by ccoady on 5/15/2018.
 */

public class CIPConnectToIPLinkUseCase extends CIPOpUseCase {

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


    public CIPConnectToIPLinkUseCase() {
        super("ConnectToIPLink", 0);
    }

    /**
     * CIPConnectToIPLinkUseCase request inner class
     * <p>
     * Contains the control system entry id
     */
    public class Request extends CIPOpUseCase.Request {
        private int ipLinkPort;
        private boolean ipLinkLoggingEnabled;
        public static final int DEFAULT_IPLINK_PORT = 42872;

        public Request() {
            ipLinkPort = DEFAULT_IPLINK_PORT;
            ipLinkLoggingEnabled = true;
        }


        public boolean isIpLinkLoggingEnabled() {
            return ipLinkLoggingEnabled;
        }

        public void setIpLinkLoggingEnabled(boolean ipLinkLoggingEnabled) {
            this.ipLinkLoggingEnabled = ipLinkLoggingEnabled;
        }


       public void setIPLinkPort(int ipLinkPort) {
                this.ipLinkPort = ipLinkPort;
            }

        public int getIPLinkPort() {
            return ipLinkPort;
        }


       }

    /**
     * CIPConnectToIPLinkUseCase response inner class
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

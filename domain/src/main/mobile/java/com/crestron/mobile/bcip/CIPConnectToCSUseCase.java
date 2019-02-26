package com.crestron.mobile.bcip;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;

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
        super("ConnectToControlSystem", 0);
    }

    /**
     * CIPConnectToCSUseCase request inner class
     * <p>
     * Contains the control system entry id
     */
    public class Request extends CIPOpUseCase.Request {
        private ControlSystemEntry controlSystemEntry;

        public Request() {

        }

        public Request(ControlSystemEntry controlSystemEntry) {
            this.controlSystemEntry = controlSystemEntry;

        }

        public void setControlSystemEntry(ControlSystemEntry controlSystemEntry) {
            this.controlSystemEntry = controlSystemEntry;

        }

        public ControlSystemEntry getControlSystemEntry() {
            return this.controlSystemEntry;
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

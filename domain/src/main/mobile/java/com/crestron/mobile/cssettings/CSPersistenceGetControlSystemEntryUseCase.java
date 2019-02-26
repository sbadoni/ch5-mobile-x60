package com.crestron.mobile.cssettings;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;

/**
 * <h1>CSPersistenceGetControlSystemEntryUseCase class </h1>
 * <p>
 * Used to retrieve a control system entries
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CSPersistenceGetControlSystemEntryUseCase extends CSPersistenceControlSystemEntryOpUseCase {


    public CSPersistenceGetControlSystemEntryUseCase() {
        super("CSPersistenceGetControlSystemsEntry");
    }

    /**
     * CSPersistenceUpdateControlSystemEntryUseCase request inner class
     * <p>
     * Contains the control system entry id
     */
    public class Request extends CSPersistenceControlSystemEntryOpUseCase.Request {
        private String controlSystemID;

        public Request() {
        }

        public Request(String controlSystemID) {
            this.controlSystemID = controlSystemID;
        }

        public String getControlSystemID() {
            return controlSystemID;
        }

        public void setControlSystemID(String controlSystemID) {
            this.controlSystemID = controlSystemID;
        }

    }

    /**
     * CSPersistenceGetControlSystemEntryUseCase response inner class
     * <p>
     * Contains the requested control system entry
     */
    public class Response extends CSPersistenceControlSystemEntryOpUseCase.Response {

        ControlSystemEntry caControlSystems;

        public Response() {
            this.responseStatus = Status.STARTED;
        }

        public Response(ControlSystemEntry caControlSystems) {
            this.caControlSystems = caControlSystems;
            this.responseStatus = Status.STARTED;
        }

        public ControlSystemEntry getCaControlSystems() {
            return caControlSystems;
        }

        public void setCaControlSystems(ControlSystemEntry caControlSystems) {
            this.caControlSystems = caControlSystems;
        }


    }
}

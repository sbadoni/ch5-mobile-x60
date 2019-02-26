package com.crestron.mobile.cssettings;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;

import java.util.List;

/**
 * <h1>CSPersistenceCreateControlSystemEntryUseCase class </h1>
 * <p>
 * Used to add one or more control system entries
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CSPersistenceCreateControlSystemEntryUseCase extends CSPersistenceControlSystemEntryOpUseCase {


    public CSPersistenceCreateControlSystemEntryUseCase() {

        super("CSPersistenceAddControlSystemEntry");
    }

    /**
     * CSPersistenceCreateControlSystemEntryUseCase request inner class
     * <p>
     * Contains on array list of control system entries to be added
     */
    public class Request extends CSPersistenceControlSystemEntryOpUseCase.Request {

        private List<ControlSystemEntry> caControlSystems;

        public Request() {
        }

        public Request(List<ControlSystemEntry> caControlSystems) {
            this.caControlSystems = caControlSystems;
        }

        public List<ControlSystemEntry> getCaControlSystems() {
            return caControlSystems;
        }

        public void setCaControlSystems(List<ControlSystemEntry> caControlSystems) {
            this.caControlSystems = caControlSystems;
        }
    }

    /**
     * CSPersistenceCreateControlSystemEntryUseCase response inner class
     * <p>
     * Contains on array list of control system entries added
     */
    public class Response extends CSPersistenceControlSystemEntryOpUseCase.Response {

        private List<ControlSystemEntry> caControlSystems;

        public Response() {
            this.responseStatus = Status.STARTED;
        }

        public Response(List<ControlSystemEntry> caControlSystems) {
            this.caControlSystems = caControlSystems;
            this.responseStatus = Status.STARTED;
        }

        public List<ControlSystemEntry> getCaControlSystems() {
            return caControlSystems;
        }

        public void setCaControlSystems(List<ControlSystemEntry> caControlSystems) {
            this.caControlSystems = caControlSystems;
        }
    }
}

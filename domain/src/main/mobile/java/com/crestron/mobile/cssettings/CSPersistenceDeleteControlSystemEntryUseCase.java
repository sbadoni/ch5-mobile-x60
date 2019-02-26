package com.crestron.mobile.cssettings;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;

import java.util.List;

/**
 * <h1>CSPersistenceDeleteControlSystemEntryUseCase class </h1>
 * <p>
 * Used for deletion of one or more control system entries
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CSPersistenceDeleteControlSystemEntryUseCase extends CSPersistenceControlSystemEntryOpUseCase {
    private List<ControlSystemEntry> caControlSystems;

    public CSPersistenceDeleteControlSystemEntryUseCase() {
        super("CSPersistenceDeleteControlSystemEntry");
    }

    /**
     * CSPersistenceDeleteControlSystemEntryUseCase request inner class
     * <p>
     * Contains on array list of control system entries to be deleted
     */
    public class Request extends CSPersistenceControlSystemEntryOpUseCase.Request {

        public Request() {
        }

        public Request(List<ControlSystemEntry> caControlSystems) {
            CSPersistenceDeleteControlSystemEntryUseCase.this.caControlSystems = caControlSystems;
        }

        public List<ControlSystemEntry> getCaControlSystems() {
            return caControlSystems;
        }

        public void setCaControlSystems(List<ControlSystemEntry> caControlSystems) {
            CSPersistenceDeleteControlSystemEntryUseCase.this.caControlSystems = caControlSystems;
        }
    }

    /**
     * CSPersistenceDeleteControlSystemEntryUseCase response inner class
     */
    public class Response extends CSPersistenceControlSystemEntryOpUseCase.Response {

        public Response() {
            this.responseStatus = Status.STARTED;
        }

        public List<ControlSystemEntry> getEntriesToDelete() {
            return caControlSystems;
        }

    }
}

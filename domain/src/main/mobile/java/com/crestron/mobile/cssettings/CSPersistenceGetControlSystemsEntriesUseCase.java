package com.crestron.mobile.cssettings;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;

import java.util.List;

/**
 * <h1>CSPersistenceGetControlSystemsEntriesUseCase class </h1>
 * <p>
 * Used to retrieve a control system entries
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CSPersistenceGetControlSystemsEntriesUseCase extends CSPersistenceControlSystemEntryOpUseCase {
    public CSPersistenceGetControlSystemsEntriesUseCase() {
        super("CSPersistenceGetControlSystemsEntries");
    }

    /**
     * CSPersistenceGetControlSystemsEntriesUseCase request inner class
     */
    public class Request extends CSPersistenceControlSystemEntryOpUseCase.Request {

    }

    /**
     * CSPersistenceGetControlSystemsEntriesUseCase response inner class
     * <p>
     * Contains an array list of control system entries
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


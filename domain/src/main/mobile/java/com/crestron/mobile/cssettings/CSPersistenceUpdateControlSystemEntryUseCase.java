package com.crestron.mobile.cssettings;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;

import java.util.List;

/**
 * <h1>CSPersistenceUpdateControlSystemEntryUseCase class </h1>
 * <p>
 * Used to update one or more control system entries
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CSPersistenceUpdateControlSystemEntryUseCase extends CSPersistenceControlSystemEntryOpUseCase {
    public CSPersistenceUpdateControlSystemEntryUseCase() {
        super("CSPersistenceEditControlSystemEntry");
    }

    /**
     * CSPersistenceUpdateControlSystemEntryUseCase request inner class
     * <p>
     * Contains on array list of control system entries to be updated
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
     * CSPersistenceUpdateControlSystemEntryUseCase response inner class
     */
    public class Response extends CSPersistenceControlSystemEntryOpUseCase.Response {


    }
}

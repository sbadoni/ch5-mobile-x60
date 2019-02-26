package com.crestron.mobile.bcip;

import com.crestron.mobile.contract.UseCase;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;

/**
 * Use case to signal when bcip connection to a control system established successfully.
 */
public class ConnectionEstablishedUseCase extends UseCase {
    private ControlSystemEntry csEntry;

    public ConnectionEstablishedUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }

    public ControlSystemEntry getCsEntry() {
        return csEntry;
    }

    public void setCsEntry(ControlSystemEntry csEntry) {
        this.csEntry = csEntry;
    }

}

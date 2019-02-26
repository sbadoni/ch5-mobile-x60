package com.crestron.mobile.reservedjoin;

import com.crestron.mobile.contract.UseCase;

public class Csig_Boolean_UseCase extends UseCase {
    /**
     * Digital value
     */
    private boolean value;

    /**
     * type boolean
     *
     * @return
     */
    public boolean getValue() {
        return value;
    }

    /**
     * type boolean
     *
     * @param value
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * @param useCaseName
     * @param useCaseId
     */
    public Csig_Boolean_UseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}

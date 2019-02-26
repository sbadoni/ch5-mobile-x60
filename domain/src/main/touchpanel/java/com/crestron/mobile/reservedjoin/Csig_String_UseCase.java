package com.crestron.mobile.reservedjoin;

import com.crestron.mobile.contract.UseCase;

public class Csig_String_UseCase extends UseCase {

    /**
     * Serial value
     */
    private String value;

    /**
     * @return Serial value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param  value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @param useCaseName
     * @param useCaseId
     */
    public Csig_String_UseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}

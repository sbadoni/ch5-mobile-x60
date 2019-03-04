package com.crestron.mobile.bcip.reservedjoin;

import com.crestron.mobile.contract.UseCase;

import org.jetbrains.annotations.NotNull;

public class Csig_String_UseCaseResp extends UseCase {

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

    public Csig_String_UseCaseResp(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}

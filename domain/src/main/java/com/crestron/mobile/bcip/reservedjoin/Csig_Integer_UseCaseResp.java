package com.crestron.mobile.bcip.reservedjoin;

import com.crestron.mobile.contract.UseCase;

import org.jetbrains.annotations.NotNull;

public class Csig_Integer_UseCaseResp extends UseCase {

    /**
     * Analog value
     */
    private int value;

    /**
     * @return int
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @param useCaseName
     * @param useCaseId
     */

    public Csig_Integer_UseCaseResp(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}
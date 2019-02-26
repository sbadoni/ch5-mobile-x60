package com.crestron.mobile.reservedjoin;

import com.crestron.mobile.contract.UseCase;

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
    public Csig_Integer_UseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}

package com.crestron.mobile.bcip.reservedjoin;

import com.crestron.mobile.contract.UseCase;

import org.jetbrains.annotations.NotNull;

public class Csig_Boolean_UseCaseResp extends UseCase{

    /**
     * Digital value
     */
    private boolean value;

    /**
     * Whether the join is a toggle
     */

    private boolean toggle;

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

    /**
     * return toggle value
     */
    public boolean getToggle() {
        return toggle;
    }


    /**
     * @param toggle value
     */
    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }


    public Csig_Boolean_UseCaseResp(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}

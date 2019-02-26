package com.crestron.mobile.bcip.reservedjoin.response;

import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCase;

public class ReservedBooleanResponseUseCase extends Csig_Boolean_UseCase {

    /**
     * Whether the join is a toggle
     */

    private boolean toggle;

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


    public ReservedBooleanResponseUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }
}

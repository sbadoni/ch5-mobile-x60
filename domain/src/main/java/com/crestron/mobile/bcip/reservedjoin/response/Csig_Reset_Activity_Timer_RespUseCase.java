package com.crestron.mobile.bcip.reservedjoin.response;

import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCaseResp;

import org.jetbrains.annotations.NotNull;

public class Csig_Reset_Activity_Timer_RespUseCase extends Csig_Boolean_UseCaseResp {

    public Csig_Reset_Activity_Timer_RespUseCase(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }

    public Csig_Reset_Activity_Timer_RespUseCase() {
        super("Reset_Activity_Timer", 29727);
    }
}

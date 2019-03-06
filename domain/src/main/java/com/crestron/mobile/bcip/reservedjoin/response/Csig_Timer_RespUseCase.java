package com.crestron.mobile.bcip.reservedjoin.response;


import com.crestron.mobile.bcip.reservedjoin.Csig_Integer_UseCaseResp;

import org.jetbrains.annotations.NotNull;

public class Csig_Timer_RespUseCase extends Csig_Integer_UseCaseResp {
    public Csig_Timer_RespUseCase(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }

    public Csig_Timer_RespUseCase() {
        super("Timer", 33300);
    }
}

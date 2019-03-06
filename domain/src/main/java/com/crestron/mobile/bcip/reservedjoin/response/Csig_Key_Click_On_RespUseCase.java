package com.crestron.mobile.bcip.reservedjoin.response;

import com.crestron.mobile.bcip.reservedjoin.Csig_Boolean_UseCaseResp;

import org.jetbrains.annotations.NotNull;

public class Csig_Key_Click_On_RespUseCase extends Csig_Boolean_UseCaseResp {

    public Csig_Key_Click_On_RespUseCase(@NotNull String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId);
    }

    public Csig_Key_Click_On_RespUseCase() {
        super("Key_Click_On", 29900);
    }
}

package com.crestron.mobile.bcip.reservedjoin.response;


import com.crestron.mobile.bcip.reservedjoin.Csig_String_UseCaseResp;

import org.jetbrains.annotations.NotNull;

public class Csig_URL_Path_RespUseCase extends Csig_String_UseCaseResp {
    public Csig_URL_Path_RespUseCase(@NotNull String useCaseName, int useCaseId) {
        super("URL_Path", 20000);
    }
}

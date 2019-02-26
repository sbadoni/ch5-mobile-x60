package com.crestron.mobile.bcip;

public class CIPObjectUseCaseResp extends CIPUseCase {

    String jsonString;

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }


    public CIPObjectUseCaseResp(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPObjectUseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }

    public CIPObjectUseCaseResp(String useCaseName) {
        super(useCaseName, 0, 0);
    }
}

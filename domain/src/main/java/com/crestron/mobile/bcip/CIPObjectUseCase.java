package com.crestron.mobile.bcip;

public class CIPObjectUseCase extends CIPUseCase {

    private String jsonString;

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getValue(){
        return jsonString;
    }


    public CIPObjectUseCase(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPObjectUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }

    public CIPObjectUseCase(String useCaseName) {
        super(useCaseName, 0, 0);
    }

}

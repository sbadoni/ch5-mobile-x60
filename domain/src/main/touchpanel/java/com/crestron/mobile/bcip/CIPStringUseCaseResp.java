package com.crestron.mobile.bcip;

/**
 * <h1>CIPStringUseCaseResp class </h1>
 * <p>
 * Represents a CIP serial join response
 *
 * @author Colm Coady
 * @version 1.0
 */
public class CIPStringUseCaseResp extends  CIPUseCase {

    /**
     * Serial value
     */
    private String value;

    /**
     * @return Serial value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param Serial value
     */
    public void setValue(String value) {
        this.value = value;
    }

    public CIPStringUseCaseResp(String useCaseName, int useCaseId, int controlJoinId, String value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }

    public CIPStringUseCaseResp(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPStringUseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }
}

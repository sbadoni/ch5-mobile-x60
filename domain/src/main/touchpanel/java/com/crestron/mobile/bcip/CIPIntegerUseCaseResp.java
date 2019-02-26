package com.crestron.mobile.bcip;

/**
 * <h1>CIPIntegerUseCaseResp class </h1>
 * <p>
 * Represents a CIP analog join response
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPIntegerUseCaseResp extends CIPUseCase {

    /**
     * Analog value
     */
    private int value;

    /**
     * @return Analog value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param Analog value
     */
    public void setValue(int value) {
        this.value = value;
    }

    public CIPIntegerUseCaseResp(String useCaseName, int useCaseId, int controlJoinId, int value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }

    public CIPIntegerUseCaseResp(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPIntegerUseCaseResp(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }
}

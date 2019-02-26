package com.crestron.mobile.bcip;

/**
 * <h1>CIPStringUseCase class </h1>
 * <p>
 * Represents a CIP serial join
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPStringUseCase extends CIPUseCase {

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

    public CIPStringUseCase(String useCaseName, int useCaseId, int controlJoinId, String value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }

    public CIPStringUseCase(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPStringUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }

    public CIPStringUseCase(String useCaseName) {
        super(useCaseName, 0, 0);
    }
}

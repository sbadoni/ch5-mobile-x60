package com.crestron.mobile.bcip;

/**
 * <h1>CIPBooleanUseCase class </h1>
 * <p>
 * Represents a CIP digital join
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPBooleanUseCase extends CIPUseCase {

    /**
     * Digital value
     */
    private boolean value;

    /**
     * @return Digital value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * @param Digital value
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    public CIPBooleanUseCase(String useCaseName, int useCaseId, int controlJoinId, boolean value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }

    public CIPBooleanUseCase(String useCaseName, int useCaseId, int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPBooleanUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }

    public CIPBooleanUseCase(String useCaseName) {
        super(useCaseName, 0, 0);
    }
}

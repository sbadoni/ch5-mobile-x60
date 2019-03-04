package com.crestron.mobile.bcip;

/**
 * <h1>CIPIntegerUseCase class </h1>
 * <p>
 * Represents a CIP analog join
 *
 * @author Colm Coady
 * @version 1.0
 */

public class CIPIntegerUseCase extends CIPUseCase {

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

    public CIPIntegerUseCase(String useCaseName, int useCaseId, int controlJoinId, int value) {
        super(useCaseName, useCaseId, controlJoinId);
        this.value = value;
    }

    public CIPIntegerUseCase(String useCaseName, int useCaseId,  int controlJoinId) {
        super(useCaseName, useCaseId, controlJoinId);
    }

    public CIPIntegerUseCase(String useCaseName, int useCaseId) {
        super(useCaseName, useCaseId, 0);
    }

    public CIPIntegerUseCase(String useCaseName) {
        super(useCaseName, 0, 0);
    }
}

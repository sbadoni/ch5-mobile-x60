package com.crestron.mobile.contract

/**
 * <h1>Generic Use Case</h1>
 *
 * Base UseCase class from which all use cases are derived from
 * All use cases implement the methods of this class.
 *
 * @author Colm Coady
 * @version 1.0
 */
abstract class UseCase(useCaseName: String, useCaseId: Int) {
    /**
     * Response Status enum
     * Typically used to set a status on the Response class
     */
    enum class Status {
        STARTED, FAILED, IN_PROGRESS, SUCCESS;

        var statusMessage: String? = null
    }

    /**
     * Use case name.
     */
    val useCaseName: String = useCaseName

    /**
     * Use case Id.
     */
    val useCaseId: Int = useCaseId

    /**
     * Returns a string representation of use case name and id.
     * @return Use case name and id string formatted as 'name: id'
     */
    override fun toString(): String{
        val retStr = "${useCaseName}: ${useCaseId}"
        return retStr
    }

}


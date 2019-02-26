package com.crestron.common.mobile.mocks;

/**
 * Definition of subjects that pyng supports.
 */
public class MockPyngSubject {
    // Declare the subject constants
    public static final int QUERY_LIGHTING_SCENES = 0;
    public static final int QUERY_LIGHTING_SCENES_BOGUS = 2;
    public static final int RECEIVE_LIGHTING_SCENES = 3;
    public static final int SET_PROGRESS_INDICATOR = 4;
    public static final int SHOW_ERROR = 5;
    public static final int RAISE_ROOM_LIGHT = 6;
    public static final int LOWER_ROOM_LIGHT = 7;
    public static final int ACTIVATE_LIGHTING_SCENE = 8;
    public static final int DEACTIVATE_LIGHTING_SCENE = 9;

    // Simulate random events
    public static final int PYNG_EVENT_10 = 10;
    public static final int PYNG_EVENT_11 = 11;
    public static final int PYNG_EVENT_12 = 12;
    public static final int PYNG_EVENT_13 = 13;
    public static final int PYNG_EVENT_14 = 14;
    public static final int PYNG_EVENT_15 = 15;
    public static final int PYNG_EVENT_16 = 16;
    public static final int PYNG_EVENT_17 = 17;

    // These are passed through the message bus
    private final int subjectId;
    private final String message;

    /**
     * Definition of use cases (i.e. subjects) that pass a subjectId id and message.
     *
     * @param subjectId Int- the id of the subject to subscribe to.
     * @param message   String - the message object that is delivered with this subject.
     */
    public MockPyngSubject(int subjectId, String message) {
        this.subjectId = subjectId;
        this.message = message;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getMessage() {
        return message;
    }

}

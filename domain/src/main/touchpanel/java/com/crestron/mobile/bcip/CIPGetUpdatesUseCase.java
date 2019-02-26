package com.crestron.mobile.bcip;

import com.crestron.mobile.contract.UseCase;

/**
 * Created by ccoady on 9/12/2018.
 */

public class CIPGetUpdatesUseCase extends UseCase {

    private boolean mUpdateFromControlSystem = false;

    /**
     * @return UpdateFromControlSystem - request an update from control system
     */
    public boolean getUpdateFromControlSystem() {
        return mUpdateFromControlSystem;
    }

    /**
     * @param UpdateFromControlSystem - request an update from control system
     */
    public void setUpdateFromControlSystem(boolean value) {
        this.mUpdateFromControlSystem = value;
    }

    public CIPGetUpdatesUseCase() {
        super("GetUpdates", 0);
    }

    public CIPGetUpdatesUseCase(boolean UpdateFromControlSystem ) {
        super("GetUpdates", 0);
        mUpdateFromControlSystem = UpdateFromControlSystem;
    }
}

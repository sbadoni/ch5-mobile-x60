package com.crestron.blackbird.mobile.cssettings.interactor;

import com.crestron.mobile.cssettings.model.ControlSystemEntry;
import com.crestron.itemattribute.db.tables.AttributeEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * Interface contract for interacting with the db from control_system_settings module.
 */
public interface DbInteractor {
    Maybe createOrUpdateSystemEntry(ControlSystemEntry entry);

    Completable deleteSystem(Long... controlSysIds);

    Maybe<List<AttributeEntity>> getAllSystemsWithInfo();

    Maybe<List<AttributeEntity>> getSystemById(Long controlSysId);

}

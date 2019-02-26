package com.crestron.blackbird.mobile.projectmanagement.interactor;

import com.crestron.blackbird.mobile.projectmanagement.model.ProjectEntry;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * Interface contract for interacting with the db from project_management module.
 */
public interface DbInteractor {
    Maybe createOrUpdateProjectEntry(ProjectEntry entry);

    Completable updateLastAccessedDate(AttributeEntity attributeEntity);

    Completable deleteProject(Long... controlSysIds);

    Maybe<List<AttributeEntity>> getAllProjectsWithAttributes();

    Maybe<List<AttributeEntity>> getProjectWithAttributesById(Long controlSysId);

    Maybe<ItemEntity> getItemById(Long controlSysId);

}

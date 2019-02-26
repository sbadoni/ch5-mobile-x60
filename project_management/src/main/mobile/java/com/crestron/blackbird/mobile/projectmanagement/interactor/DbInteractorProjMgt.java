package com.crestron.blackbird.mobile.projectmanagement.interactor;

import android.content.Context;
import android.text.TextUtils;

import com.crestron.blackbird.mobile.projectmanagement.model.ProjectEntry;
import com.crestron.itemattribute.db.util.DbUtil;
import com.crestron.itemattribute.db.ItemAttrDB;
import com.crestron.itemattribute.db.dao.AttributeDao;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;
import com.crestron.itemattribute.db.util.RxUtil;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * This is the point of interaction for project_management module to the database asynchronously.
 */
public class DbInteractorProjMgt implements DbInteractor {
    private static final String DB_NAME = "projects.db";

    private ItemAttrDB database;

    public DbInteractorProjMgt(Context context) {
        boolean memoryOnly = DbUtil.isTestLooper();
        database = ItemAttrDB.getInstance(context, memoryOnly, DB_NAME);
    }

    @Override
    public Completable updateLastAccessedDate(AttributeEntity entity) {
        AttributeDao attributeDao = database.getAttrDao();
        return Completable.fromAction(() -> attributeDao.updateAttr(entity));
    }

    @Override public Maybe createOrUpdateProjectEntry(ProjectEntry entry) {
        /* First insert/update row into [item] table */
        ItemEntity projectItem;
        String projectHash = entry.getHash();

        if (!TextUtils.isEmpty(entry.getControlSystemID())) {
            projectItem = new ItemEntity(Long.valueOf(entry.getControlSystemID()), projectHash);
        } else {
            projectItem = new ItemEntity(null, projectHash);
        }
        Long projectId = _insertOrUpdateItem(projectItem);

        /* Now insert/update rows into [attributes] table. */
        // Populate each [attributes] table row from ProjectEntry pojo fields
        AttributeEntity projectInfo = new AttributeEntity();
        projectInfo.attrKey = projectHash;
        projectInfo.value = entry.getLastAccessedDate();
        projectInfo.attrID = projectId;
        database.getAttrDao().insertAttr(projectInfo);

        return getProjectWithAttributesById(projectId);
    }

    /**
     * Decides whether to perform an update or insert operation.
     *
     * @param project ItemEntity - row in the table.
     * @return Long - id of the row.
     */
    private Long _insertOrUpdateItem(ItemEntity project) {
        List<ItemEntity> itemList = database.getAttrDao().getAllItems();
        for (ItemEntity item : itemList) {
            if (item.getId().equals(project.getId())) {
                return database.getAttrDao().updateItem(project);
            }
        }

        return database.getAttrDao().insertItem(project);
    }

    @Override public Completable deleteProject(Long... projectIds) {
        return Completable.fromAction(() -> database.getAttrDao().deleteItemDB(projectIds));
    }

    @Override public Maybe<List<AttributeEntity>> getAllProjectsWithAttributes() {
        return RxUtil.convertToRxMaybe(() -> database.getAttrDao().getAllAttributesInTable());
    }

    @Override public Maybe<List<AttributeEntity>> getProjectWithAttributesById(Long projectId) {
        return RxUtil.convertToRxMaybe(() -> database.getAttrDao().getAttributesFromID(projectId));
    }

    @Override public Maybe<ItemEntity> getItemById(Long projectId) {
        return RxUtil.convertToRxMaybe(() -> database.getAttrDao().getItemFromID(projectId));
    }
}

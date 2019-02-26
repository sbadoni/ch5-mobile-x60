package com.crestron.blackbird.mobile.cssettings.interactor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.crestron.blackbird.mobile.cssettings.model.InfoKeys;
import com.crestron.cryptography.Encryptor;
import com.crestron.cryptography.SecurityMethods;
import com.crestron.itemattribute.db.ItemAttrDB;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;
import com.crestron.itemattribute.db.util.DbUtil;
import com.crestron.itemattribute.db.util.RxUtil;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;
import com.example.data.BuildConfig;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * This is the point of interaction for control_system_settings module to the database asynchronously.
 */
public class DbInteractorCsSettings implements DbInteractor {
    private static final String TAG = DbInteractor.class.getCanonicalName();
    private static final String DB_NAME = "settings.db";

    private ItemAttrDB database;

    public ItemAttrDB getDatabase() {
        return database;
    }

    public DbInteractorCsSettings(Context context) {
        boolean memoryOnly = DbUtil.isTestLooper();
        database = ItemAttrDB.getInstance(context, memoryOnly, DB_NAME);
    }

    @Override
    public Maybe createOrUpdateSystemEntry(final ControlSystemEntry entry) {
        /* First insert/update row into [item] table */
        ItemEntity controlSys;

        if (!TextUtils.isEmpty(entry.getControlSystemID())) {
            controlSys = new ItemEntity(Long.valueOf(entry.getControlSystemID()), entry.getFriendlyName());
        } else {
            controlSys = new ItemEntity(null, entry.getFriendlyName());
        }
        Long sysId = _insertOrUpdateControlSys(controlSys);

        /* Now insert/update rows into [attributes] table. */
        // Populate each [attributes] table row from ControlSystemEntry pojo fields
        AttributeEntity sysInfo = new AttributeEntity();
        sysInfo.attrID = sysId;
        for (InfoKeys key : InfoKeys.values()) {
            switch (key) {
                case CIP_PORT_1:
                    if (entry.getCipPort1() > 0) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.getCipPort1());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case CIP_PORT_2:
                    if (entry.getCipPort2() > 0) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.getCipPort2());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case HOSTNAME_1:
                    if (!TextUtils.isEmpty(entry.getHostName1())) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = entry.getHostName1();
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case HOSTNAME_2:
                    if (!TextUtils.isEmpty(entry.getHostName2())) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = entry.getHostName2();
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case HTTP_PORT_1:
                    if (entry.getHttpPort1() > 0) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.getHttpPort1());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case HTTP_PORT_2:
                    if (entry.getHttpPort2() > 0) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.getHttpPort2());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case IPID:
                    if (entry.getIpid() > 0) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.getIpid());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case SECOND_ADDRESS_ACTIVE:
                    if (entry.isAddress2Enabled()) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.isAddress2Enabled());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case SSL_ENABLED:
                    sysInfo.attrKey = key.name();
                    sysInfo.value = String.valueOf(entry.isUseSSL());
                    database.getAttrDao().insertAttr(sysInfo);
                    break;
                case SSL_PASSWORD_LOGIN:
                    if (!TextUtils.isEmpty(entry.getUserPassword())) {
                        sysInfo.attrKey = key.name();
                        // Encrypt the password
                        try {
                            String securityKey = SecurityMethods.CONTROL_SYSTEMS_KEY;
                            sysInfo.value = Encryptor.encryptText(securityKey.getBytes("UTF-8"), entry.getUserPassword());
                            database.getAttrDao().insertAttr(sysInfo);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
                case SSL_SYSTEM_LOGIN:
                    if (!TextUtils.isEmpty(entry.getUserName())) {
                        sysInfo.attrKey = key.name();

                        // Encrypt the username
                        try {
                            String securityKey = SecurityMethods.CONTROL_SYSTEMS_KEY;
                            sysInfo.value = Encryptor.encryptText(securityKey.getBytes("UTF-8"), entry.getUserName());
                            database.getAttrDao().insertAttr(sysInfo);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
                case TE_PROGRAM_INSTANCE_ID:
                    if (!TextUtils.isEmpty(entry.getProgramInstanceId())) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = entry.getProgramInstanceId();
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                case USE_LOCAL_PROJECT:
                    if (entry.isUseLocalProject()) {
                        sysInfo.attrKey = key.name();
                        sysInfo.value = String.valueOf(entry.isUseLocalProject());
                        database.getAttrDao().insertAttr(sysInfo);
                    }
                    break;
                default:
                    if (BuildConfig.DEBUG) {
                        throw new AssertionError("Not all keys from InfoKeys are handled");
                    }
                    break;
            }
        }

        // Return an Rx Maybe from last db operation
        return getSystemById(sysId);
    }

    /**
     * Decides whether to perform an update or insert operation.
     *
     * @param controlSys ItemEntity - row in the table.
     * @return Long - id of the row.
     */
    private Long _insertOrUpdateControlSys(ItemEntity controlSys) {
        List<ItemEntity> itemList = database.getAttrDao().getAllItems();
        for (ItemEntity item : itemList) {
            if (item.getId().equals(controlSys.getId())) {
                return database.getAttrDao().updateItem(controlSys);
            }
        }

        return database.getAttrDao().insertItem(controlSys);
    }

    @Override
    public Completable deleteSystem(Long... controlSysIds) {
        return Completable.fromAction(() -> database.getAttrDao().deleteItemDB(controlSysIds));
    }

    @Override
    public Maybe<List<AttributeEntity>> getAllSystemsWithInfo() {
        return RxUtil.convertToRxMaybe(() -> database.getAttrDao().getAllAttributesInTable());
    }

    @Override
    public Maybe<List<AttributeEntity>> getSystemById(Long controlSysId) {
        return RxUtil.convertToRxMaybe(() -> database.getAttrDao().getAttributesFromID(controlSysId));
    }

}
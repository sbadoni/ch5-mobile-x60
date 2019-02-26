package com.crestron.blackbird.mobile.cssettings.model;

import android.text.TextUtils;

import com.crestron.cryptography.Decryptor;
import com.crestron.cryptography.SecurityMethods;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;
import com.crestron.mobile.cssettings.model.ControlSystemEntry;

import java.util.List;

import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.CIP_PORT_1;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.CIP_PORT_2;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.HOSTNAME_1;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.HOSTNAME_2;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.HTTP_PORT_1;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.HTTP_PORT_2;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.IPID;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.SECOND_ADDRESS_ACTIVE;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.SSL_ENABLED;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.SSL_PASSWORD_LOGIN;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.SSL_SYSTEM_LOGIN;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.TE_PROGRAM_INSTANCE_ID;
import static com.crestron.blackbird.mobile.cssettings.model.InfoKeys.USE_LOCAL_PROJECT;

/**
 * Represents the value object of {@link ControlSystemEntry} that gets sent to UI.
 */
public class EntryModel {
    private static final String TAG = ControlSystemEntry.class.getCanonicalName();

    /**
     * Make an entry model object.
     * Exceptions may be thrown by decrypting username/password.
     *
     * @param relationTable The joined sql table that holds the values.
     * @param item          the item the attributes are a part of
     * @return Instance of ControlSystemEntry.
     */
    public static ControlSystemEntry makeEntryObj(Object relationTable, ItemEntity item) {

        List<AttributeEntity> sysTable = (List<AttributeEntity>) relationTable;

        ControlSystemEntry entry = new ControlSystemEntry();
        String controlSysId = String.valueOf(item.getId());
        entry.setControlSystemID(controlSysId);
        entry.setFriendlyName(item.getItem_key());

        //Set AttributeEntity on entry obj from stored keys from control_system_info table
        for (AttributeEntity sysInfo : sysTable) {
            if (sysInfo.attrID.equals(item.getId()))
                for (InfoKeys key : InfoKeys.values()) {
                    switch (key) {
                        case CIP_PORT_1:
                            if (sysInfo.attrKey.equals(CIP_PORT_1.name())) {
                                entry.setCipPort1(Integer.parseInt(sysInfo.value));
                            }
                            break;
                        case CIP_PORT_2:
                            if (sysInfo.attrKey.equals(CIP_PORT_2.name())) {
                                entry.setCipPort2(Integer.parseInt(sysInfo.value));
                            }
                            break;
                        case HOSTNAME_1:
                            if (sysInfo.attrKey.equals(HOSTNAME_1.name())) {
                                entry.setHostName1(sysInfo.value);
                            }
                            break;
                        case HOSTNAME_2:
                            if (sysInfo.attrKey.equals(HOSTNAME_2.name())) {
                                entry.setHostName2(sysInfo.value);
                            }
                            break;
                        case HTTP_PORT_1:
                            if (sysInfo.attrKey.equals(HTTP_PORT_1.name())) {
                                entry.setHttpPort1(Integer.parseInt(sysInfo.value));
                            }
                            break;
                        case HTTP_PORT_2:
                            if (sysInfo.attrKey.equals(HTTP_PORT_2.name())) {
                                entry.setHttpPort2(Integer.parseInt(sysInfo.value));
                            }
                            break;
                        case IPID:
                            if (sysInfo.attrKey.equals(IPID.name())) {
                                entry.setIpid(Integer.parseInt(sysInfo.value));
                            }
                            break;
                        case SECOND_ADDRESS_ACTIVE:
                            if (sysInfo.attrKey.equals(SECOND_ADDRESS_ACTIVE.name())) {
                                entry.setAddress2Enabled(Boolean.parseBoolean(sysInfo.value));
                            }
                            break;
                        case SSL_ENABLED:
                            if (sysInfo.attrKey.equals(SSL_ENABLED.name())) {
                                entry.setUseSSL(Boolean.parseBoolean(sysInfo.value));
                            }
                            break;
                        case SSL_PASSWORD_LOGIN:
                            if (sysInfo.attrKey.equals(SSL_PASSWORD_LOGIN.name()) && !TextUtils.isEmpty(sysInfo.value)) {
                                String securityKey = SecurityMethods.CONTROL_SYSTEMS_KEY;
                                String deobfuscatedPwd = Decryptor.decryptData(securityKey.getBytes(), sysInfo.value);
                                entry.setUserPassword(deobfuscatedPwd);
                            }
                            break;
                        case SSL_SYSTEM_LOGIN:
                            if (sysInfo.attrKey.equals(SSL_SYSTEM_LOGIN.name()) && !TextUtils.isEmpty(sysInfo.value)) {
                                String securityKey = SecurityMethods.CONTROL_SYSTEMS_KEY;
                                String deobfuscatedUserName = Decryptor.decryptData(securityKey.getBytes(), sysInfo.value);
                                entry.setUserName(deobfuscatedUserName);
                            }
                            break;
                        case TE_PROGRAM_INSTANCE_ID:
                            if (sysInfo.attrKey.equals(TE_PROGRAM_INSTANCE_ID.name())) {
                                entry.setProgramInstanceId(sysInfo.value);
                            }
                            break;
                        case USE_LOCAL_PROJECT:
                            if (sysInfo.attrKey.equals(USE_LOCAL_PROJECT.name())) {
                                entry.setHostName2(sysInfo.value);
                            }
                            break;
                    }
                }
        }

        return entry;
    }
}

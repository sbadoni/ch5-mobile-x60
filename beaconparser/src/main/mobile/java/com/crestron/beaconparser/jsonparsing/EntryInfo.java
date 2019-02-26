package com.crestron.beaconparser.jsonparsing;

import java.util.ArrayList;
import java.util.List;

/**
 * the top layer of an .ibc file. this contains uuid and top later information
 */
public class EntryInfo {

    private String resName = "";
    private String csFriendlyName = "";
    private List<RoomInfo> rooms;
    private String uuid = "";
    private String CrestronBeaconConfig = "";

    public EntryInfo() {
        rooms = new ArrayList<>();
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getCsFriendlyName() {
        return csFriendlyName;
    }

    public void setCsFriendlyName(String csFriendlyName) {
        this.csFriendlyName = csFriendlyName;
    }

    public List<RoomInfo> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomInfo> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(RoomInfo roomInfo) {
        rooms.add(roomInfo);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCrestronBeaconConfig() {
        return CrestronBeaconConfig;
    }

    public void setCrestronBeaconConfig(String crestronBeaconConfig) {
        CrestronBeaconConfig = crestronBeaconConfig;
    }

    public String toPrettyString() {
        StringBuilder info = new StringBuilder("Entry: \n" +
                "\tresName: " + getResName() + "\n" +
                "\tcsFriendlyName: " + getCsFriendlyName() + "\n");
        for(RoomInfo r : getRooms()) {
            info.append(r.toPrettyString());
        }
        info.append("\tuuid: ")
                .append(getUuid())
                .append("\n").append("CrestronBeaconConfig: ").append(getCrestronBeaconConfig());
        return info.toString();
    }
}

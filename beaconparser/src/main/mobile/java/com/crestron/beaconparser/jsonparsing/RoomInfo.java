package com.crestron.beaconparser.jsonparsing;

import java.util.ArrayList;
import java.util.List;

/**
 * the room layer of an .ibc file. this contains room information
 */
public class RoomInfo {

    private int joinID;
    private String roomName = "";
    private List<BeaconInfo> beacons;
    private int index;

    public RoomInfo() {
        beacons = new ArrayList<>();
    }

    public int getJoinID() {
        return joinID;
    }

    public void setJoinID(int joinID) {
        this.joinID = joinID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<BeaconInfo> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<BeaconInfo> beacons) {
        this.beacons = beacons;
    }

    public void addBeacon(BeaconInfo beaconInfo) {
        beacons.add(beaconInfo);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String toPrettyString() {
        StringBuilder info = new StringBuilder("\tRooms: \n" +
                "\t\tjoinID: " + getJoinID() + "\n" +
                "\t\troomName: " + getRoomName() + "\n");
        for (BeaconInfo b : getBeacons()) {
            info.append(b.toPrettyString());
        }
        info.append("\t\tindex: ").append(getIndex()).append("\n");
        return info.toString();
    }
}

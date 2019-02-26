package com.crestron.beaconparser.jsonparsing;

/**
 * the final layer of an .ibc file. This contains beacon information
 */
public class BeaconInfo {

    private String beaconName = "";
    private int imajor;
    private int iminor;

    public BeaconInfo(String beaconName, int imajor, int iminor) {
        this.beaconName = beaconName;
        this.imajor = imajor;
        this.iminor = iminor;
    }

    public BeaconInfo() {
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public int getImajor() {
        return imajor;
    }

    public void setImajor(int imajor) {
        this.imajor = imajor;
    }

    public int getIminor() {
        return iminor;
    }

    public void setIminor(int iminor) {
        this.iminor = iminor;
    }

    public String toPrettyString() {
        return "\t\tBeacons: \n" +
                "\t\t\tbeaconName: " + getBeaconName() + "\n" +
                "\t\t\timajor: " + getImajor() + "\n" +
                "\t\t\timinor: " + getIminor() + "\n";
    }
}

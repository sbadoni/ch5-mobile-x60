package com.crestron.mobile.signal;

import com.google.gson.annotations.SerializedName;

public class ReservedSignals {
    private String timestamp;
    private String source;
    private String prefix;
    private int version;
    private String device;
    private Signal signals;


    @SerializedName("timestamp")
    public String getTimestamp() {
        return timestamp;
    }
    @SerializedName("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @SerializedName("source")
    public String getSource() {
        return source;
    }
    @SerializedName("source")
    public void setSource(String source) {
        this.source = source;
    }

    @SerializedName("prefix")
    public String getPrefix() {
        return prefix;
    }
    @SerializedName("prefix")
    public void setPrefix(String reservedJoinJSONPrefix) {
        this.prefix = reservedJoinJSONPrefix;
    }

    @SerializedName("version")
    public int getVersion() {
        return version;
    }
    @SerializedName("version")
    public void setVersion(int version) {
        this.version = version;
    }

    @SerializedName("device")
    public String getDevice() {
        return device;
    }
    @SerializedName("device")
    public void setDevice(String device) {
        this.device = device;
    }

    @SerializedName("signals")
    public Signal getSignals() {
        return signals;
    }
    @SerializedName("signals")
    public void setSignals(Signal signals) {
        this.signals = signals;
    }


}

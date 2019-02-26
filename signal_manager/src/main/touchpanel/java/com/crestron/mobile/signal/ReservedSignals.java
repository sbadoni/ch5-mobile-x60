package com.crestron.mobile.signal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReservedSignals {

    public static final String DIRECTION_TOUI = "toUI";
    public static final String DIRECTION_FROMUI = "FromUI";
    public static final String DIRECTION_BOTH = "both";
    public static final String SIGNAL_TYPE_BOOL = "boolean";
    public static final String SIGNAL_TYPE_STRING = "string";
    public static final String SIGNAL_TYPE_INT = "numeric";

    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("signals")
    @Expose
    private List<SignalInfo> signals = null;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<SignalInfo> getSignals() {
        return signals;
    }

    public void setSignals(List<SignalInfo> signals) {
        this.signals = signals;
    }

}
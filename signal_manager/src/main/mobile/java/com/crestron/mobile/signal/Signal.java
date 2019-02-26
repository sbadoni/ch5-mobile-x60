package com.crestron.mobile.signal;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Signal {

    private UI toUI;
    private UI fromUI;

    @SerializedName("toUI")
    public UI getToUI() {
        return toUI;
    }

    @SerializedName("toUI")
    public void setToUI(UI toUI) {
        this.toUI = toUI;
    }

    @SerializedName("fromUI")
    public UI getFromUI() {
        return fromUI;
    }

    @SerializedName("fromUI")
    public void setFromUI(UI fromUI) {
        this.fromUI = fromUI;
    }

    public class UI {
        @SerializedName("boolean")
        private Map<String,SignalInfo> booleanMap;
        private Map<String,SignalInfo> string;
        @SerializedName("numeric")
        private Map<String,SignalInfo> numeric;

        @SerializedName("boolean")
        public Map<String, SignalInfo> getBooleanTMap() {
            return booleanMap;
        }
        @SerializedName("boolean")
        public void setBooleanTMap(Map<String, SignalInfo> booleanTMap) {
            this.booleanMap = booleanTMap;
        }
        @SerializedName("string")
        public Map<String, SignalInfo> getString() {
            return string;
        }
        @SerializedName("string")
        public void setString(Map<String, SignalInfo> string) {
            this.string = string;
        }

        @SerializedName("numeric")
        public Map<String, SignalInfo> getNumeric() {
            return numeric;
        }
        @SerializedName("numeric")
        public void setNumeric(Map<String, SignalInfo> numeric) {
            this.numeric = numeric;
        }



    }



}

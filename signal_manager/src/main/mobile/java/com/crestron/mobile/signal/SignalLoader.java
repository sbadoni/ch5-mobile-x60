package com.crestron.mobile.signal;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * <h1>SignalLoader class </h1>
 * <p>
 * Loads signals from user project
 *
 * @author Colm Coady
 * @version 1.0
 */

class SignalLoader {

    private static final String TAG = SignalLoader.class.getName();

    SignalLoader() {
    }

    /**
     * Gson object used for JSON parsing
     */
    private Gson gson = new Gson();

    /**
     * User project signals
     */
    private UserSignals userSignals;

    /**
     * Reserved signals
     */
    private ReservedSignals mReservedSignals;

    /**
     * UserSignals inner class.
     * <p>
     * Defines arrays of signals
     * Only used to represent user project JSON
     */
    public class UserSignals {

        /**
         * StringSignals class containing lists of inbound and outbound string signals
         */
        class StringSignals {
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }

        /**
         * BoolSignals class containing lists of inbound and outbound boolean signals
         */
        class BoolSignals {
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }

        /**
         * IntegerSignals class containing lists of  inbound and outbound integer signals
         */
        class IntegerSignals {
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }

        /**
         * UserSignals class containing StringSignals,BoolSignals and IntegerSignals
         */
        StringSignals string;
        @SerializedName("boolean")
        BoolSignals booleanT;
        IntegerSignals numeric;
    }

    /**
     * @return UserSignals object
     */
    UserSignals getUserSignals() {
        return userSignals;
    }

    /**
     * @return Reserved Signals
     */
    ReservedSignals getReservedSignals() {
        return mReservedSignals;
    }


    /**
     * Loads user signals from a JSON file
     *
     * @param jsonFile path to JSON file
     */
    void loadUserSignals(File jsonFile) {
        try {

            FileInputStream fileInputStream = new FileInputStream(jsonFile);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            fileInputStream.close();
            parseUserJson(stringBuilder.toString());
        } catch (Exception e) {
            Log.d(TAG, "Error e: " + e.toString());
        }
    }

    /**
     * Performs an initial parse of a JSON file
     *
     * @param strJSON JSON string
     */
    private void parseUserJson(String strJSON) {
        JsonElement jelement;
        //JsonObject jobject;
        //JsonArray jArr;

        try {
            jelement = new JsonParser().parse(strJSON);
            userSignals = gson.fromJson(jelement.toString(), UserSignals.class);
            /*if (jelement.isJsonArray()) {
                jArr = jelement.getAsJsonArray();
                JsonElement jSubElement = jArr.get(0);
                jobject = jSubElement.getAsJsonObject();

                userSignals = gson.fromJson(jobject.toString(), UserSignals.class);

            }*/
        } catch (JsonSyntaxException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Loads Reserved signals from a JSON file
     *
     * @param inputStream path to JSON file
     */

    public void loadReservedSignals(InputStream inputStream) {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        parseReservedJson(writer.toString());
    }

    /**
     * Performs an initial parse of a JSON file
     *
     * @param strJSON JSON string
     */
    private void parseReservedJson(String strJSON) {
        try {
            mReservedSignals = gson.fromJson(strJSON, ReservedSignals.class);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, e.getMessage());
        }
    }


}

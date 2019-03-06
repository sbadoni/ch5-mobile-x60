package com.crestron.mobile.signal;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
    private ReservedSignal reservedSignals;

    /**
     * UserSignals inner class.
     * <p>
     * Defines arrays of signals
     * Only used to represent user project JSON
     */
    public class UserSignals {

        /**
         * StringSignals class containing lists of inbound and outbound string signals
         *
         */
        class StringSignals {
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }

        /**
         * BoolSignals class containing lists of inbound and outbound boolean signals
         *
         */
        class BoolSignals {
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }

        /**
         * IntegerSignals class containing lists of  inbound and outbound integer signals
         *
         */
        class IntegerSignals {
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }


        class ObjectSignals{
            List<SignalInfo> inbound;
            List<SignalInfo> outbound;
        }

        /**
         * UserSignals class containing StringSignals,BoolSignals and IntegerSignals
         *
         */
        StringSignals string;
        @SerializedName("boolean")
        BoolSignals booleanT;
        IntegerSignals numeric;

        ObjectSignals object;
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
    ReservedSignal getReservedSignals() {
        return reservedSignals;
    }

    /**
     * Loads user signals from a JSON file
     *
     * @param jsonFile path to JSON file
     */
    void loadUserSignals(File jsonFile) {
        try {

            FileInputStream fileInputStream  = new FileInputStream(jsonFile);

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
            Log.e( "Error e: " , e.toString());
        }
    }

    /**
     * Performs an initial parse of a JSON file
     *
     * @param strJSON JSON string
     */
    private void parseUserJson(String strJSON) {
        JsonElement jelement;
        JsonObject jobject;
        JsonArray jArr;

        try {
            userSignals = gson.fromJson(strJSON, UserSignals.class);
        } catch (JsonSyntaxException e) {
            Log.e( "Error e: " , e.toString());
        }
    }

    /**
     * Loads Reserved signals from a JSON file
     *
     * @param inputStream path to JSON file
     */
    public void loadReservedSignals(InputStream inputStream) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                Log.e( "Error e: " , e.toString());
            } catch (IOException e) {
                Log.e( "Error e: " , e.toString());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
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
            reservedSignals = gson.fromJson(strJSON, ReservedSignal.class);
        } catch (JsonSyntaxException e) {
            Log.e( "Error e: " , e.toString());
        }
    }


}

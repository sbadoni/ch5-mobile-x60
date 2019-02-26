package com.crestron.beaconparser;

import android.content.Context;
import android.content.res.Resources;

import com.crestron.beaconparser.R;
import com.crestron.beaconparser.jsonparsing.BeaconConfigProviderInterface;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BeaconConfigProviderFromStringTestCase implements BeaconConfigProviderInterface {

    Context context;

    public BeaconConfigProviderFromStringTestCase(Context context) {
        this.context = context;
    }

    @Override
    public String getJSON() {

        Resources res = context.getResources();
        try {
            InputStream inputStream = res.openRawResource(R.raw.second_test);

            assert inputStream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            return "{\n" +
                    "  \"entries\" : [\n" +
                    "    {\n" +
                    "      \"resName\" : \"TestOne\",\n" +
                    "      \"csFriendlyName\" : \"Jacobs MC3\",\n" +
                    "      \"rooms\" : [\n" +
                    "        {\n" +
                    "          \"joinID\" : 13,\n" +
                    "          \"roomName\" : \"Cubicle\",\n" +
                    "          \"beacons\" : [\n" +
                    "            {\n" +
                    "              \"beaconName\" : \"TC3EC7F3\",\n" +
                    "              \"imajor\" : 67,\n" +
                    "              \"iminor\" : 1\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"index\" : 6\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"joinID\" : 31,\n" +
                    "          \"roomName\" : \"Mac\",\n" +
                    "          \"beacons\" : [\n" +
                    "            {\n" +
                    "              \"beaconName\" : \"TC9EC99F\",\n" +
                    "              \"imajor\" : 67,\n" +
                    "              \"iminor\" : 2\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"index\" : 9\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"uuid\" : \"D1D94D19-439E-43A2-84AE-0B8E7BC3EF87\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"CrestronBeaconConfig\" : \"1.0.0\"\n" +
                    "}";
        }
    }
}

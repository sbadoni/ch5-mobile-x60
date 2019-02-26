package com.crestron.beaconparser.jsonparsing;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BeaconConfigProviderFromFile implements BeaconConfigProviderInterface {

    private String json;
    private Context context;

    /**
     * constructor
     * @param json - the path to the json file
     */
    public BeaconConfigProviderFromFile(Context context, String json) {
        this.json = json;
        this.context = context;
    }

    @Override
    public String getJSON() {
        File file = new File(json);

        Uri uri = Uri.fromFile(file);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

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
            return null;
        }
    }
}

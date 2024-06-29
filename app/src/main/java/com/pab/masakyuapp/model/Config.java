package com.pab.masakyuapp.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.pab.masakyuapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final String TAG = "Config";
    private Properties properties;

    public Config(Context context) {
        properties = new Properties();
        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            properties.load(rawResource);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }
    }

    public String getBaseUrl() {
        return properties.getProperty("BASE_URL", null);
    }
}

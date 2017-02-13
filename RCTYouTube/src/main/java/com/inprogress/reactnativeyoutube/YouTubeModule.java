package com.inprogress.reactnativeyoutube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;

import java.util.HashMap;

public class YouTubeModule extends ReactContextBaseJavaModule {
    ReactApplicationContext reactContext;
    YouTubeManager mYouTubeManager;

    public YouTubeModule(ReactApplicationContext reactContext, YouTubeManager youTubeManager) {
        super(reactContext);
        mYouTubeManager = youTubeManager;
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "YouTubeModule";
    }

    @ReactMethod
    public void seekTo(Integer seconds) {
        mYouTubeManager.seekTo(seconds);
    }

    @ReactMethod
    public void play (ReadableMap config) {
        Activity mainActivity = getCurrentActivity();

        Intent intent = new Intent(reactContext, YouTubeActivity.class);
        intent.putExtras(getExtras(config));

        mainActivity.startActivity(intent);
    }

    // helper method used to extract the videoId, title and description properties
    // videoId is the only required property
    private Bundle getExtras (ReadableMap config) {
        HashMap map = ((ReadableNativeMap) config).toHashMap();
        Bundle extras = new Bundle();

        if (map.containsKey(YouTubeActivity.KEY_VIDEO_ID)) {
            extras.putString(YouTubeActivity.KEY_VIDEO_ID, map.get(YouTubeActivity.KEY_VIDEO_ID).toString());
        }

        if (map.containsKey(YouTubeActivity.KEY_TITLE)) {
            extras.putString(YouTubeActivity.KEY_TITLE, map.get(YouTubeActivity.KEY_TITLE).toString());
        }

        if (map.containsKey(YouTubeActivity.KEY_DESCRIPTION)) {
            extras.putString(YouTubeActivity.KEY_DESCRIPTION, map.get(YouTubeActivity.KEY_DESCRIPTION).toString());
        }

        return extras;
    }
}

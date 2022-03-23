package com.openwrapinrn.n8ive.ads.openwrap;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.pubmatic.sdk.common.OpenWrapSDK;
import com.pubmatic.sdk.common.models.POBApplicationInfo;

import java.net.MalformedURLException;
import java.net.URL;

public class OpenWrap extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    OpenWrap(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "OpenWrap";
    }

    @ReactMethod
    public void initializeSDK(String storeURLString, Promise promise) {
        POBApplicationInfo appInfo = new POBApplicationInfo();
        String exception = "";
        try {
            appInfo.setStoreURL(new URL(storeURLString));
        } catch (MalformedURLException e) {
            exception = "MalformedURLException";
            e.printStackTrace();
        }
        OpenWrapSDK.setApplicationInfo(appInfo);
        promise.resolve((exception == "") ? "success" : exception);
    }
}

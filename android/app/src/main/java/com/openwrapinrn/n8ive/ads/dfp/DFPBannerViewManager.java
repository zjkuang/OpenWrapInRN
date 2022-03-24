package com.openwrapinrn.n8ive.ads.dfp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class DFPBannerViewManager extends SimpleViewManager<DFPBannerViewGroup> {
    private static final String REACT_CLASS = "DFPBanner";
    private ThemedReactContext mThemedReactContext;

    private static final String PROP_PUBLISHER_ID = "publisherID";
    private static final String PROP_PROFILE_ID = "profileID";
    private static final String PROP_IS_ON_SCREEN = "isOnScreen";
    private static final String PROP_AD_UNIT_ID = "adUnitID";
    private static final String PROP_SLOT_UUID = "slotUUID";
    private static final String PROP_AD_SIZE_TAG = "adSizeTag";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected DFPBannerViewGroup createViewInstance(@NonNull ThemedReactContext reactContext) {
        mThemedReactContext = reactContext;
        return new DFPBannerViewGroup(reactContext);
    }

    public enum Events {
        EVENT_SIZE_CHANGE("onSizeChange"),
        EVENT_DID_RECEIVE_AD("onBannerViewDidReceiveAd"),
        EVENT_DID_RECEIVE_AD_ERROR("onBannerViewDidFailToReceiveAdWithError"),
        EVENT_WILL_LEAVE_APP("onAdViewWillLeaveApplication"),
        EVENT_ADMOB_EVENT_RECEIVED("onAdmobDispatchAppEvent");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    @Override
    @Nullable
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @ReactProp(name = PROP_PUBLISHER_ID)
    public void setPropPublisherID(final DFPBannerViewGroup view, final String publisherID) {
        view.setPropPublisherID(publisherID);
    }

    @ReactProp(name = PROP_PROFILE_ID)
    public void setPropProfileID(final DFPBannerViewGroup view, final int profileID) {
        view.setPropProfileID(profileID);
    }

    @ReactProp(name = PROP_IS_ON_SCREEN)
    public void setPropIsOnScreen(final DFPBannerViewGroup view, final Boolean isOnScreen) {
        view.setPropIsOnScreen(isOnScreen);
    }

    @ReactProp(name = PROP_AD_UNIT_ID)
    public void setPropAdUnitID(final DFPBannerViewGroup view, final String adUnitID) {
        view.setPropAdUnitID(adUnitID);
    }

    @ReactProp(name = PROP_SLOT_UUID)
    public void setPropSlotUUID(final DFPBannerViewGroup view, final String slotUUID) {
        view.setPropSlotUUID(slotUUID);
    }

    @ReactProp(name = PROP_AD_SIZE_TAG)
    public void setPropAdSizeTag(final DFPBannerViewGroup view, final String adSizeTag) {
        view.setPropAdSizeTag(adSizeTag);
    }

    @Override
    protected void onAfterUpdateTransaction(@NonNull DFPBannerViewGroup view) { // = <iOS>didSetProps
        super.onAfterUpdateTransaction(view);
        view.onAfterUpdateTransaction();
    }
}

package com.openwrapinrn.n8ive.ads.dfp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AppEventListener;
import com.pubmatic.sdk.common.POBError;
import com.pubmatic.sdk.openwrap.banner.POBBannerView;
import com.pubmatic.sdk.openwrap.core.POBBid;
import com.pubmatic.sdk.openwrap.core.POBBidEvent;
import com.pubmatic.sdk.openwrap.core.POBBidEventListener;
import com.pubmatic.sdk.openwrap.eventhandler.dfp.DFPBannerEventHandler;

import java.util.List;
import java.util.Map;

public class DFPBannerViewGroup extends ReactViewGroup implements AppEventListener, POBBidEventListener {
    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEmitter;
    private String LOG_TAG = "DFPBannerViewGroup";

    private String mPublisherID = "156276"; // test id
    private int mProfileID = 1165; // test id

    private Boolean mIsOnScreen = false;
    private String mAdUnitID = "";
    private String mSlotUUID = "";
    private String mAdSizeTag = "";
    private AdSize[] mAdSizes;
    private Boolean mPropChanged = false;
    private int mLastWidth = -1;
    private int mLastHeight = -1;
    private Map<String, List<String>> mDTBCustomTargeting;
    private Boolean mPubMaticBidding = false;

    private POBBannerView mBannerView = null;

    public DFPBannerViewGroup(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;
        mEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
    }

    // AppEventListener
    @Override
    public void onAppEvent(@NonNull String name, @NonNull String info) {
        WritableMap event = Arguments.createMap();
        event.putString(name, info);
        mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_ADMOB_EVENT_RECEIVED.toString(), event);
    }

    // POBBidEventListener
    @Override
    public void onBidReceived(@NonNull POBBidEvent pobBidEvent, @NonNull POBBid pobBid) {
        didFinishPubMaticBidding();
    }

    // POBBidEventListener
    @Override
    public void onBidFailed(@NonNull POBBidEvent pobBidEvent, @NonNull POBError pobError) {
        didFinishPubMaticBidding();
    }

    public void setPropPublisherID(final String publisherID) {
        if (publisherID != null && !publisherID.equals(mPublisherID)) {
            mPublisherID = publisherID;
            mPropChanged = true;
        }
    }

    public void setPropProfileID(final int profileID) {
        if (profileID != mProfileID) {
            mProfileID = profileID;
            mPropChanged = true;
        }
    }

    public void setPropIsOnScreen(final Boolean isOnScreen) {
        if (isOnScreen != mIsOnScreen) {
            mIsOnScreen = isOnScreen;
            mPropChanged = true;
        }
    }

    public void setPropAdUnitID(final String adUnitID) {
        if (adUnitID != null && !adUnitID.equals(mAdUnitID)) {
            mAdUnitID = adUnitID;
            mPropChanged = true;
        }
    }

    public void setPropSlotUUID(final String slotUUID) {
        if (slotUUID != null && !slotUUID.equals(mSlotUUID)) {
            mSlotUUID = slotUUID;
            mPropChanged = true;
        }
    }

    public void setPropAdSizeTag(final String adSizeTag) {
        if (adSizeTag != null && !adSizeTag.equals(mAdSizeTag)) {
            mAdSizeTag = adSizeTag;
            mPropChanged = true;

            AdSize adSize = getAdSizeFromTag(adSizeTag);
            AdSize[] adSizes = new AdSize[1];
            adSizes[0] = adSize;
            mAdSizes = adSizes;
        }
    }

    public void onAfterUpdateTransaction() { // = <iOS>didSetProps
        if (mPropChanged) {
            loadBanner();
            mPropChanged = false;
        }
    }

    private AdSize getAdSizeFromTag(String adSizeTag) {
        switch (adSizeTag.toLowerCase()) {
            case "largebanner":
                return AdSize.LARGE_BANNER;
            case "mediumrectangle":
                return AdSize.MEDIUM_RECTANGLE;
            case "fullBanner":
                return AdSize.FULL_BANNER;
            case "leaderBoard":
                return AdSize.LEADERBOARD;
            case "smartBannerPortrait":
            case "smartBannerLandscape":
            case "smartBanner":
                return AdSize.SMART_BANNER;
            default:
                return AdSize.BANNER;
        }
    }

    private void loadBanner() {
        Log.d(LOG_TAG, "loadBanner()...");
        if (!mIsOnScreen) {
            Log.d(LOG_TAG, "loadBanner() aborted. Not on screen.");
            removeAllViews();
            return;
        }
        if (mAdUnitID.isEmpty()) {
            Log.d(LOG_TAG, "loadBanner() aborted. mAdUnitID is empty.");
            return;
        }
        if (mAdSizes == null || mAdSizes.length ==0) {
            Log.d(LOG_TAG, "loadBanner() aborted. mAdSizes is empty.");
            return;
        }

        DFPBannerEventHandler eventHandler = makeBannerEventHandler();
        String shortAdUnitID = mAdUnitID.substring(mAdUnitID.lastIndexOf('/') + 1).trim();
        POBBannerView bannerView = new POBBannerView(mThemedReactContext, mPublisherID, mProfileID, shortAdUnitID, eventHandler);
        removeBanner();
        addView(bannerView);
        mBannerView = bannerView;
        attachEvents();

        mPubMaticBidding = true;
        mBannerView.loadAd(); // -> POBBidEventListener :: onBidReceived()/onBidFailed(); POBBannerViewListener :: onAdReceived()/onAdFailed()
    }

    private void removeBanner() {
        if (mBannerView != null) {
            removeView(mBannerView);
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    private DFPBannerEventHandler makeBannerEventHandler() {
        DFPBannerEventHandler eventHandler = new DFPBannerEventHandler(mThemedReactContext, mAdUnitID, mAdSizes);
        eventHandler.setConfigListener(new DFPBannerEventHandler.DFPConfigListener() {
            @Override
            public void configure(@NonNull AdManagerAdView adView, @NonNull AdManagerAdRequest.Builder adRequestBuilder, @Nullable POBBid pobBid) {
                adView.setAdSizes(AdSize.FLUID, AdSize.BANNER, AdSize.MEDIUM_RECTANGLE);

                int width, height;
                if (adView.getAdSize().equals(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mThemedReactContext, adView.getAdSize().getWidthInPixels(mThemedReactContext)))) {
                    width = adView.getAdSize().getWidthInPixels(mThemedReactContext);
                    height = adView.getAdSize().getHeightInPixels(mThemedReactContext);
                } else {
                    width = adView.getWidth();
                    height = adView.getHeight();
                }
                if (width != mLastWidth || height != mLastHeight) {
                    mLastHeight = height;
                    mLastWidth = width;
                    WritableMap event = Arguments.createMap();
                    event.putDouble("width", width);
                    event.putDouble("height", height);
                    mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_SIZE_CHANGE.toString(), event);
                }

                adRequestBuilder.setContentUrl("http://www.gasbuddy.com");

                if (mDTBCustomTargeting != null) {
                    for (String key: mDTBCustomTargeting.keySet()) {
                        adRequestBuilder.addCustomTargeting(key, mDTBCustomTargeting.get(key));
                    }
                }
            }
        });
        return eventHandler;
    }

    protected void attachEvents() {
        if (mBannerView == null) { return; }
        final String TAG = "BannerViewListener";
        mBannerView.setListener(new POBBannerView.POBBannerViewListener() {
            @Override
            public void onAdReceived(POBBannerView adView) {
                int width = adView.getCreativeSize().getAdWidth();
                int height = adView.getCreativeSize().getAdHeight();
                int left = adView.getLeft();
                int top = adView.getTop();
                adView.measure(width, height);
                adView.layout(left, top, left + width, top + height);

                WritableMap size = Arguments.createMap();
                size.putDouble("width", width);
                size.putDouble("height", height);
                mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_SIZE_CHANGE.toString(), size);

                WritableMap frame = Arguments.createMap();
                frame.putDouble("x", left);
                frame.putDouble("y", top);
                frame.putDouble("width", width);
                frame.putDouble("height", height);
                mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_DID_RECEIVE_AD.toString(), frame);

                mPubMaticBidding = true; // OpenWrap will refresh its bidding automatically every 30 sec
            }

            @Override
            public void onAdFailed(POBBannerView adView, POBError error) {
                WritableMap event = Arguments.createMap();
                switch (error.getErrorCode()) {
                    case AdManagerAdRequest.ERROR_CODE_INTERNAL_ERROR:
                        event.putString("error", "ERROR_CODE_INTERNAL_ERROR");
                        break;
                    case AdManagerAdRequest.ERROR_CODE_INVALID_REQUEST:
                        event.putString("error", "ERROR_CODE_INVALID_REQUEST");
                        break;
                    case AdManagerAdRequest.ERROR_CODE_NETWORK_ERROR:
                        event.putString("error", "ERROR_CODE_NETWORK_ERROR");
                        break;
                    case AdManagerAdRequest.ERROR_CODE_NO_FILL:
                        event.putString("error", "ERROR_CODE_NO_FILL");
                        break;
                }
                mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_DID_RECEIVE_AD_ERROR.toString(), event);

                mPubMaticBidding = true; // OpenWrap will refresh its bidding automatically every 30 sec
            }

            @Override
            public void onAdOpened(POBBannerView adView) {
                //
            }

            @Override
            public void onAdClosed(POBBannerView adView) {
                //
            }
        });
    }

    private void didFinishPubMaticBidding() {
        mPubMaticBidding = false;
        proceedToLoadAd();
    }

    private void proceedToLoadAd() {
        if (mPubMaticBidding || (mBannerView == null)) { return; }
        mBannerView.proceedToLoadAd();
    }
}
package com.openwrapinrn.n8ive.ads.dfp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AppEventListener;
import com.openwrapinrn.R;
import com.pubmatic.sdk.common.POBError;
import com.pubmatic.sdk.openwrap.banner.POBBannerView;
import com.pubmatic.sdk.openwrap.core.POBBid;
import com.pubmatic.sdk.openwrap.core.POBBidEvent;
import com.pubmatic.sdk.openwrap.core.POBBidEventListener;
import com.pubmatic.sdk.openwrap.eventhandler.dfp.DFPBannerEventHandler;

import java.util.List;
import java.util.Map;

public class DFPBannerViewGroup extends ReactViewGroup implements AppEventListener {
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

    AdManagerAdView mAdView = null;

    public DFPBannerViewGroup(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;
        mEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
    }

    // AppEventListener
    @Override
    public void onAppEvent(@NonNull String name, @NonNull String info) {
        Log.d(LOG_TAG, String.format("onAppEvent: %s", name));
        WritableMap event = Arguments.createMap();
        event.putString(name, info);
        mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_ADMOB_EVENT_RECEIVED.toString(), event);
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

        AdManagerAdView adView = new AdManagerAdView(this.mThemedReactContext);
        // adView.setAdSizes(AdSize.BANNER, AdSize.MEDIUM_RECTANGLE, AdSize.FLUID);
        adView.setAdSizes(AdSize.BANNER);
        // adView.setAdSizes(mAdSizes);
        adView.setAdUnitId("/6499/example/banner"); // test ID: "/6499/example/banner"
        removeBanner();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(adView, layoutParams);
        mAdView = adView;
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void removeBanner() {
        if (mAdView != null) {
            removeView(mAdView);
            destroyBanner();
        }
    }

    public void destroyBanner() {
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
    }
}

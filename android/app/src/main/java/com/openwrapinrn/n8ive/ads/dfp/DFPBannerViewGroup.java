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
        adView.setAdSizes(AdSize.BANNER, AdSize.MEDIUM_RECTANGLE, AdSize.FLUID);
        adView.setAdUnitId(mAdUnitID); 
        removeBanner();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(adView, layoutParams);
        mAdView = adView;
        adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        addAdListener();
        addLayoutListener();

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void addAdListener() {
        if (mAdView == null) { return; }
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
                // Setting the gravity to center was causing an layout issue when OpenWrap Banner is integrated with React Native application,
                // To resolve this issue, one must overwrite the child views of POBBannerView's property to NO_GRAVITY
                int childCount = mAdView.getChildCount();
                for (int position = 0; position < childCount; position++){
                    View childView = mAdView.getChildAt(position);
                    if(childView.getLayoutParams() instanceof FrameLayout.LayoutParams){
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)childView.getLayoutParams();
                        layoutParams.gravity = Gravity.END;
                        // This doesn't work for fluid ads
                        // if (fluid) {
                        //     // https://developers.google.com/ad-manager/mobile-ads-sdk/android/native/styles#fluid_size
                        //     layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                        //     layoutParams.width = LayoutParams.MATCH_PARENT;
                        //     layoutParams.height = LayoutParams.WRAP_CONTENT;
                        // }
                        // Fluid ads seems to have been broken by GAM Android SDK:
                        //   https://groups.google.com/g/google-admob-ads-sdk/c/PLc6xW1_ETA/m/xMO8JRuzAQAJ
                    }
                }

                AdSize adSize = mAdView.getAdSize();
                int width = adSize.getWidthInPixels(mThemedReactContext);
                int height = adSize.getHeightInPixels(mThemedReactContext);
                int left = mAdView.getLeft();
                int top = mAdView.getTop();
                mAdView.measure(width, height);
                mAdView.layout(left, top, left + width, top + height);

                if (mAdView.getAdSize().isFluid()) {
                    // Seems we still cannot recognize the fluid ad.
                }

                mAdView.setBackgroundColor(getResources().getColor(R.color.catalyst_redbox_background));
                WritableMap size = Arguments.createMap();
                size.putDouble("width", width);
                size.putDouble("height", height);
                mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_SIZE_CHANGE.toString(), size);

                WritableMap frame = Arguments.createMap();
                //frame.putDouble("x", left);
                //frame.putDouble("y", top);
                frame.putDouble("width", width);
                frame.putDouble("height", height);
                mEmitter.receiveEvent(getId(), DFPBannerViewManager.Events.EVENT_DID_RECEIVE_AD.toString(), frame);

                //mPubMaticBidding = true; // OpenWrap will refresh its bidding automatically every 30 sec
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
    }

    private void addLayoutListener() {
        if (mAdView == null) { return; }
        mAdView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                Log.d(LOG_TAG, String.format(
                        "onLayoutChange :: new: (%d,%d),%dx%d; old: (%d,%d),%dx%d; measuredSize: %dx%d",
                        left, top, (right - left), (bottom - top),
                        oldLeft, oldTop, (oldRight - oldLeft), (oldBottom - oldTop),
                        measuredWidth, measuredHeight));
            }
        });
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

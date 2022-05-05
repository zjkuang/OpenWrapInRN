package com.openwrapinrn.n8ive.ads.dfp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AppEventListener;
import com.openwrapinrn.R;


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
    boolean isFluid = false;

    public DFPBannerViewGroup(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;
        mEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);
    }

    // AppEventListener
    @Override
    public void onAppEvent(@NonNull String name, @NonNull String info) {
        Log.d(LOG_TAG, String.format("onAppEvent: %s", name));

        this.isFluid = true;

        this.updateLayout();

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

        removeBanner();

        mAdView = new AdManagerAdView(this.mThemedReactContext);
        mAdView.setAdSizes(AdSize.FLUID);
        mAdView.setAdUnitId(mAdUnitID);
//        mAdView.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
//
//        mAdView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mAdView.setAppEventListener(this);
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

                mAdView.addOnLayoutChangeListener(
                        (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                            // Forward the new height to its container.
                            int newMeasuredHeight = v.getMeasuredHeight();
                            int newHeight = v.getHeight();
                            Log.d("test", "onLayoutChange: MeasuredHeight::" + newMeasuredHeight);
                            Log.d("test", "onLayoutChange: Height::" + newHeight);

                            ResponseInfo responseInfo = mAdView.getResponseInfo();
                            Log.d("test", "onLayoutChange: responseInfo::" + responseInfo);
                        }
                );

                AdSize adSize = mAdView.getAdSize();
                Context context = getContext();

                int width = adSize.getWidthInPixels(context);
                int height = adSize.getHeightInPixels(context) * 3;

                int left = mAdView.getLeft() + 800 ;
                int top = mAdView.getTop();

                View parent = (View) mAdView.getParent();

//                if (parent != null) {
//                    int parentWidth = parent.getWidth();
//
//                    left = (parentWidth - width) / 2;
//                }

                mAdView.measure(width, height);
                mAdView.layout(left, top, left + width, top + height);

                if (adSize.isFluid()) {
                    // Seems we still cannot recognize the fluid ad.
                    Log.d("test", "isFluid: true");
                }

                mAdView.setBackgroundColor(getResources().getColor(R.color.catalyst_redbox_background));

                // Post adSize back to RN.
                sendOnSizeChangeEvent(width, height);

                addView(mAdView);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void sendOnSizeChangeEvent(int width, int height) {
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

    private class MeasureAndLayoutRunnable implements Runnable {
        @Override
        public void run() {
            updateLayout();
        }
    }

    private boolean isFluid() {
        return this.isFluid;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();

        if (isFluid()) {
            post(new DFPBannerViewGroup.MeasureAndLayoutRunnable());
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (isFluid()) {
            post(new DFPBannerViewGroup.MeasureAndLayoutRunnable());
        }
    }

    private static void measureAndLayout(View view, int width, int height) {
        int left = 0;
        int top = 0;

        view.measure(width, height);
        view.layout(left, top, left + width, top + height);
        view.requestLayout();
        view.invalidate();
        view.forceLayout();
    }

    int cachedWidth = 0;
    int cachedHeight = 0;

    private void updateLayout() {
        try {
            if (!isFluid()) {
                return;
            }

            if (mAdView == null) {
                return;
            }

            View parent = (View) mAdView.getParent();

            if (parent == null) {
                return;
            }

            int width = parent.getWidth();
            int height = parent.getHeight();

            if (cachedWidth == width && cachedHeight == height) {
                return;
            }

            cachedWidth = width;
            cachedHeight = height;

            // In case of fluid ads, every GAD view and their subviews must be laid out by hand,
            // otherwise the web view won't align to the container bounds.
            measureAndLayout(mAdView, width, height);

            ViewGroup child = (ViewGroup) mAdView.getChildAt(0);

            if (child != null) {
                measureAndLayout(child, width, height);

                ViewGroup webView = (ViewGroup) child.getChildAt(0);

                if (webView != null) {
                    measureAndLayout(webView, width, height);

                    ViewGroup internalChild = (ViewGroup) webView.getChildAt(0);

                    if (internalChild != null) {
                        measureAndLayout(internalChild, width, height);

                        ViewGroup leafNode = (ViewGroup) internalChild.getChildAt(0);

                        if (leafNode != null) {
                            measureAndLayout(leafNode, width, height);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            Log.e("error", "Failed to layout:" + exception.getLocalizedMessage());
        }
    }
}

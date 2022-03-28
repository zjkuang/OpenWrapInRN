//
//  DFPBanner.h
//  OpenWrapInRN
//
//  Created by Zhengqian Kuang on 2022-03-27.
//

#import <Foundation/Foundation.h>
#import <React/RCTComponent.h>

@import GoogleMobileAds;
@import OpenWrapSDK;
@import OpenWrapHandlerDFP;
#import <DTBiOSSDK/DTBiOSSDK.h>

@class RCTEventDispatcher;

@interface DFPBannerView : UIView <POBBannerViewDelegate, POBBidEventDelegate, GADAdSizeDelegate, GADAppEventDelegate, DTBAdCallback>

@property (nonatomic) BOOL optOutFromSale;
@property (nonatomic) BOOL adOnScreen;
@property (nonatomic, copy) NSString *adUnitID;
@property (nonatomic, copy) NSString *slotUUID;
@property (nonatomic, copy) NSString *placement;
@property (nonatomic, copy) NSString *adSizeTag; // Decided by placement on JS side
@property (nonatomic, copy) NSArray *adSizes;
@property (nonatomic, copy) NSDictionary *dimensions;
@property (nonatomic, copy) NSDictionary *additionalParameters;
@property (nonatomic, copy) NSNumber *sessionStartTimeSince1970InSec;
@property (nonatomic, copy) NSNumber *sessionImpressionsCount;
@property (nonatomic, copy) NSString *testDeviceID;

@property (nonatomic, copy) RCTDirectEventBlock onWillChangeAdSizeTo;
@property (nonatomic, copy) RCTDirectEventBlock onSizeChange;
@property (nonatomic, copy) RCTDirectEventBlock onBannerViewDidReceiveAd;
@property (nonatomic, copy) RCTDirectEventBlock onDidFailToReceiveAdWithError;
@property (nonatomic, copy) RCTDirectEventBlock onAdViewWillPresentScreen;
@property (nonatomic, copy) RCTDirectEventBlock onAdViewDidDismissScreen;
@property (nonatomic, copy) RCTDirectEventBlock onAdViewWillLeaveApplication;
@property (nonatomic, copy) RCTDirectEventBlock onAdmobDispatchAppEvent;

@end

//
//  DFPBannerManager.m
//  OpenWrapInRN
//
//  Created by Zhengqian Kuang on 2022-03-27.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>

#import "DFPBannerViewManager.h"
#import "DFPBannerView.h"

@implementation DFPBannerViewManager

RCT_EXPORT_MODULE();

- (UIView *)view {
    return [[DFPBannerView alloc] init];
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(optOutFromSale, BOOL);
RCT_EXPORT_VIEW_PROPERTY(adOnScreen, BOOL);
RCT_EXPORT_VIEW_PROPERTY(adUnitID, NSString);
RCT_EXPORT_VIEW_PROPERTY(slotUUID, NSString);
RCT_EXPORT_VIEW_PROPERTY(placement, NSString);
RCT_EXPORT_VIEW_PROPERTY(adSizeTag, NSString);
RCT_EXPORT_VIEW_PROPERTY(adSizes, NSArray);
RCT_EXPORT_VIEW_PROPERTY(dimensions, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(additionalParameters, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(sessionStartTimeSince1970InSec, NSNumber);
RCT_EXPORT_VIEW_PROPERTY(sessionImpressionsCount, NSNumber);
RCT_EXPORT_VIEW_PROPERTY(testDeviceID, NSString);

RCT_EXPORT_VIEW_PROPERTY(onWillChangeAdSizeTo, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onSizeChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onBannerViewDidReceiveAd, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onDidFailToReceiveAdWithError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdViewWillPresentScreen, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdViewDidDismissScreen, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdViewWillLeaveApplication, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAdmobDispatchAppEvent, RCTDirectEventBlock)

@end

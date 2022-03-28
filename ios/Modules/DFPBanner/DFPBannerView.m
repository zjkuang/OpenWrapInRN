//
//  DFPBanner.m
//  OpenWrapInRN
//
//  Created by Zhengqian Kuang on 2022-03-27.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/UIView+React.h>
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import "DFPBannerView.h"

BOOL turnOffLocalDebug = NO;
void localDebug(NSString *format, ...) {
  if (turnOffLocalDebug) {
    return;
  }
  va_list args;
  va_start(args, format);
  NSString *formatWithTag = [NSString stringWithFormat:@"*** GBDFPBannerView :: %@", format];
  NSLogv(formatWithTag, args);
  va_end(args);
}

@implementation DFPBannerView {
  POBBannerView *_bannerView;
  BOOL _needsReloadBanner;
  CGSize _bannerViewSize;
  NSDictionary *_dtbCustomTargeting;
  BOOL _openWrapBidding;
  BOOL _dtbAdLoading;
}

- (instancetype)init {
  if (self = [super init]) {
    _adOnScreen = YES;
    _openWrapBidding = NO;
    _dtbAdLoading = NO;
    self.backgroundColor=[UIColor clearColor];
  }
  return self;
}

- (void)insertReactSubview:(UIView *)view atIndex:(NSInteger)atIndex {
  RCTLogError(@"DFP Banner cannot have any subviews");
  [super insertReactSubview:view atIndex:atIndex];
  return;
}

- (void)removeReactSubview:(UIView *)subview {
  RCTLogError(@"DFP Banner cannot have any subviews");
  [super removeReactSubview:subview];
  return;
}

- (GADAdSize)getAdSizeFromString:(NSString *)adSizeTag {
  if ([adSizeTag isEqualToString:@"Banner"]) {
    return GADAdSizeBanner;
  } else if ([adSizeTag isEqualToString:@"LargeBanner"]) {
    return GADAdSizeLargeBanner;
  } else if ([adSizeTag isEqualToString:@"MediumRectangle"]) {
    return GADAdSizeMediumRectangle;
  } else if ([adSizeTag isEqualToString:@"FullBanner"]) {
    return GADAdSizeFullBanner;
  } else if ([adSizeTag isEqualToString:@"Leaderboard"]) {
    return GADAdSizeLeaderboard;
  }
  // smart banner currently not supported by OpenWrap
//  else if ([adSizeTag isEqualToString:@"SmartBannerPortrait"]) {
//    return kGADAdSizeSmartBannerPortrait;
//    return GADPortraitAnchoredAdaptiveBannerAdSizeWithWidth(_width);
//  } else if ([adSizeTag isEqualToString:@"SmartBannerLandscape"]) {
//    return kGADAdSizeSmartBannerLandscape;
//  }
  else {
    return GADAdSizeBanner;
  }
}

- (void)loadBanner {
  localDebug(@"<%@> loadBanner()...", _placement);
  
  if (!_adOnScreen) {
    localDebug(@"<%@> loadBanner() aborted. !_adOnScreen", _placement);
    return;
  }
  if (!_adUnitID) {
    localDebug(@"<%@> loadBanner() aborted. !_adUnitID", _placement);
    return;
  }
  if (!(_adSizeTag || _dimensions || _adSizes)) {
    localDebug(@"<%@> loadBanner() aborted. !(_adSizeTag || _dimensions || _adSizes)", _placement);
    return;
  }
  
  GADAdSize size = GADAdSizeInvalid;
  NSMutableArray *validAdSizes = [[NSMutableArray alloc] init];
  if (_dimensions) {
    NSNumber *width = [RCTConvert NSNumber:_dimensions[@"width"]];
    NSNumber *height = [RCTConvert NSNumber:_dimensions[@"height"]];

    CGFloat widthVal = [width doubleValue];
    CGFloat heightVal = [height doubleValue];

    CGSize cgSize = CGSizeMake(widthVal, heightVal);

    size = GADAdSizeFromCGSize(cgSize);
    [validAdSizes addObject:NSValueFromGADAdSize(size)];
  } else if (_adSizes) {
    for (id anAdSize in _adSizes) {
      GADAdSize aSize;

      // BannerSize
      if ([anAdSize isKindOfClass:[NSString class]]) {
        NSString *stringAdSize = (NSString *)anAdSize;

        aSize = [self getAdSizeFromString:stringAdSize];
        [validAdSizes addObject:NSValueFromGADAdSize(aSize)];

        // Set size to the first one in the list
        if (GADAdSizeEqualToSize(size, GADAdSizeInvalid)) {
          size = aSize;
        }
      }

      // Dimensions
      if ([anAdSize isKindOfClass:[NSDictionary class]]) {
        NSDictionary *dictionaryAdSize = (NSDictionary *)anAdSize;

        NSNumber *width = [RCTConvert NSNumber:dictionaryAdSize[@"width"]];
        NSNumber *height = [RCTConvert NSNumber:dictionaryAdSize[@"height"]];

        CGFloat widthVal = [width doubleValue];
        CGFloat heightVal = [height doubleValue];

        CGSize cgSize = CGSizeMake(widthVal, heightVal);

        aSize = GADAdSizeFromCGSize(cgSize);
        [validAdSizes addObject:NSValueFromGADAdSize(aSize)];

        // Set size to the first one in the list
        if (GADAdSizeEqualToSize(size, GADAdSizeInvalid)) {
          size = aSize;
        }
      }
    }
  } else {
    size = [self getAdSizeFromString:_adSizeTag];
    [validAdSizes addObject:NSValueFromGADAdSize(size)];
  }

  // For OpenWrap demand, we do not want to support multiple-sized ads for each ad slot. We only want to be doing that for DFP (Google).
  // DFP (Google) is the end-server that OpenWrap (and Amazon) demand ultimately delivers into. DFP (Google) supports multi-size requests for each ad slot in the app (ie. 320x50, 300x250, fluid) but Amazon and OpenWrap SDKs should only ever be receiving a single-sized request, depending on the ad slot.
  // For example, for each "Anchor" ad slot, OpenWrap and Amazon should only ever be receiving 320x50 requests.
  // Whereas for the "In-Content" / "Inside" ad slots, they should be receiving 300x250 requests only.
  
  DFPBannerEventHandler *eventHandler = [[DFPBannerEventHandler alloc]
                                         initWithAdUnitId: _adUnitID // (test adUniId) @"/15671365/pm_sdk/PMSDK-Demo-App-Banner"
                                         andSizes:validAdSizes]; // For PubMatic
  [eventHandler setConfigBlock:^(GAMBannerView *view, GAMRequest *request, POBBid *bid) { // For GAM
    view.validAdSizes = @[
      NSValueFromGADAdSize(GADAdSizeFluid),
      NSValueFromGADAdSize(GADAdSizeBanner),
      NSValueFromGADAdSize(GADAdSizeMediumRectangle),
      NSValueFromGADAdSize(GADAdSizeLargeBanner)
    ];

    CGRect rect = self->_bannerView.frame;
    rect.size = size.size;
    view.frame = rect;

    // eventHandler has been initialized with _adUnitID so this is not necessary
    // but leave this comment there
    // view.adUnitID = self->_adUnitID; // (test value) @"/15671365/pm_sdk/PMSDK-Demo-App-Banner";
    view.adSizeDelegate = self;
    
    GADExtras *extras = [GADExtras new];
    extras.additionalParameters = self->_additionalParameters;

    request.contentURL = @"http://www.gasbuddy.com";
    [request setCustomTargeting: self->_dtbCustomTargeting];
    [request registerAdNetworkExtras:extras];
    
    localDebug(@"<%@> loadBanner() [eventHandler setConfigBlock] [request setCustomTargeting.amzn_b:%@]", self->_placement, self->_dtbCustomTargeting[@"amzn_b"]);

    RCTLogInfo(@"openwrap: bid info: %@", bid);
  }];

  NSArray *fullAdUnitId = [_adUnitID componentsSeparatedByString: @"/"];
  NSString *adUnitId = fullAdUnitId.lastObject;

  // Since this function could get called multiple times, we need to make sure that we only have
  // one POBBannerView at a time. Without this code, _bannerView will leak as it adds new banner
  // views to the UIView without dereferencing and removing the old one.
  [self removeBannerView];
  // Create a new banner view instance and add it to the view.
  _bannerView = [[POBBannerView alloc]
               initWithPublisherId:@"160361" // (test value) @"156276"
               profileId:@3422 // (test value) @1165
               adUnitId: adUnitId // (test value) @"/15671365/pm_sdk/PMSDK-Demo-App-Banner"
               eventHandler: eventHandler];
  _bannerView.frame = CGRectMake(self.bounds.origin.x, self.bounds.origin.y, size.size.width, size.size.height);
  _bannerView.delegate = self;
  [self addSubview:_bannerView];
  
  _bannerViewSize = _bannerView.frame.size;
  
  _openWrapBidding = YES;
  localDebug(@"loadBanner() [_bannerView loadAd]...");
  [_bannerView loadAd]; // -> bidEvent:didReceiveBid: / bidEvent:didFailToReceiveBidWithError:
  localDebug(@"loadBanner() loadAmazonAd...");
  [self loadAmazonAd];
}

- (void)didFinishLoadingOpenWrapBid {
  [self proceedToLoadAd];
}

- (void)didFinishLoadingDTBAd {
  [self proceedToLoadAd];
}

- (void)proceedToLoadAd {
  localDebug(@"proceedToLoadAd()...");
  if (_openWrapBidding || _dtbAdLoading) { return; }
  localDebug(@"[_bannerView proceedToLoadAd]...");
  [_bannerView proceedToLoadAd]; // POBBannerView will load the next ad in 30 seconds
}

- (void)setOptOutFromSale:(BOOL)optOutFromSale {
  if (optOutFromSale != _optOutFromSale) {
    _optOutFromSale = optOutFromSale;
    _needsReloadBanner = YES;
  }
}

- (void)setAdOnScreen:(BOOL)adOnScreen {
  if (adOnScreen != _adOnScreen) {
    _adOnScreen = adOnScreen;
    _needsReloadBanner = YES;
  }
}

- (void)setAdUnitID:(NSString *)adUnitID {
  if(![adUnitID isEqual:_adUnitID]) {
    _adUnitID = adUnitID;
    _needsReloadBanner = YES;
  }
}

- (void)setSlotUUID:(NSString *)slotUUID {
  if(![slotUUID isEqual:_slotUUID]) {
    _slotUUID = slotUUID;
    _needsReloadBanner = YES;
  }
}

- (void)setAdSizeTag:(NSString *)adSizeTag {
  if(![adSizeTag isEqual:_adSizeTag]) {
    _adSizeTag = adSizeTag;
    _needsReloadBanner = YES;
  }
}

- (void)setAdSizes:(NSArray *)adSizes {
  // TODO: Haven't seen adSizes being set from JS. ???
  if(![adSizes isEqual:_adSizes]) {
    _adSizes = adSizes;
    _needsReloadBanner = YES;
  }
}

- (void)setDimensions:(NSDictionary *)dimensions {
  if(![dimensions isEqual:_dimensions]) {
    _dimensions = dimensions;
    _needsReloadBanner = YES;
  }
}

- (void)setTestDeviceID:(NSString *)testDeviceID {
  if([testDeviceID isEqualToString:@"EMULATOR"]) {
    GADMobileAds.sharedInstance.requestConfiguration.testDeviceIdentifiers = @[GADSimulatorID];
  } else if (testDeviceID) {
    GADMobileAds.sharedInstance.requestConfiguration.testDeviceIdentifiers = @[testDeviceID];
  }
  if(![testDeviceID isEqual:_testDeviceID]) {
    _testDeviceID = testDeviceID;
    _needsReloadBanner = YES;
  }
}

- (void)didSetProps:(NSArray<NSString *> *)changedProps {
  [super didSetProps:changedProps];
  if (_needsReloadBanner) {
    [self loadBanner];
    _needsReloadBanner = NO;
  }
}

- (void)removeBannerView {
  if(_bannerView) {
    [_bannerView removeFromSuperview];
    _bannerView.delegate = nil;
    _bannerView = nil;
  }
}

- (void)removeFromSuperview {
  [self removeBannerView];

  [super removeFromSuperview];
}

/// Tells the delegate an ad request loaded an ad.
- (void)bannerViewDidReceiveAd:(nonnull POBBannerView *)bannerView {
  localDebug(@"<%@> bannerViewDidReceiveAd (adSizeTag=%@) (slotUUID=%@)", _placement, _adSizeTag, _slotUUID);
//  CGRect rect = bannerView.frame;
//  rect.size = _bannerViewSize;
//  bannerView.frame = rect;
  _bannerView.frame = CGRectMake(self.bounds.origin.x, self.bounds.origin.y, _bannerViewSize.width, _bannerViewSize.height);
  localDebug(@"<%@> bannerViewDidReceiveAd _bannerView frame adjusted to: (%.0f, %.0f), (%.0f x %.0f)", _placement, _bannerView.frame.origin.x, _bannerView.frame.origin.y, _bannerView.frame.size.width, _bannerView.frame.size.height);

  if (self.onSizeChange) {
    self.onSizeChange(@{
      @"width": [NSNumber numberWithFloat: _bannerViewSize.width],
      @"height": [NSNumber numberWithFloat: _bannerViewSize.height]
                      });
  }
  if (self.onBannerViewDidReceiveAd) {
    self.onBannerViewDidReceiveAd(@{});
  }
  
  _openWrapBidding = YES; // OpenWrap will refresh its bidding automatically every 30 sec
  
  [self loadAmazonAd];
}

/// Tells the delegate an ad request failed.
- (void)bannerView:(POBBannerView *)bannerView didFailToReceiveAdWithError:(NSError *)error {
  localDebug(@"<%@> OpenWrap: didFailToReceiveAdWithError: %@", _placement, error.localizedDescription);
  if (self.onDidFailToReceiveAdWithError) {
    self.onDidFailToReceiveAdWithError(@{ @"error": [error localizedDescription] });
  }
  
  [self loadBanner];
}

- (nonnull UIViewController *)bannerViewPresentationController {
  return [UIApplication sharedApplication].delegate.window.rootViewController;
}


/// Tells the delegate that a full screen view will be presented in response
/// to the user clicking on an ad.
- (void)bannerViewWillPresentModal:(POBBannerView *)bannerView {
  localDebug(@"<%@> bannerViewWillPresentModal", _placement);
  if (self.onAdViewWillPresentScreen) {
    self.onAdViewWillPresentScreen(@{});
  }
}

/// Tells the delegate that the full screen view has been dismissed.
- (void)bannerViewDidDismissModal:(POBBannerView *)bannerView {
  localDebug(@"<%@> bannerViewDidDismissModal", _placement);
  if (self.onAdViewDidDismissScreen) {
    self.onAdViewDidDismissScreen(@{});
  }
}

/// Tells the delegate that a user click will open another app (such as
/// the App Store), backgrounding the current app.
- (void)bannerViewWillLeaveApplication:(POBBannerView *)adView {
  localDebug(@"<%@> bannerViewWillLeaveApplication", _placement);
  if (self.onAdViewWillLeaveApplication) {
    self.onAdViewWillLeaveApplication(@{});
  }
}

- (void)bidEvent:(id<POBBidEvent>)bidEventObject didFailToReceiveBidWithError:(NSError *)error {
  localDebug(@"<%@> bidEvent:didFailToReceiveBidWithError: %@", _placement, error.localizedDescription);
  _openWrapBidding = NO;
  [self didFinishLoadingOpenWrapBid];
}

- (void)bidEvent:(id<POBBidEvent>)bidEventObject didReceiveBid:(POBBid *)bid {
  localDebug(@"<%@> bidEvent:didReceiveBid", _placement);
  _openWrapBidding = NO;
  [self didFinishLoadingOpenWrapBid];
}

/// Called before the ad view changes to the new size.
- (void)adView:(POBBannerView *)bannerView willChangeAdSizeTo:(GADAdSize)size {
  localDebug(@"<%@> adView:willChangeAdSizeTo(%.1fx%.1f):", _placement, size.size.width, size.size.height);
  _bannerViewSize = size.size;
  if (self.onWillChangeAdSizeTo) {
    // bannerView calls this method on its adSizeDelegate object before the banner updates it size,
    // allowing the application to adjust any views that may be affected by the new ad size.
    self.onWillChangeAdSizeTo(@{
      @"width": [NSNumber numberWithFloat: size.size.width],
      @"height": [NSNumber numberWithFloat: size.size.height]
                      });
  }
}

- (void)adView:(POBBannerView *)banner didReceiveAppEvent:(NSString *)name withInfo:(NSString *)info {
  localDebug(@"<%@> adView:didReceiveAppEvent(%@):withInfo(%@):", _placement, name, info);
  NSMutableDictionary *myDictionary = [[NSMutableDictionary alloc] init];
  myDictionary[name] = info;
  if (self.onAdmobDispatchAppEvent) {
    self.onAdmobDispatchAppEvent(@{ name: info });
  }
}

- (BOOL)loadAmazonAd {
  localDebug(@"<%@> loadAmazonAd()...", _placement);
  if (!_slotUUID || [_slotUUID length] == 0) {
    localDebug(@"<%@> loadAmazonAd() aborted. _slotUUID is empty.", _placement);
    return NO;
  }
  
  GADAdSize gadAdSize = [self getAdSizeFromString:_adSizeTag];
  localDebug(@"<%@> loadAmazonAd() gadAdSize:(%.0fx%.0f), slotUUID:(%@)", _placement, gadAdSize.size.width, gadAdSize.size.height, _slotUUID);
  DTBAdSize *dtbAdSize = [[DTBAdSize alloc] initBannerAdSizeWithWidth:gadAdSize.size.width height:gadAdSize.size.height andSlotUUID:_slotUUID];
  if (!dtbAdSize) {
    localDebug(@"<%@> loadAmazonAd() aborted. dtbAdSize=NULL", _placement);
    return NO;
  }
  
  NSTimeInterval timeStamp = [[NSDate date] timeIntervalSince1970];
  DTBAds *dtbAds = [DTBAds sharedInstance];
  [dtbAds addCustomAttribute:@"sessDuration" value:[NSNumber numberWithInt:(timeStamp - [_sessionStartTimeSince1970InSec intValue])]];
  [dtbAds addCustomAttribute:@"impDepth" value:_sessionImpressionsCount];
  
  DTBAdLoader *adLoader = [[DTBAdLoader alloc] init];
  [adLoader setAdSizes:@[dtbAdSize]];
  if (_optOutFromSale) {
    [adLoader putCustomTarget:@"1YY" withKey:@"aps_privacy"];
  }
  _dtbCustomTargeting = NULL;
  _dtbAdLoading = YES;
  [adLoader loadAd:self]; // -> onSuccess: / onFailure:
  
  return YES;
}

- (void)onSuccess:(DTBAdResponse *)adResponse {
  localDebug(@"<%@> DTBAdLoader DTBAdResponse customTargeting.amzn_b: %@", _placement, adResponse.customTargeting[@"amzn_b"]);
  _dtbAdLoading = NO;
  _dtbCustomTargeting = adResponse.customTargeting;
  [self didFinishLoadingDTBAd];
}

- (void)onFailure:(DTBAdError)error {
  localDebug(@"<%@> DTBAdLoader DTBAdError: %d", _placement, error);
  _dtbAdLoading = NO;
  [self didFinishLoadingDTBAd];
}

@end

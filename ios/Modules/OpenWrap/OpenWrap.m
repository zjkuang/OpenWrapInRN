//
//  OpenWrap.m
//  OpenWrapInRN
//
//  Created by Zhengqian Kuang on 2022-03-27.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(GBOpenWrap, NSObject)

RCT_EXTERN_METHOD(initializeSDK:(NSString)storeURLString)

@end

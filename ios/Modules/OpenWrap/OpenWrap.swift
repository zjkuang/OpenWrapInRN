//
//  OpenWrap.swift
//  OpenWrapInRN
//
//  Created by Zhengqian Kuang on 2022-03-27.
//

import Foundation
import OpenWrapSDK

@objc(OpenWrap)
class OpenWrap: NSObject {

  @objc static func requiresMainQueueSetup() -> Bool {
    return false;
  }

  @objc(initializeSDK:)
  func
  initializeSDK(storeURLString: String) {
    if let storeURL = URL(string: storeURLString) {
      let appInfo = POBApplicationInfo()
      appInfo.storeURL = storeURL
      OpenWrapSDK.setApplicationInfo(appInfo)
    }
    
    #if DEBUG
    OpenWrapSDK.setLogLevel(.all)
    #endif
  }

}

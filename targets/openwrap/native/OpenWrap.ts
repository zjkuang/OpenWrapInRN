import {NativeModules, Platform} from 'react-native';

export type Country = 'US' | 'CA';

export interface OpenWrap {
  initializeSDK(country: Country): void;
}

interface NativeOpenWrap {
  initializeSDK(storeURLString: string): Promise<string>;
}

const BaseSDK: NativeOpenWrap = NativeModules.OpenWrap;
const openWrapCANStoreURL =
  'https://apps.apple.com/ca/app/gasbuddy-find-cheap-gas/id406719683';
const openWrapUSStoreURL =
  'https://apps.apple.com/us/app/gasbuddy-find-pay-for-gas/id406719683';
const openWrapAndroidStoreURL =
  'https://play.google.com/store/apps/details?id=gbis.gbandroid';
export const OpenWrap: OpenWrap = {
  initializeSDK(country: string) {
    if (Platform.OS === 'ios') {
      BaseSDK.initializeSDK(
        country === 'US' ? openWrapUSStoreURL : openWrapCANStoreURL,
      );
    } else if (Platform.OS === 'android') {
      // android has one link for both US and CAN store
      BaseSDK.initializeSDK(openWrapAndroidStoreURL).then(result => {
        console.log('OpenWrap.initializeSDK():', result);
      });
    }
  },
};

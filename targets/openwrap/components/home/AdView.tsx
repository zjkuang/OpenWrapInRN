import React from 'react';
import {View} from 'react-native';
import {AdSizeTag, DFPBanner} from '../../native';
import {AdPlacement, useAdSize, useAdSecrets} from './helper';
import {styles} from './style';

export type BannerViewFrame = {
  x: number;
  y: number;
  width: number;
  height: number;
};
export type AdViewProps = {
  placement: AdPlacement;
};
export const AdView = (props: AdViewProps) => {
  const adSecrets = useAdSecrets(props.placement);
  const adSize = useAdSize(props.placement);
  const onSizeChange = React.useCallback(e => {
    console.log('onSizeChange:', JSON.stringify(e.nativeEvent));
    const {width, height} = e.nativeEvent;
    console.log(`size: ${width} x ${height}`);
  }, []);
  const onBannerViewDidReceiveAd = React.useCallback(e => {
    console.log('onBannerViewDidReceiveAd:', JSON.stringify(e.nativeEvent));
    const frame: BannerViewFrame = e.nativeEvent;
    console.log(
      `frame: (${frame.x}, ${frame.y}), ${frame.width} x ${frame.height}`,
    );
  }, []);
  return (
    <View
      style={[styles.bottomAd, {width: adSize.width, height: adSize.height}]}>
      <DFPBanner
        adSizeTag={AdSizeTag.Banner}
        {...adSecrets}
        isOnScreen
        onSizeChange={onSizeChange}
        onBannerViewDidReceiveAd={onBannerViewDidReceiveAd}
      />
    </View>
  );
};

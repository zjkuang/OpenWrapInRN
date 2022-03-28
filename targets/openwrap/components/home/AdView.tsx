import React from 'react';
import {Platform, View} from 'react-native';
import {AdSizeTag, DFPBanner} from '../../native';
import {AdPlacement, useAdSize, useAdSecrets} from './helper';
import {styles} from './style';

type BannerViewSize = {
  width: number;
  height: number;
};
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
  const [viewSize, setViewSize] = React.useState<BannerViewSize>(
    useAdSize(props.placement),
  );
  const onSizeChange = React.useCallback(e => {
    const size: BannerViewSize = e.nativeEvent;
    console.log(`onSizeChange :: size: ${size.width} x ${size.height}`);
    setViewSize(size);
  }, []);
  const onBannerViewDidReceiveAd = React.useCallback(e => {
    const frame: BannerViewFrame = e.nativeEvent;
    console.log(
      `onBannerViewDidReceiveAd :: frame: (${frame.x}, ${frame.y}), ${frame.width} x ${frame.height}`,
    );
  }, []);

  const styleAdjustment = React.useMemo(() => {
    if (Platform.OS === 'android') {
      return {width: viewSize.width, height: viewSize.height};
    } else if (Platform.OS === 'ios') {
      if (props.placement === 'Home-Anchor') {
        return {width: 320, height: 50};
      } else if (props.placement === 'List-Inside') {
        return {width: 300, height: 250};
      } else if (props.placement === 'List-Bottom') {
        return {width: 300, height: 100};
      } else {
        return {};
      }
    }
  }, [props.placement, viewSize.height, viewSize.width]);

  return (
    <View style={[styles.bottomAd, styleAdjustment]}>
      {Platform.OS === 'android' ? (
        <DFPBanner
          adSizeTag={AdSizeTag.Banner}
          {...adSecrets}
          isOnScreen
          onSizeChange={onSizeChange}
          onBannerViewDidReceiveAd={onBannerViewDidReceiveAd}
        />
      ) : null}
    </View>
  );
};

import React from 'react';
import {View} from 'react-native';
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

  return (
    <View
      style={[
        styles.bottomAd,
        {width: viewSize.width, height: viewSize.height},
      ]}>
      <DFPBanner
        placement={props.placement}
        adSizeTag={
          props.placement === 'List-Inside'
            ? AdSizeTag.MediumRectangle
            : AdSizeTag.Banner
        }
        {...adSecrets}
        isOnScreen
        onSizeChange={onSizeChange}
        onBannerViewDidReceiveAd={onBannerViewDidReceiveAd}
      />
    </View>
  );
};

import React from 'react';
import {View} from 'react-native';
import {AdSizeTag, DFPBanner} from '../../native';
import {AdPlacement, useAdSize, useAdSecrets} from './helper';
import {styles} from './style';

export type AdViewProps = {
  placement: AdPlacement;
};

export const AdView = (props: AdViewProps) => {
  const adSecrets = useAdSecrets(props.placement);
  const adSize = useAdSize(props.placement);
  return (
    <View
      style={[styles.bottomAd, {width: adSize.width, height: adSize.height}]}>
      <DFPBanner
        adSizeTag={AdSizeTag.Banner}
        {...adSecrets}
        isOnScreen
        onSizeChange={event => {
          console.log('onSizeChange:', JSON.stringify(event));
        }}
      />
    </View>
  );
};

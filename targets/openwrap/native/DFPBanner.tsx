import React from 'react';
import {ViewProps, requireNativeComponent} from 'react-native';

export enum AdSizeTag {
  Invalid = 'Invalid',
  Banner = 'Banner',
  MediumRectangle = 'MediumRectangle',
}

interface DFPBannerNativeProps {
  //
}

const DFPBannerNative =
  requireNativeComponent<DFPBannerNativeProps>('DFPBannerNative');

export interface DFPBannerProps extends ViewProps {
  publisherID: string;
  profileID: number;
  isOnScreen?: boolean;
  adUnitID: string;
  slotUUID?: string;
  adSizeTag: AdSizeTag;
  placement?: string;
  onSizeChange?: (dims: Record<'width' | 'height', number>) => void;
  onBannerViewDidReceiveAd?: (
    frame: Record<'x' | 'y' | 'width' | 'height', number>,
  ) => void;
  onDidFailToReceiveAdWithError?: (error: string) => void;
}

export const DFPBanner = (props: DFPBannerProps) => {
  return <DFPBannerNative {...props} />;
};

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
  onSizeChange?: (dims: Record<'width' | 'height', number>) => void;
  onBannerViewDidReceiveAd?: (
    frame: Record<'x' | 'y' | 'width' | 'height', number>,
  ) => void;
  onDidFailToReceiveAdWithError?: (error: string) => void;
}

export const DFPBanner = (props: DFPBannerProps) => {
  const [events, setEvents] = React.useState<Partial<DFPBannerProps>>({});
  React.useEffect(() => {
    const newEvents: Partial<DFPBannerProps> = {};
    if (props.onSizeChange) {
      const nativeOnSizeChange: (e: any) => void = e => {
        props.onSizeChange?.(e.nativeEvent);
      };
      newEvents.onSizeChange = nativeOnSizeChange;
    }
    setEvents(newEvents);
  }, [props, props.onSizeChange]);
  return <DFPBannerNative {...props} {...events} />;
};

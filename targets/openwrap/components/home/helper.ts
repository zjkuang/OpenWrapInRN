import React from 'react';
import {useWindowDimensions} from 'react-native';

export type AdPlacement = 'Home-Anchor' | 'List-Inside' | 'List-Bottom';
export type Size = {
  width: number;
  height: number;
};

export const useAdSize = (placement: AdPlacement) => {
  const {width: windowWidth} = useWindowDimensions();
  const adSize = React.useMemo(() => {
    if (placement === 'Home-Anchor') {
      const size: Size = {
        width: 320,
        height: 50,
      };
      return size;
    } else if (placement === 'List-Inside') {
      const size: Size = {
        width: 300,
        height: 250,
      };
      return size;
    } else if (placement === 'List-Bottom') {
      const size: Size = {
        width: windowWidth,
        height: 100 * (windowWidth / 300),
      };
      return size;
    } else {
      const size: Size = {
        width: 0,
        height: 0,
      };
      return size;
    }
  }, [placement, windowWidth]);
  return adSize;
};

interface AdSecrets {
  publisherID: string;
  profileID: number;
  adUnitID: string;
  slotUUID: string;
}
export const useAdSecrets = (placement: AdPlacement) => {
  const adSecrets: AdSecrets = React.useMemo(() => {
    const secretsFromJSON = require('../../../../secrets.json');
    const publisherID: string =
      secretsFromJSON.PubMatic_PublisherID || '156276'; // test id: '156276'
    const profileID: number = secretsFromJSON.PubMatic_ProfileID || 1165; // test id: 1165
    let placementConfig;
    if (placement === 'Home-Anchor') {
      placementConfig = secretsFromJSON.HomeAnchorPortrait;
    } else if (placement === 'List-Inside') {
      placementConfig = secretsFromJSON.ListInside;
    } else if (placement === 'List-Bottom') {
      placementConfig = secretsFromJSON.ListBottom;
    }
    const networkKey: string = placementConfig.NetworkKey || '';
    const unitID: string = placementConfig.UnitId || '';
    const adUnitID: string = [networkKey, unitID].join('');
    const slotUUID: string = placementConfig.SlotUUId || '';
    const secrets: AdSecrets = {
      publisherID,
      profileID,
      adUnitID,
      slotUUID,
    };
    return secrets;
  }, [placement]);
  return adSecrets;
};

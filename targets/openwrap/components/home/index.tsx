import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  Text,
  TouchableOpacity,
  useColorScheme,
  View,
} from 'react-native';
import {AdSizeTag, DFPBanner} from '../../native';
import {AdPlacement, useAdSize, useAdSecrets} from './helper';
import {styles} from './style';

export const HomeComponent = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const [adPlacement, setAdPlacement] =
    React.useState<AdPlacement>('Home-Anchor');

  const selectorDecoration = React.useCallback(
    (placement: AdPlacement) => {
      return placement === adPlacement ? styles.topSelectorActive : {};
    },
    [adPlacement],
  );

  const adSecrets = useAdSecrets(adPlacement);
  const adSize = useAdSize(adPlacement);

  const onPressPlacement = React.useCallback(
    (placement: AdPlacement) => {
      if (placement !== adPlacement) {
        console.log('Change to', placement);
        setAdPlacement(placement);
      }
    },
    [adPlacement],
  );

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <View style={[styles.fullScreenContainer, styles.contentAlignmentCenter]}>
        <View style={styles.topSelectorContainer}>
          <TouchableOpacity
            style={[styles.topSelector, selectorDecoration('Home-Anchor')]}
            onPress={() => onPressPlacement('Home-Anchor')}>
            <Text style={styles.topSelectorText}>{'Home-Anchor'}</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.topSelector, selectorDecoration('List-Inside')]}
            onPress={() => onPressPlacement('List-Inside')}>
            <Text style={styles.topSelectorText}>{'List-Inside'}</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.topSelector, selectorDecoration('List-Bottom')]}
            onPress={() => onPressPlacement('List-Bottom')}>
            <Text style={styles.topSelectorText}>{'List-Bottom'}</Text>
          </TouchableOpacity>
        </View>
        <Text>OpenWrap Demo</Text>
        <View style={[styles.bottomAdContainer, {height: adSize.height}]}>
          {adPlacement === 'Home-Anchor' && (
            <View
              style={[
                styles.bottomAd,
                {width: adSize.width, height: adSize.height},
              ]}>
              <DFPBanner
                adSizeTag={AdSizeTag.Banner}
                {...adSecrets}
                isOnScreen
              />
            </View>
          )}
          {adPlacement === 'List-Inside' && (
            <View
              style={[
                styles.bottomAd,
                {width: adSize.width, height: adSize.height},
              ]}>
              <DFPBanner
                adSizeTag={AdSizeTag.MediumRectangle}
                {...adSecrets}
                isOnScreen
              />
            </View>
          )}
          {adPlacement === 'List-Bottom' && (
            <View
              style={[
                styles.bottomAd,
                {width: adSize.width, height: adSize.height},
              ]}>
              <DFPBanner
                adSizeTag={AdSizeTag.Banner}
                {...adSecrets}
                isOnScreen
              />
            </View>
          )}
        </View>
      </View>
    </SafeAreaView>
  );
};

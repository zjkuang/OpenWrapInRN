import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  Text,
  TouchableOpacity,
  useColorScheme,
  useWindowDimensions,
  View,
} from 'react-native';
import {styles} from './style';

type AdSizeTag = 'Banner' | 'Medium-Rectangle' | 'Fluid';
type Size = {
  width: number;
  height: number;
};

export const HomeComponent = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const {width: windowWidth} = useWindowDimensions();
  const [adSizeTag, setAdSizeTag] = React.useState<AdSizeTag>('Banner');

  const selectorDecoration = React.useCallback(
    (myAdSizeTag: AdSizeTag) => {
      return myAdSizeTag === adSizeTag ? styles.topSelectorActive : {};
    },
    [adSizeTag],
  );

  const adSize = React.useMemo(() => {
    if (adSizeTag === 'Banner') {
      const size: Size = {
        width: 320,
        height: 50,
      };
      return size;
    } else if (adSizeTag === 'Medium-Rectangle') {
      const size: Size = {
        width: 300,
        height: 250,
      };
      return size;
    } else if (adSizeTag === 'Fluid') {
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
  }, [adSizeTag, windowWidth]);

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <View style={[styles.fullScreenContainer, styles.contentAlignmentCenter]}>
        <View style={styles.topSelectorContainer}>
          <TouchableOpacity
            style={[styles.topSelector, selectorDecoration('Banner')]}
            onPress={() => {
              setAdSizeTag('Banner');
            }}>
            <Text style={styles.topSelectorText}>Anchor</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.topSelector, selectorDecoration('Medium-Rectangle')]}
            onPress={() => {
              setAdSizeTag('Medium-Rectangle');
            }}>
            <Text style={styles.topSelectorText}>Med-Rector</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.topSelector, selectorDecoration('Fluid')]}
            onPress={() => {
              setAdSizeTag('Fluid');
            }}>
            <Text style={styles.topSelectorText}>Fluid</Text>
          </TouchableOpacity>
        </View>
        <Text>OpenWrap Demo</Text>
        <View style={[styles.bottomAdContainer, {height: adSize.height}]}>
          <View
            style={[
              styles.bottomAd,
              {width: adSize.width, height: adSize.height},
            ]}>
            <Text>{adSizeTag}</Text>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
};

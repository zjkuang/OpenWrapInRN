import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  Text,
  TouchableOpacity,
  useColorScheme,
  View,
} from 'react-native';
import {styles} from './style';

type AdSizeTag = 'Banner' | 'Medium-Rectangle' | 'Fluid';

export const HomeComponent = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const [adSizeTag, setAdSizeTag] = React.useState<AdSizeTag>('Banner');

  const selectorDecoration = (myAdSizeTag: AdSizeTag) => {
    return myAdSizeTag === adSizeTag ? styles.topSelectorActive : {};
  };

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
        <View style={styles.bottomAdContainer}>
          <Text>{adSizeTag}</Text>
        </View>
      </View>
    </SafeAreaView>
  );
};

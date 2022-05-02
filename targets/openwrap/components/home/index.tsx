import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  Text,
  useColorScheme,
  View,
} from 'react-native';
import {AdPlacement, useAdSize} from './helper';
import {AdPlacementSelector} from './AdPlacementSelector';
import {AdView} from './AdView';
import {styles} from './style';

export const HomeComponent = () => {
  const isDarkMode = useColorScheme() === 'dark';
  const [adPlacement, setAdPlacement] =
    React.useState<AdPlacement>('Home-Anchor');

  const adSize = useAdSize(adPlacement);

  const onSelectAdPlacement = React.useCallback(
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
        <AdPlacementSelector onSelect={onSelectAdPlacement} />
        <View style={[styles.bottomAdContainer, {height: adSize.height}]}>
          {adPlacement === 'Home-Anchor' && <AdView placement={adPlacement} />}
          {adPlacement === 'List-Inside' && <AdView placement={adPlacement} />}
          {adPlacement === 'List-Bottom' && <AdView placement={adPlacement} />}
        </View>
      </View>
    </SafeAreaView>
  );
};

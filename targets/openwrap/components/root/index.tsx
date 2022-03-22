import React from 'react';
import {
  useColorScheme,
  StatusBar,
  SafeAreaView,
  Text,
  View,
} from 'react-native';
import {styles} from './style';

export const RootComponent = () => {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <View style={[styles.fullScreenContainer, styles.contentAlignmentCenter]}>
        <Text>OpenWrap Demo</Text>
      </View>
    </SafeAreaView>
  );
};

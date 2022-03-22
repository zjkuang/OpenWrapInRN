import {StyleSheet} from 'react-native';

export const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
  },
  fullScreenContainer: {
    width: '100%',
    height: '100%',
  },
  contentAlignmentCenter: {
    justifyContent: 'center', // primary axis
    alignItems: 'center', // secondary axis
  },
});

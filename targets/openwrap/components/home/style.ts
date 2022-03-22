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
  topSelectorContainer: {
    position: 'absolute',
    top: 0,
    width: '100%',
    height: 60,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  topSelector: {
    marginHorizontal: 8,
  },
  topSelectorText: {
    marginHorizontal: 8,
    marginVertical: 4,
  },
  topSelectorActive: {
    borderWidth: 1,
    borderRadius: 6,
    borderColor: 'orange',
  },
  bottomAdContainer: {
    position: 'absolute',
    bottom: 0,
    width: '100%',
    height: 50,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'orange',
  },
  bottomAd: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'cyan',
  },
});

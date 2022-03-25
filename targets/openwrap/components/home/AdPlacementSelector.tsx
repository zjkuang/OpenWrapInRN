import React from 'react';
import {Text, TouchableOpacity, View} from 'react-native';
import {AdPlacement} from './helper';
import {styles} from './style';

type AdPlacementSelectorProps = {
  onSelect: (placement: AdPlacement) => void;
};
export const AdPlacementSelector = (props: AdPlacementSelectorProps) => {
  const [adPlacement, setAdPlacement] =
    React.useState<AdPlacement>('Home-Anchor');

  const onSelect = React.useCallback(
    (placement: AdPlacement) => {
      if (placement !== adPlacement) {
        setAdPlacement(placement);
      }
    },
    [adPlacement],
  );

  React.useEffect(() => {
    props.onSelect(adPlacement);
  }, [adPlacement, props]);

  return (
    <View style={styles.topSelectorContainer}>
      <TouchableOpacity
        style={[
          styles.topSelector,
          adPlacement === 'Home-Anchor' ? styles.topSelectorActive : {},
        ]}
        onPress={() => onSelect('Home-Anchor')}>
        <Text style={styles.topSelectorText}>{'Home-Anchor'}</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={[
          styles.topSelector,
          adPlacement === 'List-Inside' ? styles.topSelectorActive : {},
        ]}
        onPress={() => onSelect('List-Inside')}>
        <Text style={styles.topSelectorText}>{'List-Inside'}</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={[
          styles.topSelector,
          adPlacement === 'List-Bottom' ? styles.topSelectorActive : {},
        ]}
        onPress={() => onSelect('List-Bottom')}>
        <Text style={styles.topSelectorText}>{'List-Bottom'}</Text>
      </TouchableOpacity>
    </View>
  );
};

import React from 'react';
import {OpenWrap} from '../../native';
import {HomeComponent} from '../home';

export const RootComponent = () => {
  React.useEffect(() => {
    OpenWrap.initializeSDK('US');
  }, []);

  return <HomeComponent />;
};

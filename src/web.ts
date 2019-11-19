import {WebPlugin} from '@capacitor/core';
import {WifiDirectPlugin, WifiP2pDevice} from './definitions';

export class WifiDirectWeb extends WebPlugin implements WifiDirectPlugin {
  constructor() {
    super({
      name: 'WifiDirect',
      platforms: ['web']
    });
  }

  startDiscoveringPeers(): Promise<void> {
    return new Promise<void>(resolve => {
      console.log('Start discovering');
      return resolve();
    });
  }

  stopDiscoveringPeers(): Promise<void> {
    return new Promise<void>(resolve => {
      console.log('Stop discovering');
      return resolve();
    });
  }

  connection(option: { device: WifiP2pDevice }): Promise<void> {
    return new Promise<void>(resolve => {
      console.log('Connection to ' + option.device.deviceName);
      return resolve();
    });
  }
}

const WifiDirect = new WifiDirectWeb();

export { WifiDirect };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(WifiDirect);

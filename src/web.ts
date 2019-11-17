import {WebPlugin} from '@capacitor/core';
import {WifiDirectPlugin, WifiP2pDevice} from './definitions';

export class WifiDirectWeb extends WebPlugin implements WifiDirectPlugin {
  constructor() {
    super({
      name: 'WifiDirect',
      platforms: ['web']
    });
  }

  discoverPeers(): Promise<{ devices: WifiP2pDevice[] }> {
    return new Promise<{ devices: WifiP2pDevice[] }>((resolve) => {
      return resolve({devices: []});
    });
  };
}

const WifiDirect = new WifiDirectWeb();

export { WifiDirect };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(WifiDirect);

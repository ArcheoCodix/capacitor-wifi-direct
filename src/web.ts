import { WebPlugin } from '@capacitor/core';
import { WifiDirectPlugin } from './definitions';

export class WifiDirectWeb extends WebPlugin implements WifiDirectPlugin {
  constructor() {
    super({
      name: 'WifiDirect',
      platforms: ['web']
    });
  }

  discoverPeers(): void {}
}

const WifiDirect = new WifiDirectWeb();

export { WifiDirect };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(WifiDirect);

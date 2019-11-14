import { WebPlugin } from '@capacitor/core';
import { WifiDirectPlugin } from './definitions';

export class WifiDirectWeb extends WebPlugin implements WifiDirectPlugin {
  constructor() {
    super({
      name: 'WifiDirect',
      platforms: ['web']
    });
  }

  discoverPeers(options: { value: string }): Promise<any> {
    console.log(options);
    return undefined;
  };
}

const WifiDirect = new WifiDirectWeb();

export { WifiDirect };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(WifiDirect);

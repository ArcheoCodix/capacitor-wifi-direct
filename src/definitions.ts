declare module "@capacitor/core" {
  interface PluginRegistry {
    WifiDirect: WifiDirectPlugin;
  }
}

export interface WifiDirectPlugin {
  discoverPeers(options: { value: string }): Promise<any>;
}

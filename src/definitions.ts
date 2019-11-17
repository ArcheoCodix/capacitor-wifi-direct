declare module "@capacitor/core" {
  interface PluginRegistry {
    WifiDirect: WifiDirectPlugin;
  }
}

export enum DeviceStatus {
    Available = 3,
    Connected = 0,
    Failed = 2,
    Invited = 1,
    Unavailable = 4
}

export interface WifiP2pDevice {
    deviceAddress: string;
    deviceName: string;
    primaryDeviceType: string;
    secondaryDeviceType: string;
    status: DeviceStatus;
}

export interface WifiDirectPlugin {
  discoverPeers(): Promise<{ devices: WifiP2pDevice[] }>;
  addListener(eventName: 'wifiState', listener: EventListener<{ devices: WifiP2pDevice[] }>): void;
}

import {PluginListenerHandle} from "@capacitor/core";

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

export enum FailReason {
  Busy = 2,
  Error = 0,
  NoServiceRequests = 3,
  P2pUnsupported = 1
}

export interface WifiP2pDevice {
    deviceAddress: string;
    deviceName: string;
    primaryDeviceType: string;
    secondaryDeviceType: string;
    status: DeviceStatus;
}

export interface WifiP2pInfo {
    groupFormed: boolean;
    isGroupOwner: boolean;
}

export interface WifiDirectPlugin {
  startDiscoveringPeers(): Promise<void>;
  stopDiscoveringPeers(): Promise<void>;
  connect(option: {device: WifiP2pDevice}): Promise<void>;
  disconnect(): Promise<void>;
  host(): Promise<void>;

  addListener(eventName: 'wifiStateChanged', listener: (state: {isEnabled: boolean}) => void): PluginListenerHandle;
  addListener(eventName: 'peersDiscovered', listener: (req: { devices: WifiP2pDevice[] }) => void): PluginListenerHandle;
  addListener(eventName: 'connectionInfoAvailable', listener: (info: WifiP2pInfo) => void): PluginListenerHandle;
}

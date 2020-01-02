import {CallbackID} from "@capacitor/core";

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

export enum FailureReason {
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

export declare type DiscoveryPeersWatchCallback = (req: { devices: WifiP2pDevice[] }, err?: FailureReason) => void;
export declare type WifiStateWatchCallback = (state: {isEnabled: boolean}) => void;
export declare type ConnectionInfoWatchCallback = (info: WifiP2pInfo, err?: FailureReason) => void;

export interface WifiDirectPlugin {
  startDiscoveringPeers(callback: DiscoveryPeersWatchCallback): CallbackID;
  stopDiscoveringPeers(): Promise<void>;
  connect(option: {device: WifiP2pDevice}, callback: ConnectionInfoWatchCallback): CallbackID;
  // disconnect(): Promise<void>;
  host(): Promise<void>;
  startWatchConnectionInfo(callback: ConnectionInfoWatchCallback): CallbackID;
  startWatchWifiState(callback: WifiStateWatchCallback): CallbackID;

  clearInfoConnectionWatch(option: {id: CallbackID}): Promise<void>;
  clearWifiStateWatch(option: {id: CallbackID}): Promise<void>;
}

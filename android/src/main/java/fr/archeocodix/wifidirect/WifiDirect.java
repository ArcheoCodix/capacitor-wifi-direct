package fr.archeocodix.wifidirect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@NativePlugin(
        permissions={
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
        }
)
public class WifiDirect extends Plugin {

    final String WIFI_STATE_EVENT = "wifiStateChanged";
    final String CONNECTION_INFO_EVENT = "connectionInfoAvailable";

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    Context context;
    IntentFilter intentFilter;

    Map<String, PluginCall> watchingPeersCalls = new HashMap<>();
    PluginCall watchingPeersDiscover;

    @Override
    public void load() {
        super.load();

        if (!hasRequiredPermissions()) {
            pluginRequestAllPermissions();
        }

        this.context = getContext();

        this.manager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = manager.initialize(this.context, this.context.getMainLooper(), null);
        this.receiver = new WiFiDirectBroadcastReceiver(this.manager, this.channel, this);

        this.intentFilter = new IntentFilter();
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    // Call WifiP2pManager.requestPeers() to get a list of current peers
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            processPeers((WifiP2pDevice[]) wifiP2pDeviceList.getDeviceList().toArray());
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            JSObject connectionInfo = new JSObject();

            connectionInfo.put("groupFormed", info.groupFormed);
            connectionInfo.put("isGroupOwner", info.isGroupOwner);
            connectionInfo.put("hostAddress", groupOwnerAddress.getHostAddress());

            notifyListeners(CONNECTION_INFO_EVENT, connectionInfo);
        }
    };

    @PluginMethod(returnType=PluginMethod.RETURN_CALLBACK)
    public void startDiscoveringPeers(final PluginCall call) {
        if (watchingPeersDiscover != null) {
            watchingPeersDiscover.release(bridge);
            watchingPeersDiscover = null;
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                call.save();
                watchingPeersDiscover = call;
            }

            @Override
            public void onFailure(int reason) {
                call.reject(String.valueOf(reason));
            }
        });
    }

    @PluginMethod()
    public void stopDiscoveringPeers(final PluginCall call) {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                watchingPeersDiscover.release(bridge);
                watchingPeersDiscover = null;
                call.success();
            }

            @Override
            public void onFailure(int reason) {
                call.reject(String.valueOf(reason));
            }
        });
    }

    @PluginMethod()
    public void connect(final PluginCall call) {
        if (!call.getData().has("device")) {
            call.reject("Must provide a device want to connect");
            return;
        }

        JSObject device = call.getObject("device");

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.getString("deviceAddress");

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                call.success();
            }

            @Override
            public void onFailure(int reason) {
                call.reject(String.valueOf(reason));
            }
        });
    }

    @PluginMethod()
    public void disconnect(final PluginCall call) {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                call.success();
            }

            @Override
            public void onFailure(int reason) {
                call.reject(String.valueOf(reason));
            }
        });
    }

    @PluginMethod()
    public void host(final PluginCall call) {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                call.success();
            }

            @Override
            public void onFailure(int reason) {
                call.reject(String.valueOf(reason));
            }
        });
    }

    private void startPeersWatch(PluginCall call) {
        watchingPeersCalls.put(call.getCallbackId(), call);
    }

    @PluginMethod()
    public void clearPeersWatch(PluginCall call) {
        String callbackId = call.getString("id");
        if (callbackId != null) {
            PluginCall removed = watchingPeersCalls.remove(callbackId);
            if (removed != null) {
                removed.release(bridge);
            }
        }

        call.success();
    }

    private void processPeers(WifiP2pDevice[] devices) {
        JSObject jsDevices = deviceArrayToJSObject(devices);

        if (watchingPeersDiscover != null) watchingPeersDiscover.success(jsDevices);

        for (Map.Entry<String, PluginCall> watch : watchingPeersCalls.entrySet()) {
            watch.getValue().success(jsDevices);
        }
    }

    private JSObject deviceArrayToJSObject(WifiP2pDevice[] devices) {
        JSArray deviceArray = new JSArray();

        for (WifiP2pDevice device : devices) {
            JSObject obj = new JSObject();

            obj.put("deviceAddress", device.deviceAddress);
            obj.put("deviceName", device.deviceName);
            obj.put("primaryDeviceType", device.primaryDeviceType);
            obj.put("secondaryDeviceType", device.secondaryDeviceType);
            obj.put("status", device.status);

            deviceArray.put(obj);
        }

        JSObject ret = new JSObject();

        ret.put("devices", deviceArray);

        return ret;
    }

    protected void sendConnectionState(boolean isWifiEnabled) {
        JSObject state = new JSObject();
        state.put("isEnabled", isWifiEnabled);
        notifyListeners(WIFI_STATE_EVENT, state);
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        context.unregisterReceiver(receiver);
    }
}

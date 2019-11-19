package fr.archeocodix.wifidirect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    final String PEERS_DISCOVERED_EVENT = "peersDiscovered";
    final String WIFI_STATE_EVENT = "wifiStateChanged";

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;

    Context context;
    IntentFilter intentFilter;

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
            Iterator<WifiP2pDevice> list = wifiP2pDeviceList.getDeviceList().iterator();
            JSArray deviceArray = new JSArray();

            for (; list.hasNext();) {
                WifiP2pDevice device = list.next();
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

            notifyListeners(PEERS_DISCOVERED_EVENT, ret);
        }
    };

    @PluginMethod()
    public void startDiscoveringPeers(final PluginCall call) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                call.success();
            }

            @Override
            public void onFailure(int reasonCode) {
                call.reject("error - reason code : " + reasonCode);
            }
        });
    }

    @PluginMethod()
    public void stopDiscoveringPeers(final PluginCall call) {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                call.success();
            }

            @Override
            public void onFailure(int reasonCode) {
                call.reject("error - reason code : " + reasonCode);
            }
        });
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

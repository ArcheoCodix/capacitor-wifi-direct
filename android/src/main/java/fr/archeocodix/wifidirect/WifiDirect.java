package fr.archeocodix.wifidirect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin()
public class WifiDirect extends Plugin {

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;

    Context context;
    IntentFilter intentFilter;

    @Override
    public void load() {
        super.load();

        Log.i("Load", "start");

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

    @PluginMethod()
    public void discoverPeers(PluginCall call) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i("onSuccess", "Yes");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.i("onFailure", String.valueOf(reasonCode));
            }
        });
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        Log.i("resume", "on resume");
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        Log.i("pause", "on pause");
        context.unregisterReceiver(receiver);
    }
}

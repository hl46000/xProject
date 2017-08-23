package com.purehero.android.vcard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by purehero on 2017-08-18.
 */

public class WifiDirectActivity extends Activity implements View.OnClickListener {

    private WiFiDirectBroadcastReceiver receiver;
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;

    private TextView tvWifiInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView( R.layout.activity_wifi_direct);

        int btnIDs[] = { R.id.btnScan };
        for( int btn_id : btnIDs ) {
            Button btn = ( Button ) this.findViewById( btn_id );
            if( btn != null ) {
                btn.setOnClickListener( this );
            }
        }

        tvWifiInfo = ( TextView ) findViewById( R.id.textWifiInfo );

        wifiManager     = ( WifiManager ) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if( !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled( true );
        }

        wifiP2pManager  = ( WifiP2pManager ) getApplicationContext().getSystemService( Context.WIFI_P2P_SERVICE );
        wifiP2pChannel  = wifiP2pManager.initialize( this, getMainLooper(), null );

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        tvWifiInfo.setText( "WiFi Status : " + wifiP2pManager.toString());

        /*
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration config : configs ) {
            tvWifiInfo.append("\n\n " + config.toString());
        }
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        receiver = new WiFiDirectBroadcastReceiver();
        registerReceiver( receiver, intentFilter );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver( receiver );
    }


    @Override
    public void onClick(View v) {
        switch( v.getId()) {
            case R.id.btnScan :
                wifiP2pManager.discoverPeers( wifiP2pChannel, new WifiP2pManager.ActionListener(){
                    @Override
                    public void onSuccess() {
                        Log.d( "WIFI_DIRECT", "Success discover peers" );
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d( "WIFI_DIRECT", "Failure discover peers" );
                    }
                });
                break;
        }
    }

    class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d( "WIFI_DIRECT", "WiFiDirectBroadcastReceiver::onReceive" );

            String action = intent.getAction();
            if( WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals( action)) {

            } else if( WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals( action)) {
                if( wifiP2pManager != null ) {
                    wifiP2pManager.requestPeers( wifiP2pChannel, peerListListener);
                }
                Log.d( "WIFI_DIRECT", "P2P Peers changed");

            } else if( WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals( action)) {

            } else if( WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals( action)) {

            }

            Log.d( "WIFI_DIRECT", intent.toString());
        }
    };

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList ) {
            Collection<WifiP2pDevice> deviceList = peerList.getDeviceList();
            if( deviceList != null ) {
                for( WifiP2pDevice device : deviceList ) {
                    Log.d( "WIFI_DIRECT", device.toString());
                }
            }

            peers.clear();
            peers.addAll( deviceList );
        }
    };
}

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purehero.bluetooth;

import java.io.IOException;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.purehero.bluetooth.contact_share.R;
import com.purehero.common.G;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class BluetoothDeviceListActivity extends ActionBarActivity implements OnClickListener {
    
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothManager.getInstance().initialize( this );
        
        // Setup the window
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        int btnIDs[] = { R.id.button_scan, R.id.btnDiscoverable };
        for( int id : btnIDs ) {
        	Button button = (Button) findViewById( id );
        	if( button == null ) continue;
        	
        	button.setOnClickListener( this );
        }
        

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction( BluetoothDevice.ACTION_ACL_CONNECTED );
		filter.addAction( BluetoothDevice.ACTION_ACL_DISCONNECTED );
		filter.addAction( BluetoothDevice.ACTION_NAME_CHANGED );
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction( BluetoothAdapter.ACTION_STATE_CHANGED );
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Get a set of currently paired devices
        Set<BluetoothDevice> bluetoothDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (bluetoothDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : bluetoothDevices) {
            	mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            	BluetoothManager.getInstance().addDevice( device );
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
        
        displayDeviceName();
        
        if( !BluetoothManager.getInstance().enableDevice()) {
			BluetoothManager.getInstance().requestEnable();
		}
    }

    private void displayDeviceName() {
    	BluetoothDeviceListActivity.this.runOnUiThread( new Runnable(){
			@Override
			public void run() {
				TextView myDeviceContent = ( TextView ) findViewById(R.id.content_my_device);
		        if( myDeviceContent != null ) {
		        	myDeviceContent.setText( mBtAdapter.getName());
		        }
			}});
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bluetooth, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_change_device_name) {
			G.textInputDialog( this, "디바이스명 변경", "", mBtAdapter.getName(), 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch( which ) {
					case G.DIALOG_BUTTON_ID_YES :
						mBtAdapter.setName( G.getTextInputDialogResult());
						displayDeviceName();
						break;
					}
				}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    private void cancelDiscovery() {
    	G.Log(  "cancelDiscovery()");
    	
    	// Indicate scanning in the title
    	setSupportProgressBarIndeterminateVisibility( false );
    	setTitle(R.string.bluetooth );
        
        // If we're already discovering, stop it
        if ( mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
    }
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
    	G.Log(  "doDiscovery()");

        // Indicate scanning in the title
    	setSupportProgressBarIndeterminateVisibility(true);
    	setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mNewDevicesArrayAdapter.clear();
        
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            BluetoothDevice device = BluetoothManager.getInstance().getDevice( address );
			if( device != null ) {
				try {
					BluetoothManager.getInstance().connectDevice( device, true );
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText( BluetoothDeviceListActivity.this, "연결 실패", Toast.LENGTH_LONG ).show();
				}
			}	
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            G.Log( "onReceive action : " + action );
            
            // When discovery finds a device
            switch( action ) {
            // When discovery is finished, change the Activity title
            case BluetoothAdapter.ACTION_STATE_CHANGED :
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :
            	setSupportProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
                
                Button scanButton = (Button) findViewById(R.id.button_scan);
                scanButton.setText( R.string.button_scan );
                        	
                break;
            default :
            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                G.Log( device.getName() + ", " + device.getAddress());
        		
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
        			String listData = device.getName() + "\n" + device.getAddress();
        			if( 0 > mNewDevicesArrayAdapter.getPosition( listData )) {
        				mNewDevicesArrayAdapter.add( listData );
        			}
        			
        			BluetoothManager.getInstance().addDevice( device );
                }
            	break;
            }
        }
    };

	@Override
	public void onClick(View view) {
		Button button = null;
		
		switch( view.getId()) {
		case R.id.button_scan :
			button = ( Button ) view;
			if( mBtAdapter.isDiscovering()) {
            	cancelDiscovery();
            	button.setText( R.string.button_scan );
            } else {
            	doDiscovery();                	
            	button.setText( R.string.button_cancel );
            }
			break;
			
		case R.id.btnDiscoverable :
			G.Log("click btnDiscoverable");
			
			button = ( Button ) view;
			
			if( BluetoothManager.getInstance().enableDevice()) {
				BluetoothManager.getInstance().discoverableDevice( 300 );				
			} else {
				BluetoothManager.getInstance().requestEnable();
			}
			
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { 
		case BluetoothManager.REQUEST_ENABLE_BT: // When the request to enable Bluetooth returns 
			if (resultCode == Activity.RESULT_OK) { // 확인 눌렀을 때 //Next Step 
				try {
					BluetoothManager.getInstance().startReceiveClient();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else { // 취소 눌렀을 때 
				Toast.makeText( this, "블루투스가 활성화되지 않아서 앱을 종료 합니다.", Toast.LENGTH_LONG ).show();
				this.finish();
			} 
			break;
		case BluetoothManager.REQUEST_DISCOVERABLE_DEVICE :
			G.Log("REQUEST_DISCOVERABLE_DEVICE %d", resultCode );
			
			if( resultCode != 0 ) {
				int duration = resultCode;
				new DiscoverableDeviceThread( BluetoothDeviceListActivity.this, (Button) findViewById(R.id.btnDiscoverable ), duration ).start();
				
				try {
					BluetoothManager.getInstance().startReceiveClient();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	
}

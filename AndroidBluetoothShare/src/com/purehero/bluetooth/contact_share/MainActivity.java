package com.purehero.bluetooth.contact_share;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.purehero.bluetooth.BluetoothManager;
import com.purehero.bluetooth.contact_share.R;
import com.purehero.common.BaseTabMainActivity;
import com.purehero.common.G;
import com.purehero.common.ViewPagerAdapter;
import com.purehero.contact.ContactAdapter;

public class MainActivity extends BaseTabMainActivity {
	private ContactAdapter 			contactAdapter = null;
	private RemoteContactAdapter	remoteContactAdapter = null;
	
	TextView status = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		status = ( TextView ) findViewById( R.id.tvStatue );
		
		G.init( this );
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		G.Log( "onActivityResult %d %d", requestCode, resultCode );
		if( requestCode == 100 ) {
			if( contactAdapter != null ) {
				contactAdapter.getContactDatas();
			}
			if( remoteContactAdapter != null ) {
				remoteContactAdapter.deleteCacheFiles();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void setStatusMessage( int stringResID ) {
		if( status != null ) {
			status.setText( stringResID );
		}
	}
	public void setStatusMessage( String message ) {
		if( status != null ) {
			status.setText( message );
		}
	}
	
	@Override
	public void addTabItems(ViewPagerAdapter adapter) {
		adapter.addItem( new ContactFragment( this ), getString( R.string.my_device ));
		adapter.addItem( new RemoteContactFragment( this ), getString( R.string.remote_device ));
		adapter.addItem( new ContactBackupFragment( this ), getString( R.string.backup ));
	}
	
	DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch( which ) {
			case G.DIALOG_BUTTON_ID_YES : 
				G.Log( "input dialog result : %s", G.getTextInputDialogResult());
				break;
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		BluetoothManager.getInstance().release();
		super.onDestroy();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		//return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.btConnect) {
			BluetoothManager.getInstance().openDeviceList( this );
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setContactAdapter( ContactAdapter adapter ) {
		contactAdapter = adapter;
	}
	
	public ContactAdapter getContactAdapter() {
		return contactAdapter;
	}
	
	public void setRemoteContactAdapter( RemoteContactAdapter adapter ) {
		remoteContactAdapter = adapter;
	}
	
	public RemoteContactAdapter getRemoteContactAdapter() {
		return remoteContactAdapter;
	}
}

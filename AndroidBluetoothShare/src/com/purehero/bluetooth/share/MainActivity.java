package com.purehero.bluetooth.share;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.purehero.bluetooth.BluetoothManager;
import com.purehero.common.BaseTabMainActivity;
import com.purehero.common.G;
import com.purehero.common.ViewPagerAdapter;
import com.purehero.contact.ContactAdapter;

public class MainActivity extends BaseTabMainActivity {
	private ContactAdapter contactAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		G.init( this );
	}
	
	@Override
	public void addTabItems(ViewPagerAdapter adapter) {
		adapter.addItem( new ContactFragment( this ), getString( R.string.contact ));
		adapter.addItem( new RemoteContactFragment( this ), getString( R.string.remote ));
		adapter.addItem( new BluetoothChatFragment( this ), getString( R.string.chat ));
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
}

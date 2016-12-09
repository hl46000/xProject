package com.example.myandroidsampleapp00;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallBroadcastReceiver extends BroadcastReceiver {

	private static final String YOUR_PACKAGE_NAME = "com.example.myandroidsampleapp00";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d( "TEST", "onReceive" );
		
		String action = null;
        if (intent != null) {
            action = intent.getAction();
        }

        if (action != null && Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String dataString = intent.getDataString();
            
            if( dataString != null ) {
            	Log.d( "TEST", dataString );
            	if ( dataString.equals(YOUR_PACKAGE_NAME)) {
            		Log.d( "TEST", "InstallBroadcastReceiver::onReceive");
            	}
            }
        }

	}

}

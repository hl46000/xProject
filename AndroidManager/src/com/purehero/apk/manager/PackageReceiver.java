package com.purehero.apk.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		String action = intent.getAction();
		
		String time = android.text.format.DateFormat.format("yyyy/MM/dd h:mm a", System.currentTimeMillis()).toString();
		if(action.equals(Intent.ACTION_PACKAGE_ADDED)) {
			G.log( time + " Package ADDED : " + packageName);
						
		} else if( action.equals( Intent.ACTION_PACKAGE_REMOVED)) {
			G.log( time + " Package REMOVED : " + packageName);
			
		} else if( action.equals( Intent.ACTION_PACKAGE_CHANGED)) {
			G.log( time + " Package CHANGED : " + packageName);
			
		} else if( action.equals( Intent.ACTION_PACKAGE_REPLACED)) {
			G.log( time + " Package REPLACED : " + packageName );
			
		}
	}

}

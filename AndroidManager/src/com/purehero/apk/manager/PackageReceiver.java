package com.purehero.apk.manager;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName 	= intent.getData().getSchemeSpecificPart();
		String action 		= intent.getAction();
		String app_name		= "unknown";
		
		String reg_time = android.text.format.DateFormat.format( G.DATE_FORMAT, System.currentTimeMillis()).toString();
		int nIdx = action.lastIndexOf("_");
		action = action.substring( nIdx + 1 );
		
		File base_folder 	= new File( context.getCacheDir(), "package" );
		File folder			= new File( base_folder, packageName );
		if( !folder.exists()) {
			folder.mkdirs();
		}
				
		app_name = G.readFile( new File( folder, "app_name"));
		if( app_name == null ) {
			PackageManager packageManager = context.getApplicationContext().getPackageManager();
			try {
				app_name = (String) packageManager.getApplicationLabel( packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
			} catch (NameNotFoundException e) {
				app_name		= "unknown";
			}
		}
		
		G.log( String.format( "[%s] Package %s : %s[%s]", reg_time, action, app_name, packageName ));
		
		ApkHistoryDB apk_history = new ApkHistoryDB( context, 1 );
		apk_history.inset( packageName, app_name, action, reg_time );
	}
}
package com.purehero.root.checker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.purehero.module.tabhost.FragmentEx;

public class DeviceInfoFragment extends FragmentEx {
		
	private MainActivity context = null;
	private View layout = null;
	private ListView deviceInfoListView 				= null;
	private DeviceInfoListAdapter deviceInfoListAdapter = null;

	public Fragment setMainActivity( MainActivity mainActivity ) {
		context = mainActivity;
		deviceInfoListAdapter = new DeviceInfoListAdapter( context );
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.device_info_layout, container, false); 
		
		deviceInfoListView = ( ListView ) layout.findViewById( R.id.deviceInfoListView );
		if( deviceInfoListView != null ) {
			deviceInfoListView.setAdapter( deviceInfoListAdapter );
		}
		
		TextView textDeviceName = ( TextView ) layout.findViewById( R.id.textDeviceName );
		if( textDeviceName != null ) {
			textDeviceName.setText( "\n"+AndroidDeviceInfo.getDeviceName(context)+"\n" );
		}
		return layout;	
	}
}

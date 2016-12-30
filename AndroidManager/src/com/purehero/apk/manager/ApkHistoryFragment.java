package com.purehero.apk.manager;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ApkHistoryFragment extends Fragment {
	private final MainActivity context;
	private View layout = null;
	private ListView apkHistoryListView 					= null;
	private ApkHistoryListAdapter apkHistoryListAdapter 	= null;
	
	public ApkHistoryFragment(MainActivity mainActivity) {
		context = mainActivity; 
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ApkHistoryDB db = new ApkHistoryDB(context, 1 ); 
		apkHistoryListAdapter = new ApkHistoryListAdapter( context, db );
		
		layout 				= inflater.inflate( R.layout.apk_history_layout, container, false); 
		apkHistoryListView	= ( ListView ) layout.findViewById( R.id.apkHistoryListView );		 
		apkHistoryListView.setAdapter( apkHistoryListAdapter );
		
		registerForContextMenu( apkHistoryListView );
		
		return layout;	
	}

	public void removeAllItems() {
		ApkHistoryDB db = new ApkHistoryDB( context, 1 );
		db.removeAll();
		apkHistoryListAdapter.changeCursor( db.selectAll());
		
		new Handler().postDelayed( new Runnable(){
			@Override
			public void run() {
				apkHistoryListAdapter.notifyDataSetChanged();
			}}, 1000 );
	}
}

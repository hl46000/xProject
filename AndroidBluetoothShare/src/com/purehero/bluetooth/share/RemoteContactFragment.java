package com.purehero.bluetooth.share;

import com.purehero.common.G;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class RemoteContactFragment extends Fragment {
	private final MainActivity context;
	private View layout = null;

	private ListView listView = null;
	private ProgressBar progressBar = null;
	private final RemoteContactAdapter adapter;
	
	public RemoteContactFragment(MainActivity mainActivity) {
		context = mainActivity;
		adapter = new RemoteContactAdapter( context );
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		G.Log( "onCreateView" );
		
		layout 		= inflater.inflate( R.layout.remote_contact_list, container, false);
		listView	= ( ListView ) layout.findViewById( R.id.listView );
		progressBar	= ( ProgressBar ) layout.findViewById( R.id.progressBar );
		listView.setAdapter( adapter );
		
		new Thread( getContactRunnable ).start();
		
		return layout;
	}
	
	Runnable getContactRunnable = new Runnable() {
		@Override
		public void run() {
			G.Log( "run" );			

			context.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					G.Log( "runOnUiThread run" );
					adapter.notifyDataSetChanged();
					progressBar.setVisibility( View.INVISIBLE );
				}});
		}
	};
}

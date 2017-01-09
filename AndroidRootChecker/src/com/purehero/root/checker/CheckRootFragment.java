package com.purehero.root.checker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CheckRootFragment extends Fragment implements OnClickListener {
		
	private MainActivity context = null;
	private View layout = null;
	private TextView textResultCheckRoot = null; 
	
	public CheckRootFragment(MainActivity mainActivity) {
		context = mainActivity; 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.check_root_layout, container, false); 
	
		if( layout != null ) {
			textResultCheckRoot = ( TextView ) layout.findViewById( R.id.textResultCheckRoot );
			if( textResultCheckRoot != null ) {
				textResultCheckRoot.setText( AndroidDeviceInfo.isRooted() ? "Device is rooted" : "Device is not rooted");
			}
			
			Button btnCheckRoot = ( Button ) layout.findViewById( R.id.btnCheckRoot );
			if( btnCheckRoot != null ) {
				btnCheckRoot.setOnClickListener( this );
			}
		}
		return layout;	
	}
	
	private int check_count = 0;
	
	@Override
	public void onClick(View view) {
		switch( view.getId()) {
		case R.id.btnCheckRoot : 
			if( textResultCheckRoot != null ) {
				textResultCheckRoot.setText( "Checking root" );
				check_count = 0;
				
				new Handler().postDelayed( check_root_runnable, 1000 );
			}
			break;
		}
		
	}
	
	Runnable check_root_runnable = new Runnable() {
		@Override
		public void run() {
			if( check_count > 2 ) {
				context.runOnUiThread( new Runnable(){
					@Override
					public void run() {
						textResultCheckRoot.setText( AndroidDeviceInfo.isRooted() ? "Device is rooted" : "Device is not rooted");
					}});
				
			} else {
				context.runOnUiThread( new Runnable(){
					@Override
					public void run() {
						textResultCheckRoot.setText( textResultCheckRoot.getText() + "." );
						check_count++;
						
						new Handler().postDelayed( check_root_runnable, 1000 );
					}});
			}
		}
		
	};
}

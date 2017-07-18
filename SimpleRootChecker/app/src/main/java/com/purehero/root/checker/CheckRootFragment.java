package com.purehero.root.checker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.purehero.module.fragment.FragmentEx;

public class CheckRootFragment extends FragmentEx implements OnClickListener {
		
	private MainActivity context = null;
	private View layout = null;
	private TextView textResultCheckRoot = null; 

	public Fragment setMainActivity( MainActivity mainActivity ) {
		context = mainActivity;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate( R.layout.check_root_layout, container, false); 
	
		if( layout != null ) {
			textResultCheckRoot = ( TextView ) layout.findViewById( R.id.textResultCheckRoot );
			updateRootCheckMessage();
			
			Button btnCheckRoot = ( Button ) layout.findViewById( R.id.btnCheckRoot );
			if( btnCheckRoot != null ) {
				btnCheckRoot.setOnClickListener( this );
			}
		}
		return layout;	
	}
	
	private void updateRootCheckMessage() {
		if( textResultCheckRoot == null ) return;
		
		boolean isRooted = AndroidDeviceInfo.isRooted();
		if( isRooted ) {
			textResultCheckRoot.setTextColor( Color.RED );
			textResultCheckRoot.setText( "Device is rooted" );
		} else {
			textResultCheckRoot.setTextColor( Color.GREEN );
			textResultCheckRoot.setText( "Device is not rooted" );
		}
	}
	
	private int check_count = 0;
	
	@Override
	public void onClick(View view) {
		switch( view.getId()) {
		case R.id.btnCheckRoot : 
			if( textResultCheckRoot != null ) {
				textResultCheckRoot.setTextColor( Color.BLUE );
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
						updateRootCheckMessage();
					}});
				
			} else {
				context.runOnUiThread( new Runnable(){
					@Override
					public void run() {
						textResultCheckRoot.setTextColor( Color.BLUE );
						textResultCheckRoot.setText( textResultCheckRoot.getText() + "." );
						check_count++;
						
						new Handler().postDelayed( check_root_runnable, 1000 );
					}});
			}
		}
		
	};
}

package com.purehero.bluetooth;

import com.purehero.bluetooth.share.G;

import android.app.Activity;
import android.widget.Button;

public class DiscoverableDeviceThread extends Thread {
	private final Activity act;
	private final Button btn;
	private final String orgButtonTitle;
	private int duration = 300;
	
	public DiscoverableDeviceThread( Activity act, Button btn, int duration ) {
		this.act = act;
		this.btn = btn;
		this.orgButtonTitle = (String) btn.getText();
		this.duration = duration;
	}
	
	@Override
	public void run() {
		G.Log( "DiscoverableDeviceThread start" );
		while( duration > 0 ) {
			try {
				Thread.sleep( 1000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			duration--;
			act.runOnUiThread( action );
		}		
		
		G.Log( "DiscoverableDeviceThread end" );
	}
	
	Runnable action = new Runnable() {
		@Override
		public void run() {
			btn.setText( duration > 0 ? String.format( "%s(%d초)", orgButtonTitle, duration ) : orgButtonTitle );
		}
	};
}

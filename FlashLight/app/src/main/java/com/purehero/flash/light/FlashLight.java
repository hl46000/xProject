package com.purehero.flash.light;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

public class FlashLight implements FlashLightInterface {
	protected final Context context;
	private Camera camera = null;
	private boolean flashOn = false;
	
	public FlashLight( Context context ) {
		this.context = context;
	}
	
	public boolean init() {
		if( !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH )) {
			return false;
		}
		
		camera = Camera.open();
		return true;
	}
	
	public void setFlashLight( boolean onOff ) {
		if( camera == null ) return;
		flashOn = onOff;
		
		try {
			Parameters p = camera.getParameters();
			p.setFlashMode( flashOn ? Parameters.FLASH_MODE_TORCH : Parameters.FLASH_MODE_OFF);
			camera.setParameters(p);
			
			if( onOff ) {
				camera.startPreview();
			} else {
				camera.stopPreview();
			}
		} catch( Exception e ) {
		}
	}
	
	public void release() {
		if( camera == null ) return;
		camera.release();
	}

	@Override
	public void toggleFlashLight() {
		setFlashLight( !flashOn );		
	}
}

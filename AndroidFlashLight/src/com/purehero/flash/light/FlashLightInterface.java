package com.purehero.flash.light;

public interface FlashLightInterface {
	public boolean init() ;
	public void setFlashLight( boolean onOff );
	public void release();
	public void toggleFlashLight();
}

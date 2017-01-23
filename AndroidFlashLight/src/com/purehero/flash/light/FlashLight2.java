package com.purehero.flash.light;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Size;
import android.view.Surface;

public class FlashLight2 implements FlashLightInterface {
	protected final Context context;
	
	protected CameraCaptureSession mSession;
	protected CaptureRequest.Builder mBuilder;
	protected CameraDevice mCameraDevice;
	protected CameraManager mCameraManager;
	protected SurfaceTexture mSurfaceTexture = new SurfaceTexture(1);
	
	protected boolean flashOn = false;
	protected boolean flashAvailable = false;
	
	public FlashLight2( Context context ) {
		this.context = context;
	}
	
	public boolean init() {
		try {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            //here to judge if flash is available
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics("0");
            flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (flashAvailable) {
                mCameraManager.openCamera("0", new MyCameraDeviceStateCallback(), null);
            }
            //mCameraManager.openCamera("0", new MyCameraDeviceStateCallback(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return flashAvailable;
	}
	
	public void setFlashLight( boolean onOff ) {
		if( !flashAvailable ) return;
		flashOn = onOff;
		
		try {
            mBuilder.set(CaptureRequest.FLASH_MODE, flashOn ? CameraMetadata.FLASH_MODE_TORCH : CameraMetadata.FLASH_MODE_OFF );
            mSession.setRepeatingRequest(mBuilder.build(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void release() {
		if( mSession != null) {
			mSession.close();
			mSession = null;
		}
		
		if ( mCameraDevice != null ) {
			mCameraDevice.close();
	        mCameraDevice = null;
		}
		
		if( mSurfaceTexture != null ) {
			mSurfaceTexture.release();
			mSurfaceTexture = null;
		}
	}
	
    class MyCameraDeviceStateCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            //get builder
            try {
                mBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                //flash on, default is on
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
                mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                List<Surface> list = new ArrayList<Surface>();
                
                Size size = getSmallestSize(mCameraDevice.getId());
                mSurfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
                Surface mSurface = new Surface(mSurfaceTexture);
                list.add(mSurface);
                mBuilder.addTarget(mSurface);
                camera.createCaptureSession(list, new MyCameraCaptureSessionStateCallback(), null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    }

    private Size getSmallestSize(String cameraId) throws CameraAccessException {
        Size[] outputSizes = mCameraManager.getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                .getOutputSizes(SurfaceTexture.class);
        if (outputSizes == null || outputSizes.length == 0) {
            throw new IllegalStateException(
                    "Camera " + cameraId + "doesn't support any outputSize.");
        }
        Size chosen = outputSizes[0];
        for (Size s : outputSizes) {
            if (chosen.getWidth() >= s.getWidth() && chosen.getHeight() >= s.getHeight()) {
                chosen = s;
            }
        }
        return chosen;
    }

    /**
     * session callback
     */
    class MyCameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            mSession = session;
            
            try {
            	mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF );
                mSession.setRepeatingRequest(mBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    }

	@Override
	public void toggleFlashLight() {
		setFlashLight( !flashOn );	
	}
}

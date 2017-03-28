/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.purehero.lotto.scaner.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The barcode reader activity itself. This is loosely based on the CameraPreview
 * example included in the Android SDK.
 *
 * 
 * 
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	protected static final String TAG = CaptureActivity.class.getSimpleName();

	protected static final long INTENT_RESULT_DURATION = 1500L;

	/*
	protected static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;
	static {
	    DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>();
	    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
	    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
	    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
	    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
	}
	*/

	protected enum Source {
		NATIVE_APP_INTENT,
		NONE
	}

	protected CaptureActivityHandler handler;
	protected ViewfinderView viewfinderView;
	protected TextView statusView;
	protected View resultView;
	protected Result lastResult;
	protected ResultHandler resultHandler ;
	protected boolean hasSurface;
	protected boolean copyToClipboard;
	protected Source source;
	protected Vector<BarcodeFormat> decodeFormats = null;
	protected String characterSet;
	protected InactivityTimer inactivityTimer;
	protected Handler byPassHandler = null;
	
	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}
	
	public ResultHandler getResultHandler() {
		return resultHandler;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.setContentView( R.layout.capture_activity );
		
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
	    resultView = findViewById(R.id.result_view);
	    statusView = (TextView) findViewById(R.id.status_view);
		
		Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	    CameraManager.init(getApplication());
		
		handler = null;
	    lastResult = null;
	    hasSurface = false;
		
	    inactivityTimer = new InactivityTimer(this);
	}

  @Override
  protected void onResume() {
    super.onResume();
    resetStatusView();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder, viewfinderView);
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this);
      //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    source = Source.NATIVE_APP_INTENT;
    //decodeFormats = DecodeFormatManager.parseDecodeFormats(new Intent());
    //decodeFormats = DecodeFormatManager.allDecodeFormats();
    characterSet = "UTF-8";//intent.getStringExtra(Intents.Scan.CHARACTER_SET);
    /*
    Intent intent = getIntent();
    String action = intent == null ? null : intent.getAction();
    if (intent != null && action != null) {
      if (action.equals(Intents.Scan.ACTION)) {
        // Scan the formats the intent requested, and return the result to the calling activity.
        source = Source.NATIVE_APP_INTENT;
        decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
      } else {
        // Scan all formats and handle the results ourselves (launched from Home).
        source = Source.NONE;
        decodeFormats = null;
      }
      characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
    } else {
      source = Source.NONE;
      decodeFormats = null;
      characterSet = null;
    }
    */

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true);

    inactivityTimer.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    inactivityTimer.onPause();
    CameraManager.get().closeDriver();
  }

  @Override
  protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  /*
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (source == Source.NATIVE_APP_INTENT) {
        setResult(RESULT_CANCELED);
        finish();
        return true;
      } else if ((source == Source.NONE) && lastResult != null) {
        resetStatusView();
        if (handler != null) {
          handler.sendEmptyMessage(R.id.restart_preview);
        }
        return true;
      }
    } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
      // Handle these events so they don't launch the Camera app
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
  */

  public void surfaceCreated(SurfaceHolder holder) {
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder, viewfinderView);
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		Log.d( TAG, "handleDecode:" + rawResult.toString());
	  
		inactivityTimer.onActivity();
		lastResult = rawResult;
		resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
		
		if (barcode == null) {
			// This is from history -- no saved barcode
			handleDecodeInternally(rawResult, resultHandler, null);
			
		} else {
			//drawResultPoints(barcode, rawResult);
			handleDecodeExternally(rawResult, resultHandler, barcode);
			/*
			switch (source) {
			case NATIVE_APP_INTENT:
				handleDecodeExternally(rawResult, barcode);
				break;
				
			case NONE:
				break;
			}
			*/
		}
	}

  /**
   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
   *
   * @param barcode   A bitmap of the captured image.
   * @param rawResult The decoded results which contains the points to draw.
 * @param resultHandler 
   */
  /*private void drawResultPoints(Bitmap barcode, Result rawResult) {
    ResultPoint[] points = rawResult.getResultPoints();
    if (points != null && points.length > 0) {
      Canvas canvas = new Canvas(barcode);
      Paint paint = new Paint();
      paint.setColor(getResources().getColor(R.color.result_image_border));
      paint.setStrokeWidth(3.0f);
      paint.setStyle(Paint.Style.STROKE);
      Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
      canvas.drawRect(border, paint);

      paint.setColor(getResources().getColor(R.color.result_points));
      if (points.length == 2) {
        paint.setStrokeWidth(4.0f);
        drawLine(canvas, paint, points[0], points[1]);
      } else if (points.length == 4 &&
                 (rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A)) ||
                 (rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
        // Hacky special case -- draw two lines, for the barcode and metadata
        drawLine(canvas, paint, points[0], points[1]);
        drawLine(canvas, paint, points[2], points[3]);
      } else {
        paint.setStrokeWidth(10.0f);
        for (ResultPoint point : points) {
          canvas.drawPoint(point.getX(), point.getY(), paint);
        }
      }
    }
  }

  private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
    canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
  }*/

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
		statusView.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.GONE);
		resultView.setVisibility(View.VISIBLE);

		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		
		if (barcode == null) {
			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
			
		} else {
			barcodeImageView.setImageBitmap(barcode);
		}
	}

  	// Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
	private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
	    //viewfinderView.drawResultBitmap(barcode);
	
	    // Since this message will only be shown for a second, just tell the user what kind of
	    // barcode was found (e.g. contact info) rather than the full contents, which they won't
	    // have time to read.
	    statusView.setText( R.string.completed_decoding );
	    CameraManager.get().stopPreview();
	    
	    /*
	    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
	    if (copyToClipboard) {
	      ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	      clipboard.setText(resultHandler.getDisplayContents());
	    }
	    */
	
	    if (source == Source.NATIVE_APP_INTENT) {
	    	// Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
	    	// the deprecated intent is retired.
	    	Intent intent = new Intent(getIntent().getAction());
	    	//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    	intent.putExtra("TITLE", CaptureActivity.this.getString( resultHandler.getDisplayTitle()));
	    	intent.putExtra("CONTENTS", resultHandler.getDisplayContents());
	    	intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
	    	intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
	      
	    	byte[] rawBytes = rawResult.getRawBytes();
	    	if (rawBytes != null && rawBytes.length > 0) {
	    		intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
	    	}
	      
	    	Message message = Message.obtain(handler, R.id.return_scan_result);
	    	message.obj = intent;
	    	handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
	    }
	}

  private void initCamera(SurfaceHolder surfaceHolder, ViewfinderView viewfinderView ) {
	  try {
		  CameraManager.get().openDriver(surfaceHolder, viewfinderView );
		  
	  } catch (IOException ioe) {
		  Log.w(TAG, ioe);
		  displayFrameworkBugMessageAndExit();
		  return;
    
	  } catch (RuntimeException e) {
		  // Barcode Scanner has seen crashes in the wild of this variety:
		  // java.?lang.?RuntimeException: Fail to connect to camera service
		  Log.w(TAG, "Unexpected error initializating camera", e);
		  displayFrameworkBugMessageAndExit();
		  
		  return;
	  }
	  
	  if (handler == null) {
		  handler = new CaptureActivityHandler(this, decodeFormats, characterSet, null );
	  }
  }

  	private void displayFrameworkBugMessageAndExit() {
  		AlertDialog.Builder builder = new AlertDialog.Builder(this);
  		builder.setTitle(getString(R.string.app_name));
  		builder.setMessage( R.string.camera_error );
  		builder.setPositiveButton( R.string.confirm, new FinishListener(this));
  		builder.setOnCancelListener(new FinishListener(this));
  		builder.show();
  	}

  	public void restartPreviewAfterDelay(long delayMS) {
  	    if (handler != null) {
  	    	handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
  	    }
  	    
  	    resetStatusView();
	}
  	
	protected void resetStatusView() {
  		resultView.setVisibility(View.GONE);
  		statusView.setText( R.string.scan_info );
  		statusView.setVisibility(View.VISIBLE);
  		viewfinderView.setVisibility(View.VISIBLE);
  		lastResult = null;
 
  		drawViewfinder();
  		if( handler != null ) {
  			handler.sendEmptyMessage(R.id.restart_preview);
  		}
  		CameraManager.get().startPreview();
  	}

  	public void drawViewfinder() {
  		viewfinderView.drawViewfinder();
  	}
}

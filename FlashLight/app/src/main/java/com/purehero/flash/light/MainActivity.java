package com.purehero.flash.light;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private FlashLightInterface flashLight = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "206559557", true);  // Flash Light
        StartAppAd.disableSplash();

        setContentView(R.layout.activity_main);

        SeekBar speedBar = ( SeekBar ) findViewById( R.id.speedBar );
        if( speedBar != null ) {
            speedBar.setMax(1000);
            speedBar.setOnSeekBarChangeListener( this );
        }

        int btnIDs [] = new int[]{ R.id.btnFlash };
        for( int id : btnIDs ) {
            Button btn = ( Button ) findViewById( id );
            if( btn != null ) {
                btn.setOnClickListener( this );
            }
        }

        if( Build.VERSION.SDK_INT >= 21 ) {
            flashLight = new FlashLight2( this );
        } else {
            flashLight = new FlashLight( this );
        }
        if( !flashLight.init()) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton( AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        }

        new Thread( flashing_thread ).start();
    }

    @Override
    protected void onDestroy() {
        flashLight.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
		/*
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean flash = false;

    @Override
    public void onClick(View arg0) {
        switch( arg0.getId()) {
            case R.id.btnFlash :
                flash = !flash;

                Button btn = ( Button ) arg0;
                btn.setBackgroundResource( flash ? R.drawable.light_on : R.drawable.light_off );

                flashLight.setFlashLight( flash );
                break;
        }
    }

    // Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
    private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
    private long backPressedTime = 0;

    @Override
    public void onBackPressed()
    {
        if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
            StartAppAd.onBackPressed( this );
            super.onBackPressed();

        } else {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
        }
    }

    int flashingSpeedValue = 0;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser ) {
        if( seekBar.getId() != R.id.speedBar ) return;
        flashingSpeedValue = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        flashingSpeedValue = seekBar.getProgress();

        if( flashingSpeedValue == 0 ) {
            flashLight.setFlashLight( flash );
        }
    }


    Runnable flashing_thread = new Runnable() {
        @Override
        public void run() {
            while( true ) {
                try {
                    Thread.sleep( 100 + flashingSpeedValue );
                } catch (InterruptedException e) {}

                if( flashingSpeedValue > 0 && flash ) {
                    flashLight.toggleFlashLight();

					/*
					MainActivity.this.runOnUiThread( new Runnable(){
						@Override
						public void run() {
							flashLight.toggleFlashLight();
						}});
						*/
                }
            }
        }
    };
}

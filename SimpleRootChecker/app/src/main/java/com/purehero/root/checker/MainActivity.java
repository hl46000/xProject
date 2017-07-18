package com.purehero.root.checker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.purehero.module.fragment.FragmentEx;
import com.purehero.module.tabhost.TabAppCompatActivity;
import com.purehero.module.tabhost.ViewPagerAdapter;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class MainActivity extends TabAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "206566570", true);
        //StartAppAd.disableSplash();

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void addTabItem(ViewPagerAdapter viewPagerAdapter) {
        viewPagerAdapter.addItem( new DeviceInfoFragment().setMainActivity(this), R.string.info );
        viewPagerAdapter.addItem( new CheckRootFragment().setMainActivity(this), R.string.check_root );
    }

    // Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
    private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        FragmentEx fragment = ( FragmentEx ) pagerAdapter.getItem( pager.getCurrentItem());
        if( fragment.onBackPressed()) {
            return;
        }

        if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
            StartAppAd.onBackPressed( this );
            super.onBackPressed();

        } else {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText( this, com.purehero.module.common.R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
        }
    }
}

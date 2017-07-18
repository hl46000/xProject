package com.purehero.apk.manager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.purehero.module.fragment.FragmentEx;
import com.purehero.module.fragment.FragmentText;
import com.purehero.module.fragment.filelist.FileListFragment;
import com.purehero.module.tabhost.TabAppCompatActivity;
import com.purehero.module.tabhost.ViewPagerAdapter;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TabAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "206492520", true);
        StartAppAd.disableSplash();

        setContentView(R.layout.activity_main);

        checkPermission();
    }

    @Override
    protected void addTabItem(ViewPagerAdapter viewPagerAdapter) {
        viewPagerAdapter.addItem( new ApkListFragment().setMainActivity(this), R.string.application );
        viewPagerAdapter.addItem( new FileListFragment().setMainActivity(this), R.string.files );
        viewPagerAdapter.addItem( new FragmentText(), R.string.history );
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
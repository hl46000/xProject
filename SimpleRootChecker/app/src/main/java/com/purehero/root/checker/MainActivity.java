package com.purehero.root.checker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.purehero.module.tabhost.FragmentText;
import com.purehero.module.tabhost.TabAppCompatActivity;
import com.purehero.module.tabhost.ViewPagerAdapter;

public class MainActivity extends TabAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void addTabItem(ViewPagerAdapter viewPagerAdapter) {
        viewPagerAdapter.addItem( new DeviceInfoFragment().setMainActivity(this), R.string.info );
        viewPagerAdapter.addItem( new CheckRootFragment().setMainActivity(this), R.string.check_root );
    }
}

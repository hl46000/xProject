package com.purehero.module;

import android.os.Bundle;
import android.util.Log;

import com.purehero.module.fragment.FragmentText;
import com.purehero.module.fragment.filelist.FileListFragment;
import com.purehero.module.tabhost.TabAppCompatActivity;
import com.purehero.module.tabhost.ViewPagerAdapter;

public class MainActivity extends TabAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( "MyLOG", "MainActivity::onCreate" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
    }

    @Override
    protected void addTabItem(ViewPagerAdapter pagerAdapter) {
        pagerAdapter.addItem( new FileListFragment().setMainActivity(this), "Tab1" );
        pagerAdapter.addItem( new FragmentText(), "Tab2" );
    }
}

package com.purehero.apk.manager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.purehero.module.fragment.FragmentText;
import com.purehero.module.fragment.filelist.FileListFragment;
import com.purehero.module.tabhost.TabAppCompatActivity;
import com.purehero.module.tabhost.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TabAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
    }

    @Override
    protected void addTabItem(ViewPagerAdapter viewPagerAdapter) {
        viewPagerAdapter.addItem( new ApkListFragment().setMainActivity(this), R.string.application );
        viewPagerAdapter.addItem( new FileListFragment().setMainActivity(this), R.string.files );
        viewPagerAdapter.addItem( new FragmentText(), R.string.history );
    }
}
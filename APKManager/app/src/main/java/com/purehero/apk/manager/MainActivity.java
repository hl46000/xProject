package com.purehero.apk.manager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.purehero.module.tabhost.FragmentText;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123 :
                checkPermission();
                break;
        }
    }

    private int checkPermission() {
        String permissions[] = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        List<String> request_permissions = new ArrayList<String>();
        for( String permission : permissions ) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                request_permissions.add( permission );
            }
        }

        if( request_permissions.size() > 0 ) {
            String permissionsList [] = new String[request_permissions.size()];
            request_permissions.toArray(permissionsList);
            ActivityCompat.requestPermissions(this, permissionsList, 123 );
        }

        return request_permissions.size();
    }
}
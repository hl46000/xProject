package share.file.purehero.com.fileshare;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.purehero.common.FragmentEx;
import com.purehero.common.FragmentText;
import com.purehero.common.G;
import com.purehero.common.ViewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends AppCompatActivity implements MaterialTabListener, View.OnClickListener {
    public static int ACTION_BAR_LIST_MODE = 0;
    public static int ACTION_BAR_SELECTE_MODE = 1;

    private AdView bannerAdView = null;
    private MaterialTabHost tabHost;
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    private SearchView searchView;
    private List<Toolbar> toolbarList = new ArrayList<Toolbar>();
    private int toolbarIndex = ACTION_BAR_LIST_MODE;
    private boolean selectionALL = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileClickCount.loadDatas( this );

        int toolbarTitleIDs[] = { R.string.toolbar, R.string.toolbar2 };
        int toolbarIDs [] = { R.id.my_toolbar, R.id.my_toolbar2 };
        for( int i = 0; i < toolbarIDs.length; i++  ) {
            int id = toolbarIDs[i];

            Toolbar toolbar = (Toolbar) findViewById( id );
            if( toolbar != null ) {
                toolbar.setTitle( toolbarTitleIDs[i] );
                toolbarList.add( toolbar );
            }
        }
        setSupportActionBar(toolbarList.get(toolbarIndex));

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabHost );
        pagerAdapter.addItem( new FileListFragment().setMainActivity(this), R.string.my_file );

        String state= Environment.getExternalStorageState(); //외부저장소(SDcard)의 상태 얻어오기
        if( state.equals(Environment.MEDIA_MOUNTED)){ // SDcard 의 상태가 쓰기 가능한 상태로 마운트되었는지 확인
            File externalStorageFolder = Environment.getExternalStorageDirectory();
            //G.Log( "externalStorageFolder : %s", externalStorageFolder.getAbsolutePath());
            pagerAdapter.addItem( new FileListFragment().setRootFolder(externalStorageFolder).setMainActivity(this), R.string.my_sdcard_file );
        }

        /*
        pagerAdapter.addItem( new FragmentText(), R.string.remote_file );
        */

        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);

                Fragment fragment = pagerAdapter.getItem( position );
                if( fragment instanceof FileListFragment ) {
                    FileListFragment fileListFragment = ( FileListFragment ) fragment;
                    fileListFragment.reflashListView();
                }

                // ActionBar에 검색바가 활성되어 있으면 검색바를 사라지게 한다.
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(pagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        bannerAdView = (AdView) findViewById(R.id.adView);
        if( bannerAdView != null ) {
            bannerAdView.setVisibility( View.GONE );

            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAdView.setAdListener( new AdListener(){
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    bannerAdView.setVisibility( View.VISIBLE );
                }});

            bannerAdView.loadAd(adRequest);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        FileClickCount.saveDatas( this );
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        tabHost.notifyDataSetChanged();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123 :
                /*
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    checkPermission();
                }
                */
                break;
        }
    }

    private void checkPermission() {
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
            ActivityCompat.requestPermissions(this, (String[])request_permissions.toArray(), 123 );
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
        //G.Log( "onTabSelected : %d", tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = pagerAdapter.getItem( pager.getCurrentItem());
        if( fragment instanceof OptionsItemSelectListener ) {
            OptionsItemSelectListener listener = ( OptionsItemSelectListener ) fragment;
            if( listener.onOptionsItemSelected( id )) {
                return true;
            }
        }

        switch (id) {
            case android.R.id.home          : break;
            case R.id.action_select_mode    : break;
            case R.id.action_create_folder  : break;
            case R.id.action_settings       : break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void changeToolbarMode() {
        getSupportActionBar().hide();
        setSupportActionBar(toolbarList.get(toolbarIndex));
        getSupportActionBar().show();
    }

    public void changeFileListModeToolbar() {
        toolbarIndex = ACTION_BAR_LIST_MODE;
        changeToolbarMode();
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        selectionALL = false;
    }

    public void changeFileSelectModeToolbar() {
        toolbarIndex = ACTION_BAR_SELECTE_MODE;
        changeToolbarMode();
        ActionBar actionbar = getSupportActionBar();
        if( actionbar != null ) {
            actionbar.setIcon( selectionALL ? R.drawable.ck_checked : R.drawable.ck_nomal );
            actionbar.setHomeButtonEnabled(true);

            View view = toolbarList.get(1).getChildAt(1);
            view.setOnClickListener( this );
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //G.Log( "onPrepareOptionsMenu" );
        try {
            Fragment fragment = pagerAdapter.getItem( pager.getCurrentItem());
            fragment.onPrepareOptionsMenu(menu);

            MenuItem menu_item = null;
            if( isSelectMode()) {
                menu_item = menu.findItem( R.id.action_copy );                      // 복사 메뉴
                if( menu_item != null ) {
                    menu_item.setEnabled( g_SelectedItems != null );                 // 선택된 값이 없을 경우 보이게 한다.
                }
                menu_item = menu.findItem( R.id.action_move );                      // 이동 메뉴
                if( menu_item != null ) {
                    menu_item.setEnabled( g_SelectedItems != null );                 // 선택된 값이 없을 경우 보이게 한다.
                }
                menu_item = menu.findItem( R.id.action_rename );                    // 이름 변경
                if( menu_item != null ) {
                    menu_item.setVisible( g_SelectedItems != null && g_SelectedItems.size() == 1 ); // 선택된 항목이 있고, 개수가 1개 일때만 보이게 한다.
                }

            } else {
                menu_item = menu.findItem( R.id.action_paste );                     // 붙여 넣기 메뉴
                if( menu_item != null ) {
                    menu_item.setVisible( g_SelectedItems != null );                 // 선택된 값이 있을 경우 보이게 한다.
                }
            }

        } catch( Exception e ) {}

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (toolbarIndex) {
            case 0: return baseMenu(menu);
            case 1: return selectMenu(menu);
        }
        return false;
    }

    private boolean selectMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.menu_select_mode, menu);
        return true;
    }

    private boolean baseMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    Fragment fragment = pagerAdapter.getItem( pager.getCurrentItem());
                    if( fragment instanceof SearchTextChangeListener ) {
                        SearchTextChangeListener listener = (SearchTextChangeListener) fragment;
                        listener.onQueryTextSubmit(s);
                    }


                    searchItem.collapseActionView();
                } catch( Exception e ) {}

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    Fragment fragment = pagerAdapter.getItem( pager.getCurrentItem());
                    if( fragment instanceof SearchTextChangeListener ) {
                        SearchTextChangeListener listener = (SearchTextChangeListener) fragment;
                        listener.onQueryTextChange(s);
                    }
                } catch( Exception e ) {}

                return true;
            }
        });
        
        return true;
    }

    // Back 버튼을 두번 연속으로 눌렸을때 앱을 종료하기 위해 필요한 변수 및 값
    private final int BACK_PRESSED_TIME_INTERVAL = 2000;	// 2sec
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            changeFileListModeToolbar();
        } else {
            FragmentEx fragment = (FragmentEx) pagerAdapter.getItem( pager.getCurrentItem());
            if( !fragment.onBackPressed()) {
                if( backPressedTime + BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();

                } else {
                    backPressedTime = System.currentTimeMillis();
                    Toast.makeText( this, R.string.two_back_touch_exit_app, Toast.LENGTH_SHORT ).show();;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        selectionALL = !selectionALL;

        Fragment fragment = pagerAdapter.getItem( pager.getCurrentItem());
        if( fragment instanceof OptionsItemSelectListener ) {
            OptionsItemSelectListener listener = ( OptionsItemSelectListener ) fragment;
            listener.onOptionsItemSelected( selectionALL ? R.drawable.ck_checked : R.drawable.ck_nomal );
        }
        getSupportActionBar().setIcon( selectionALL ? R.drawable.ck_checked : R.drawable.ck_nomal );

       //G.Log( "selectionALL : %s", selectionALL?"true":"false" );
    }

    public void setSelectToolbarSelectedCount(int selectedCount) {
        String strSelected = getString( R.string.toolbar2 );
        selectedCount = 0;

        collectSelectedItems();
        if( g_SelectedItems != null ) {
            selectedCount = g_SelectedItems.size();
        }

        getSupportActionBar().setTitle( String.format("%d %s", selectedCount, strSelected ));
    }

    protected List<FileListData> g_SelectedItems = null;
    public void collectSelectedItems() {
        g_SelectedItems = null;

        int len = pagerAdapter.getCount();
        for( int i = 0; i < len; i++ ) {
            Fragment fragment = pagerAdapter.getItem( i );
            if( fragment instanceof FileListFragment ) {
                FileListFragment fileListFragment = ( FileListFragment ) fragment;
                List<FileListData> selectedItems = fileListFragment.getSelectedItems();

                if( selectedItems != null && selectedItems.size() > 0 ) {
                    if( g_SelectedItems == null ) {
                        g_SelectedItems = new ArrayList<FileListData>();
                    }
                    g_SelectedItems.addAll( selectedItems );
                }
            }
        }
    }

    /**
     * UI의 모든 선택 항목을 선택하제 한다. bClearSelectedItems 값이 참이면
     * 선택항목의 기억을 삭제한다.
     *
     * @param bClearSelectedItems
     */
    public void clearSelectedItems( boolean bClearSelectedItems ) {
        if( bClearSelectedItems ) {
            g_SelectedItems = null;
        }

        int len = pagerAdapter.getCount();
        for( int i = 0; i < len; i++ ) {
            Fragment fragment = pagerAdapter.getItem( i );
            if( fragment instanceof FileListFragment ) {
                FileListFragment fileListFragment = ( FileListFragment ) fragment;
                fileListFragment.setSelectALL(false);
            }
        }
    }

    public List<FileListData> getSelectedItems() {
        return g_SelectedItems;
    }

    public int getOpCode() {
        return opCode;
    }

    protected int opCode = -1;
    public void setOpCode( int code ) {
        opCode = code;
    }

    public boolean isSelectMode() {
        return toolbarIndex == ACTION_BAR_SELECTE_MODE;
    }

    public void setAllSelectItems(boolean b) {
        int len = pagerAdapter.getCount();
        for( int i = 0; i < len; i++ ) {
            Fragment fragment = pagerAdapter.getItem( i );
            if( fragment instanceof FileListFragment ) {
                FileListFragment fileListFragment = ( FileListFragment ) fragment;
                fileListFragment.setSelectALL( b );
            }
        }
    }
}

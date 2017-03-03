package share.file.purehero.com.fileshare;


import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.purehero.common.FragmentEx;
import com.purehero.common.FragmentText;
import com.purehero.common.G;
import com.purehero.common.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class MainActivity extends AppCompatActivity implements MaterialTabListener {
    private MaterialTabHost tabHost;
    private ViewPager pager;
    private ViewPagerAdapter pagerAdapter;
    private SearchView searchView;
    private List<ToolbarEx> toolbarList = new ArrayList<ToolbarEx>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int toolbarTitleIDs[] = { R.string.toolbar, R.string.toolbar2 };
        int toolbarIDs [] = { R.id.my_toolbar, R.id.my_toolbar2 };
        for( int i = 0; i < toolbarIDs.length; i++  ) {
            int id = toolbarIDs[i];

            ToolbarEx toolbar = (ToolbarEx) findViewById( id );
            if( toolbar != null ) {
                toolbar.setTitle( toolbarTitleIDs[i] );
                toolbar.setId(id);
                toolbarList.add( toolbar );
            }
        }
        setSupportActionBar(toolbarList.get(0));

        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        // init view pager
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabHost );

        pagerAdapter.addItem( new FileListFragment().setMainActivity(this), "FIRST" );
        pagerAdapter.addItem( new FileListFragment().setMainActivity(this), "SECOND" );
        pagerAdapter.addItem( new FragmentText(), "ETC" );

        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
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
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Fragment fragment = pagerAdapter.getItem( pager.getCurrentItem());
                if( fragment instanceof OptionsItemSelectListener ) {
                    OptionsItemSelectListener listener = ( OptionsItemSelectListener ) fragment;
                    return listener.onOptionsItemSelected( item.getItemId() );
                }
                break;
            case R.id.action_select_mode :

                break;
            case R.id.action_create_folder :

                break;
            case R.id.action_settings :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getActionBar( int index ) {
        return toolbarList.get(index);
    }
    public void changeActionBar( int index ) {
        getSupportActionBar().hide();
        setSupportActionBar(toolbarList.get(index));
        getSupportActionBar().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        G.Log( "onCreateOptionsMenu : %s", getSupportActionBar().getTitle());
        ToolbarEx actionBar = (ToolbarEx) getSupportActionBar();

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
}

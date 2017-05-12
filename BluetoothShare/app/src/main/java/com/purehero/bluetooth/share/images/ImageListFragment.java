package com.purehero.bluetooth.share.images;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.purehero.bluetooth.share.G;
import com.purehero.bluetooth.share.MainActivity;
import com.purehero.bluetooth.share.R;
import com.purehero.module.fragment.FragmentEx;

/**
 * Created by MY on 2017-05-12.
 */

public class ImageListFragment extends FragmentEx {
    private MainActivity context;
    private ImageListAdapter listAdapter = null;

    final int VIEW_MODE_LIST = 0;
    final int VIEW_MODE_GRID = 1;

    private View layout = null;
    private ListView listView = null;
    private GridView gridView = null;
    private ProgressBar progressBar = null;
    int view_layout_mode = VIEW_MODE_LIST;

    public ImageListFragment setMainActivity(MainActivity mainActivity) {
        context = mainActivity;
        listAdapter = new ImageListAdapter(context);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        G.Log("onCreateView");

        // Fragment 가 option menu을 가지고 있음을 알림
        setHasOptionsMenu(true);

        // ActionBar Title 변경
        AppCompatActivity ACActivity = (AppCompatActivity) getActivity();
        ActionBar aBar = ACActivity.getSupportActionBar();
        if (aBar != null) {
            aBar.setTitle(R.string.photos);
        }
        context.showActionBarBackButton(true);

        layout = inflater.inflate(R.layout.myfile_layout, container, false);
        if (layout == null) return null;

        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        listView = (ListView) layout.findViewById(R.id.listView);
        gridView = (GridView) layout.findViewById(R.id.gridView);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);

        HorizontalScrollView pathScrollView = (HorizontalScrollView) layout.findViewById( R.id.pathScrollView ) ;
        if( pathScrollView != null ) {
            pathScrollView.setVisibility( View.GONE );
        }

        new Thread(list_data_load_runnable).start();
        return layout;
    }

    /*
     * 데이터를 다시 로딩하는 Thread
    */
    Runnable list_data_load_runnable = new Runnable() {
        @Override
        public void run() {
            listAdapter.reload();

            context.runOnUiThread( init_ui_runnable );
            listView.smoothScrollToPosition(0);
        }
    };

    /*
     * UI 관련된 항목을 초기화 시키는 Runnable
     */
    Runnable init_ui_runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
            registerForContextMenu(progressBar);

            if (view_layout_mode == VIEW_MODE_LIST) {
                // ListView 나타나게 하기
                gridView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(listAdapter);

                registerForContextMenu(listView);
                listView.setOnItemLongClickListener( itemLongClickListener );
                listView.setOnItemClickListener( itemClickListener );

            } else {
                listView.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                gridView.setAdapter(listAdapter);

                registerForContextMenu(gridView);
                gridView.setOnItemLongClickListener( itemLongClickListener );
                gridView.setOnItemClickListener( itemClickListener );
            }



            EditText item_filter = (EditText) layout.findViewById( R.id.txt_search );
            if( item_filter != null ) {
                item_filter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        listAdapter.getFilter().filter(cs);
                    }
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }
                    @Override
                    public void afterTextChanged(Editable arg0) { }
                });
            }

            listAdapter.sort();
            progressBar.setVisibility( View.GONE );
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            return false;
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.apps_action_view_mode) {
            if (view_layout_mode == VIEW_MODE_LIST) {
                item.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
                view_layout_mode = VIEW_MODE_GRID;
            } else {
                item.setIcon(R.drawable.ic_view_module_white_24dp);
                view_layout_mode = VIEW_MODE_LIST;
            }
            // 수집된 데이터 화면에 보여 주기
            init_ui_runnable.run();
            return true;
        }

        return false;
    }
}

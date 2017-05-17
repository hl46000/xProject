package com.purehero.bluetooth.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.purehero.module.common.FileIntentUtils;
import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.fragment.FragmentEx;
import com.purehero.module.fragment.common.FunctionDeleteSelectedItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by purehero on 2017-05-16.
 */

public class BaseListFragment extends FragmentEx implements View.OnClickListener{
    private MainActivity context;
    private BaseListAdapter listAdapter = null;

    final int VIEW_MODE_LIST = 0;
    final int VIEW_MODE_GRID = 1;

    private View layout = null;
    private ListView listView = null;
    private GridView gridView = null;
    private ProgressBar progressBar = null;
    private int view_layout_mode = VIEW_MODE_LIST;
    private int title_res_id = -1;

    public BaseListFragment setMainActivity(MainActivity mainActivity, int title_res_id ) {
        context = mainActivity;
        this.title_res_id = title_res_id;

        return this;
    }

    public BaseListFragment setBaseListAdapter( BaseListAdapter adapter ) {
        listAdapter = adapter;
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
        if (aBar != null && title_res_id != -1 ) {
            aBar.setTitle( title_res_id );
        }
        // ActionBar 에 뒤로 가기 버튼 아이콘 표시
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
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if( listAdapter.isSelectMode()) return true;
            return false;
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BaseListData data = ( BaseListData ) listAdapter.getItem( position );

            if( listAdapter.isSelectMode() ) {
                data.setSelected(!data.isSelected());
                listAdapter.notifyDataSetChanged();
                context.invalidateOptionsMenu();
                return;
            }

            startActivity( FileIntentUtils.Running( data.getFile()));
        }
    };

    @Override
    public boolean onBackPressed() {
        if( listAdapter.isSelectMode()) {
            listAdapter.setSelectMode( false );
            context.invalidateOptionsMenu();
            return true;
        }

        return super.onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.images_option_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        int selectedCount = listAdapter.getSelectedItemCount();

        MenuItem item = menu.findItem(R.id.images_action_select_mode);
        if (item != null) {
            item.setVisible(!listAdapter.isSelectMode());
        }

        item = menu.findItem( R.id.images_action_view_mode );
        if( item != null ) {
            if (view_layout_mode == VIEW_MODE_GRID) {
                item.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
            } else {
                item.setIcon(R.drawable.ic_view_module_white_24dp);
            }
        }

        item = menu.findItem( R.id.images_action_delete );
        if( item != null ) {
            item.setVisible( listAdapter.isSelectMode() && selectedCount > 0 );
        }

        item = menu.findItem( R.id.images_action_share );
        if( item != null ) {
            item.setVisible( listAdapter.isSelectMode() && selectedCount > 0 );
        }

        item = menu.findItem( R.id.images_action_bluetooth_share );
        if( item != null ) {
            item.setVisible( listAdapter.isSelectMode() && selectedCount > 0 );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.images_action_view_mode) {
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

        if( id == R.id.images_action_select_mode ) {
            listAdapter.setSelectMode( true );
            context.invalidateOptionsMenu();
            return true;
        }

        if( id == R.id.images_action_delete ) {
            delete_files( listAdapter.getSelectedItems() );
            return true;
        }

        if( id == R.id.images_action_share ) {
            share_files( listAdapter.getSelectedItems());
            return true;
        }

        if( id == R.id.images_action_bluetooth_share ) {
            bluetooth_share_files( listAdapter.getSelectedItems());
            return true;
        }

        return false;
    }

    // 메뉴 생성
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        context.getMenuInflater().inflate(R.menu.images_context_menu, menu);
    }

    // 메뉴 클릭
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        G.Log( "onContextItemSelected" );

        boolean ret = false;	// 메뉴의 처리 여부

        // 클릭된 APK 정보
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        G.Log( "onContextItemSelected index : " + info.position );
        BaseListData data = ( BaseListData ) listAdapter.getItem( info.position );

        List<BaseListData> datas = new ArrayList<BaseListData>();
        datas.add( data );

        int id = item.getItemId();
        if( id == R.id.images_menu_delete ) {
            delete_files( datas );
            ret = true;

        } else if( id == R.id.images_menu_bluetooth_share ) {
            bluetooth_share_files( datas );
            ret = true;

        } else if( id == R.id.images_menu_share ) {
            share_files( datas );
            ret = true;
        }

        return ret;
    }

    protected  void delete_files( List<BaseListData> selectedItems) {
        List<File> delete_files = new ArrayList<File>();
        for( BaseListData fData : selectedItems ) {
            delete_files.add( fData.getFile());
        }

        new FunctionDeleteSelectedItem( context, delete_files, new OnSuccessListener(){
            @Override
            public void OnSuccess() {
                listAdapter.setSelectMode(false);

                context.runOnUiThread( new Runnable(){
                    @Override
                    public void run() {
                        listAdapter.reload();                                               // 리스트를 갱신 시킨다.
                        context.invalidateOptionsMenu();
                    }
                });
            }
        } ).run();
    }

    protected void bluetooth_share_files(List<BaseListData> selectedItems) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setPackage("com.android.bluetooth");
        shareIntent.setType("*/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        try {
            progressBar.setVisibility( View.VISIBLE );

            ArrayList<Uri> shareDatas = new ArrayList<Uri>();
            for( BaseListData data : selectedItems ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    shareDatas.add(FileProvider.getUriForFile(context, "com.purehero.bluetooth.share.provider", data.getFile()));
                } else {
                    shareDatas.add( Uri.fromFile( data.getFile() ));
                }
            }
            progressBar.setVisibility( View.GONE );

            shareIntent.putParcelableArrayListExtra( Intent.EXTRA_STREAM, shareDatas );
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity( shareIntent );
    }

    protected void share_files(List<BaseListData> selectedItems) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("*/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        try {
            progressBar.setVisibility( View.VISIBLE );

            ArrayList<Uri> shareDatas = new ArrayList<Uri>();
            for( BaseListData data : selectedItems ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    shareDatas.add(FileProvider.getUriForFile(context, "com.purehero.bluetooth.share.provider", data.getFile()));
                } else {
                    shareDatas.add( Uri.fromFile( data.getFile() ));
                }
            }
            progressBar.setVisibility( View.GONE );

            shareIntent.putParcelableArrayListExtra( Intent.EXTRA_STREAM, shareDatas );
        } catch (Exception e) {
            e.printStackTrace();
        }

        startActivity(Intent.createChooser(shareIntent, String.format( "Share %s Files", context.getString( title_res_id)) ));
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if( viewId == R.id.refresh ) {
            progressBar.setVisibility( View.VISIBLE );
            listView.setVisibility( View.GONE );
            listView.setVisibility( View.GONE );

            new Thread(list_data_load_runnable).start();
        }
    }
}

package com.purehero.bluetooth.share.files;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.purehero.bluetooth.share.R;
import com.purehero.module.common.FileIntentUtils;
import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.fragment.CheckPermissionListener;
import com.purehero.module.fragment.FragmentEx;
import com.purehero.module.fragment.common.FunctionCreateNewFolder;
import com.purehero.module.fragment.common.FunctionDeleteSelectedItem;
import com.purehero.module.fragment.common.FunctionRenameSelectedItem;
import com.purehero.module.fragment.common.SearchTextChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * Created by purehero on 2017-03-22.
 */

public class FileListFragment extends FragmentEx
        implements SearchTextChangeListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener, CheckPermissionListener {

    private View layout = null;
    private ListView listView = null;
    private FileListAdapter listAdapter = null;
    private LinearLayout pathList = null;
    private HorizontalScrollView pathScrollView = null;
    private Activity context;
    private File root_folder = new File( "/" );

    public FileListFragment setMainActivity( Activity activity ) {
        context = activity;
        return this;
    }

    public FileListFragment setRootFolder( File root_folder ) {
        this.root_folder = root_folder;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d( "MyLOG", "FileListFragment::onCreateView" );

        layout 		= inflater.inflate( R.layout.myfile_layout, container, false);
        if( layout == null ) return null;

        setHasOptionsMenu( true );

        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            pathList        = ( LinearLayout ) layout.findViewById( R.id.pathList ) ;
            pathScrollView = ( HorizontalScrollView ) layout.findViewById( R.id.pathScrollView ) ;

            listAdapter = new FileListAdapter( context );;
            listAdapter.push_folder( root_folder, "" );

            listView.setAdapter( listAdapter );
            listView.setOnItemClickListener( this );
            listView.setOnItemLongClickListener( this );

            listUpdateRunnable.run();
        }

        return layout;
    }

    @Override
    public void onPrePause() {
        super.onPrePause();

        if( listAdapter.isSelectMode()) {           // 뒤로가기에 의한 ActionBar 전환
            changeFileListMode( true );
        }
    }

    @Override
    public boolean onBackPressed() {
        Log.d( "MyLOG", "onBackPressed" );
        try {
            if( listAdapter.isSelectMode()) {           // 뒤로가기에 의한 ActionBar 전환
                changeFileListMode( true );
                return true;
            }

            if( listAdapter.is_next_pop_folder()) {
                listAdapter.pop_folder( false );
                reloadListView();
                return true;
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return false;
    }

    private CharSequence backupActionBarTitle = null;
    private void updateSelectedCount( boolean backupPreviousValue ) {
        if( context instanceof AppCompatActivity ) {
            AppCompatActivity ACActivity = ( AppCompatActivity ) context;
            ActionBar aBar = ACActivity.getSupportActionBar();
            if( aBar != null ) {
                if( backupPreviousValue ) {
                    backupActionBarTitle = aBar.getTitle();
                }
                aBar.setTitle( String.format( "%d Selected", listAdapter.getSelectedCount()) );
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileListData data = ( FileListData ) listAdapter.getItem( position );
        data.IncrementClickCount();
        FileClickCount.saveClickCount( data );

        if( listAdapter.isSelectMode()) {                   // 파일 선택 모드일 경우
            data.setSelected( !data.isSelected());          // 선택한 항목의 선택을 반전시킨다.
            updateSelectedCount( false );                   // 선택된 항목의 개수를 화면에 갱신한다.

            listAdapter.notifyDataSetChanged();             // 리스트의 내용을 갱신시킨다.
            this.getActivity().invalidateOptionsMenu();

            return;                                         // 함수를 반환한다.
        }

        if( data.getFile().isDirectory()) {
            listAdapter.push_folder( data.getFile(), null);                     // 선택한 폴더로 리스트를 갱신시킨다.
            reloadListView();

        } else {
            Intent sendIntent = FileIntentUtils.Running( data.getFile() );
            if (sendIntent.resolveActivity( context.getPackageManager()) != null) {
                context.startActivity( Intent.createChooser( sendIntent, getString( R.string.share_select_app )) );
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        FileListData data = ( FileListData ) listAdapter.getItem( position );
        data.IncrementClickCount();
        FileClickCount.saveClickCount( data );

        if( listAdapter.isSelectMode()) {
            changeFileListMode( true );
        } else {
            changeFileSelectMode( data );
        }
        return true;
    }



    public void reflashListView() {
        listAdapter.notifyDataSetChanged();
    }

    public void reloadListView() {
        listUpdateRunnable.run();
    }


    Runnable listUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            listAdapter.reload();
            context.runOnUiThread( pathListUpdateRunnable );
            listView.smoothScrollToPosition(0);
        }
    };

    Handler pathScrollViewPosition = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pathScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
        }
    };

    Runnable pathListUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            pathList.removeAllViews();

            Vector<String> folder_names = listAdapter.getFolderNameVector();
            String name = null;

            for( int i = 0; i < folder_names.size(); i++ ) {
                name = folder_names.get(i);
                if( name.trim().isEmpty()) {
                    continue;
                }

                addPathList( name, i == folder_names.size() - 1 );
            }
            //G.Log(  "pathList.getChildCount() : %d", pathList.getChildCount());
            if( pathList.getChildCount() > 0 ) {
                pathScrollView.setVisibility( View.VISIBLE );
                pathScrollViewPosition.sendEmptyMessageDelayed( 100, 300 );
            } else {
                pathScrollView.setVisibility(View.GONE);
            }
        }
    };

    private void addPathList(String pathString, boolean isLastItem ) {
        TextView tv = new TextView( context );
        tv.setText(" / ");
        tv.setTextColor(Color.parseColor( "#CFCFFF" ));
        tv.setIncludeFontPadding( false );
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
        pathList.addView( tv );

        tv = new TextView( context );
        tv.setText(pathString);
        tv.setTextColor(Color.parseColor( "#CFCFFF" ));
        tv.setIncludeFontPadding( false );
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
        if( isLastItem ) {
            tv.setTypeface(null, Typeface.BOLD);
        }
        tv.setOnClickListener( pathListTextViewOnClickListener );
        pathList.addView( tv );
    }

    View.OnClickListener pathListTextViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView tv = ( TextView ) view;
            String name = tv.getText().toString();

            while( name.compareToIgnoreCase( listAdapter.getLastFolderName()) != 0 ) {
                listAdapter.pop_folder( false );
            }
            reloadListView();
            //context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
        }
    };

    @Override
    public boolean onQueryTextSubmit(String s) {
        listAdapter.getFilter().filter(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        listAdapter.getFilter().filter(s);
        return true;
    }



    private void function_paste_items() {
        /*
        int title_res_id = context.getOpCode() == R.id.action_move ? R.string.moving : R.string.copying;
        G.progressDialog( context, title_res_id, "", new ProgressRunnable() {
            @Override
            public void run(final ProgressDialog dialog) {
                dialog.setCancelable( true );
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        G.progressDialogCanceled = true;
                    }
                });

                List<FileListData> selected_items = context.getSelectedItems();
                long copy_byte_size = 0;
                long copied_byte_size = 0;

                for( FileListData data : selected_items ) {
                    File file = data.getFile();
                    if( file.isDirectory()) {
                        copy_byte_size += folder_length( file );
                    } else {
                        copy_byte_size += data.getFile().length();
                    }
                }

                dialog.setMax( 100 );

                byte buffer [] = new byte[ 102400 ];        // 100Kbyte
                for( FileListData data : selected_items ) {
                    if( G.progressDialogCanceled ) {
                        break;
                    }

                    File source_file = data.getFile();
                    if( source_file.isDirectory()) {
                        Stack<File> folders = new Stack<File>();
                        folders.push( source_file );

                        while( !folders.isEmpty() && !G.progressDialogCanceled ) {
                            File current_folder = folders.pop();
                            File myfile_layout[] = current_folder.listFiles();
                            for( File file : myfile_layout ) {
                                if( G.progressDialogCanceled ) break;

                                if( file.isDirectory()) {
                                    folders.push(file);
                                    continue;
                                }

                                //G.Log( "file.getAbsolutePath : %s", file.getAbsolutePath() );
                                //G.Log( "source_file.getAbsolutePath : %s", source_file.getAbsolutePath() );
                                //G.Log( "listAdapter.getLastFolder().getAbsolutePath : %s", listAdapter.getLastFolder().getAbsolutePath() );
                                File target_file = new File( file.getAbsolutePath().replace( source_file.getParentFile().getAbsolutePath(), listAdapter.getLastFolder().getAbsolutePath()));
                                //G.Log( "target_file.getAbsolutePath : %s", target_file.getAbsolutePath() );

                                target_file.getParentFile().mkdirs();
                                try {
                                    copied_byte_size = file_copy( file, target_file, buffer, copied_byte_size, copy_byte_size, dialog );
                                } catch( Exception e ) {
                                    e.printStackTrace();
                                }

                                if( G.progressDialogCanceled) {
                                    try {
                                        FileUtils.forceDelete( target_file );   // Cancel 이 눌러질때 동작 중이던 파일은 삭제 한다.
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                    } else {
                        File target_file = new File( listAdapter.getLastFolder(), source_file.getName());
                        try {
                            copied_byte_size = file_copy( source_file, target_file, buffer, copied_byte_size, copy_byte_size, dialog );
                        } catch( Exception e ) {
                            e.printStackTrace();
                        }

                        if( G.progressDialogCanceled) {
                            try {
                                FileUtils.forceDelete( target_file );   // Cancel 이 눌러질때 동작 중이던 파일은 삭제 한다.
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                dialog.setProgress( 100 );

                context.runOnUiThread( new Runnable(){
                    @Override
                    public void run() {
                        reloadListView();
                        changeFileListMode( true ); // 선택된 항목의 작업이 모두 완료 되었기 때문에 기억된 선택 사항도 함께 삭제한다.
                    }
                } );
            }
        });
        */
    }


    private long file_copy(File source_file, File target_file, byte[] buffer, long copied_byte_size, long copy_byte_size, final ProgressDialog dialog) throws IOException {
        final String source_file_name = source_file.getName();
        context.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                dialog.setMessage( source_file_name );
            }
        });

        int nRead = 0;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream( source_file );
            fos = new FileOutputStream( target_file );

            //while(( nRead = fis.read( buffer, 0, 102400 )) > 0 && !G.progressDialogCanceled ) {
            while(( nRead = fis.read( buffer, 0, 102400 )) > 0 ) {
                fos.write( buffer, 0, nRead );

                copied_byte_size += nRead;
                dialog.setProgress((int) ( copied_byte_size * 100 / copy_byte_size ));

                try { Thread.sleep( 10 ); } catch (InterruptedException e) { e.printStackTrace(); }
            }

        } catch( IOException e ) {
            throw e;

        } finally {
            if( fis != null ) {
                try { fis.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            if( fos != null ) {
                try { fos.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }

        /*
        if( context.getOpCode() == R.id.action_move ) {
            try {
                FileUtils.forceDelete( source_file );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        return copied_byte_size;
    }

    /**
     * 폴더내에 존재하는 파일의 전체 크기를 반환한다. 하위 폴더 포함
     *
     * @param base_folder
     * @return
     */
    private long folder_length(File base_folder) {
        long ret = 0;

        if( !base_folder.isDirectory()) return ret;

        Stack<File> folders = new Stack<File>();
        folders.add( base_folder );

        while( !folders.isEmpty()) {
            File file_list [] = folders.pop().listFiles();
            for( File file : file_list ) {
                if( file.isDirectory()) {
                    folders.push( file );
                    continue;
                }
                ret += file.length();
            }
        }
        return ret;
    }

    private void function_move_selected_items() {
        /*
        context.collectSelectedItems(); // UI에서 선택된 항목을 기억한다.
        context.setOpCode( R.id.action_move );
        */

        changeFileListMode( false );   // 기억된 선택항목은 유지 시킨다.
    }

    private void function_copy_selected_items() {
        /*
        context.collectSelectedItems(); // UI에서 선택된 항목을 기억한다.
        context.setOpCode( R.id.action_copy );
        */

        changeFileListMode( false );    // 기억된 선택항목은 유지 시킨다.
    }



    private void changeFileSelectMode( FileListData data ) {
        listAdapter.setSelectMode( true );  // 파일 선택모드로 전환한다.
        listAdapter.setSelectALL( false );  // 이전에 선택한 사항들을 초기화 시킨다.
        if( data != null ) {
            data.setSelected(true);         // 클릭한 항목은 기본으로 선택한다.
        }

        updateSelectedCount( true );

        if( context instanceof AppCompatActivity ) {
            AppCompatActivity ACActivity = ( AppCompatActivity ) context;
            ActionBar aBar = ACActivity.getSupportActionBar();
            if( aBar != null && backupActionBarTitle == null ) {
                backupActionBarTitle = aBar.getTitle();
                Log.d( "MyLOG", "aaa : " + backupActionBarTitle );
                aBar.setTitle( String.format( "%d Selected", listAdapter.getSelectedCount()) );
            }
        }
        listAdapter.notifyDataSetChanged();                 // 데이터가 변경되어 리스트를 갱신한다.
        this.getActivity().invalidateOptionsMenu();                    // option menu 항목을 갱신시킨다.
    }

    private void changeFileListMode( boolean bClearSelectedItems ) {
        listAdapter.setSelectMode( false ); // 파일 선택모드를 해제한다.

        if( context instanceof AppCompatActivity ) {
            AppCompatActivity ACActivity = ( AppCompatActivity ) context;
            if( backupActionBarTitle != null ) {
                ACActivity.getSupportActionBar().setTitle(backupActionBarTitle);
                backupActionBarTitle = null;
            }
        }

        if( bClearSelectedItems ) {
            listAdapter.setSelectALL( false );
        }

        listAdapter.notifyDataSetChanged();         // 데이터가 변경되어 리스트를 갱신한다.
        this.getActivity().invalidateOptionsMenu();                    // option menu 항목을 갱신시킨다.
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if( itemId ==  R.id.refresh ) {
            listAdapter.reload();
        }
    }

    /**
     * Fragment 에서 필요한 Permission 들을 반환하여 준다.
     * <br> Permission 들을 반환하여 주면 Main Activity 에서 수집하여 한번에 Permission 요청을 한다.
     *
     * @return 필요한 Permission 리스트
     */
    @Override
    public List<String> requestPermissionList() {
        Log.d( "MyLOG", "requestPermissionList()");

        List<String> requestPermissions = new ArrayList<String>();
        requestPermissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        requestPermissions.add( Manifest.permission.READ_EXTERNAL_STORAGE );

        return requestPermissions;
    }




    /*


    @Override
    public boolean onOptionsItemSelected(int id) {
        switch( id ) {
            case android.R.id.home:
                if( listAdapter.is_next_pop_folder()) {
                    listAdapter.pop_folder( false );
                    reloadListView();

                    //context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
                    return true;
                }
                break;



            case R.id.action_copy :                     // 파일 복사
                function_copy_selected_items();
                return true;

            case R.id.action_move :                     // 파일 이동
                function_move_selected_items();
                return true;

            case R.id.action_paste :                    // 파일 복사/이동, 붙여 넣기
                function_paste_items();
                return true;

            case R.drawable.ck_checked :                // 전체 파일 선택
                listAdapter.setSelectALL( true );     // 현재 폴더의 전체를 선택한다.
                listAdapter.notifyDataSetChanged();
                context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() );
                break;

            case R.drawable.ck_nomal :                  // 전체 파일 선택 취소
                listAdapter.setSelectALL( false );    // 현재 폴더의 전체 선택의 취소한다.
                listAdapter.notifyDataSetChanged();
                context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() );
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch( requestCode ) {
            case 100 :  // FTP Server 설정
                Button btn = ( Button ) layout.findViewById(R.id.btnFtpServerSW);
                if( btn != null ) {
                    if( MyFtpServer.getInstance(getActivity()).isStartedFtpServer()) {
                        btn.setBackgroundResource(R.drawable.ftp_server_on);
                    } else {
                        btn.setBackgroundResource(R.drawable.ftp_server_off);
                    }
                }
                break;
        }
    }


    // 메뉴 클릭
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean ret = false;
        // 클릭된 APK 정보
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        FileListData data = ( FileListData ) listAdapter.getItem( info.position );

        //G.Log( "onContextItemSelected index : " + info.position );
        int itemId = item.getItemId();
        if( itemId == R.id.FILE_MENU_RUNNING ) {
            file_running(data);
            ret = true;
        } else if( itemId == R.id.FILE_MENU_DELETE ) {
            file_delete(data, info.position);
            ret = true;
        } else if( itemId == R.id.FILE_MENU_SHARE ) {
            file_share( data );
            ret = true;
        }

        return ret;
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d( "MyLOG", "onCreateOptionsMenu" );

        if( listAdapter.isSelectMode()) {
            context.getMenuInflater().inflate( R.menu.myfile_option_menu_select_mode, menu );
        } else {
            context.getMenuInflater().inflate( R.menu.myfile_option_menu_list_mode, menu );
        }
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menu_item = menu.findItem( R.id.action_create_folder );    // 새 폴더 생성
        if( menu_item != null ) {
            if( !listAdapter.getLastFolder().canWrite() ) {
                menu_item.setTitle( String.format( "%s(%s)",  getString( R.string.create_folder), getString( R.string.read_only )) );
                menu_item.setEnabled( false );  // 현재 폴더에 쓰기 권한이 없다면 비활성 시킨다.
            } else {
                menu_item.setTitle( getString( R.string.create_folder));
                menu_item.setEnabled( true );
            }
        }

        menu_item = menu.findItem( R.id.action_copy );                     // 복사 하기 메뉴
        if( menu_item != null ) {
            menu_item.setEnabled( listAdapter.getSelectedCount() > 0 );
        }

        menu_item = menu.findItem( R.id.action_move );                     // 이동 하기 메뉴
        if( menu_item != null ) {
            menu_item.setEnabled( listAdapter.getSelectedCount() > 0 );
        }

        menu_item = menu.findItem( R.id.action_rename );                     // 이름변경 메뉴
        if( menu_item != null ) {
            menu_item.setEnabled( listAdapter.getSelectedCount() == 1 );
        }

        menu_item = menu.findItem( R.id.action_paste );                     // 붙여 넣기 메뉴
        if( menu_item != null ) {
            if( !listAdapter.getLastFolder().canWrite() ) {
                menu_item.setTitle( String.format( "%s(%s)", getString( R.string.paste), getString( R.string.read_only )) );
                menu_item.setEnabled( false );  // 현재 폴더에 쓰기 권한이 없다면 비활성 시킨다.
            } else {
                menu_item.setTitle( getString( R.string.paste));
                menu_item.setEnabled( listAdapter.getSelectedCount() > 0 );
            }
        }

    }

    /**
     * 새로운 폴더를 생성한다.
     */
    private void function_create_new_folder() {
        Log.d( "MyLOG", "function_create_new_folder" );

        new FunctionCreateNewFolder( context, listAdapter.getLastFolder(), new OnSuccessListener(){
            @Override
            public void OnSuccess() {
                listAdapter.reload();                                               // 리스트를 갱신 시킨다.
            }
        } ).run();
    }

    /**
     * 이름을 변경한다.
     */
    private void function_rename_selected_item() {
        Log.d( "MyLOG", "function_rename_selected_item" );

        new FunctionRenameSelectedItem( context, listAdapter.getSelectedItems().get(0).getFile(), new OnSuccessListener(){
            @Override
            public void OnSuccess() {
                listAdapter.setSelectMode(false);
                listAdapter.reload();                                               // 리스트를 갱신 시킨다.
            }
        }).run();
    }

    /**
     * 선택된 파일/폴더들을 삭제한다.
     */
    private void function_delete_selected_items() {
        Log.d( "MyLOG", "function_delete_selected_items" );

        List<File> delete_files = new ArrayList<File>();
        for( FileListData fData : listAdapter.getSelectedItems() ) {
            delete_files.add( fData.getFile());
        }

        new FunctionDeleteSelectedItem( context, delete_files, new OnSuccessListener(){
            @Override
            public void OnSuccess() {
                listAdapter.setSelectMode(false);
                listAdapter.reload();                                               // 리스트를 갱신 시킨다.
            }
        } ).run();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d("MyLOG", "onOptionsItemSelected " + id);

        if (id == R.id.action_select_mode) {                // 파일 선택 모드
            changeFileSelectMode(null);
        } else if (id == R.id.action_create_folder) {       // 새 폴더 생성
            function_create_new_folder();
        } else if (id == R.id.action_rename ) {             // 이름 변경
            function_rename_selected_item();
        } else if( id == R.id.action_delete ) {             // 삭제
            function_delete_selected_items();
        } else {
            return false;
        }

        return true;
    }
}
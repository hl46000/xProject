package share.file.purehero.com.fileshare;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.purehero.common.FragmentEx;
import com.purehero.common.G;
import com.purehero.common.ProgressRunnable;
import com.purehero.ftpserver.settings.FtpServerSettingsActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;


/**
 * Created by MY on 2017-02-25.
 */

public class FileListFragment extends FragmentEx implements SearchTextChangeListener, OptionsItemSelectListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    private View layout = null;
    private ListView listView = null;
    private FileListAdapter listAdapter = null;
    private LinearLayout pathList = null;
    private HorizontalScrollView pathScrollView = null;
    private MainActivity context;
    private File root_folder = new File( "/" );

    public FileListFragment setMainActivity( MainActivity activity ) {
        context = activity;
        return this;
    }

    public FileListFragment setRootFolder( File root_folder ) {
        this.root_folder = root_folder;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout 		= inflater.inflate( R.layout.file_list, container, false);
        if( layout == null ) return null;

        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            pathList        = ( LinearLayout ) layout.findViewById( R.id.pathList ) ;
            pathScrollView = ( HorizontalScrollView ) layout.findViewById( R.id.pathScrollView ) ;

            listAdapter = new FileListAdapter( context );;
            listAdapter.push_folder( root_folder, "" );

            listView.setAdapter( listAdapter );
            listView.setOnItemClickListener( this );
            listView.setOnItemLongClickListener( this );

            //new Thread( listUpdateRunnable ).start();
            listUpdateRunnable.run();

            int btnIDs[] = { R.id.btnFtpServerSW };
            for( int id : btnIDs ) {
                Button btn = ( Button ) layout.findViewById(id);
                if( btn != null ) {
                    btn.setOnClickListener( this );
                }
            }
        }

        return layout;
    }

    @Override
    public boolean onBackPressed() {
        try {
            if( listAdapter.isSelectMode()) {           // 뒤로가기에 의한 ActionBar 전환
                context.changeFileListModeToolbar();       // 선택모드를 해제한다.
                context.clearSelectedItems(true);          // 선택된 항목이 있다면 Clear 한다. 기억도 삭제한다.
                listAdapter.notifyDataSetChanged();         // 리스트를 갱신한다.
                return true;
            }

            if( listAdapter.is_next_pop_folder()) {
                listAdapter.pop_folder( false );
                reloadListView();

                context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
                return true;
            }
        } catch( Exception e ) {
            e.printStackTrace();
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

    Handler pathScrollViewPosition = new Handler(){
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
                pathScrollView.setVisibility( View.GONE );
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
            context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menu_item = menu.findItem( R.id.action_create_folder );    // 새 폴더 생성
        if( menu_item != null ) {
            if( !listAdapter.getLastFolder().canWrite() ) {
                menu_item.setTitle( String.format( "%s(%s)",  getString( R.string.action_create_folder), getString( R.string.read_only )) );
                menu_item.setEnabled( false );  // 현재 폴더에 쓰기 권한이 없다면 비활성 시킨다.
            } else {
                menu_item.setTitle( getString( R.string.action_create_folder));
                menu_item.setEnabled( true );
            }
        }

        menu_item = menu.findItem( R.id.action_paste );                     // 붙여 넣기 메뉴
        if( menu_item != null ) {
            if( !listAdapter.getLastFolder().canWrite() ) {
                menu_item.setTitle( String.format( "%s(%s)", getString( R.string.action_paste), getString( R.string.read_only )) );
                menu_item.setEnabled( false );  // 현재 폴더에 쓰기 권한이 없다면 비활성 시킨다.
            } else {
                menu_item.setTitle( getString( R.string.action_paste));
                menu_item.setEnabled( true );
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(int id) {
        switch( id ) {
            case android.R.id.home:
                if( listAdapter.is_next_pop_folder()) {
                    listAdapter.pop_folder( false );
                    reloadListView();

                    context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
                    return true;
                }
                break;

            case R.id.action_select_mode    :           // 파일 선택 모드
                changeFileSelectMode();
                return true;

            case R.id.action_create_folder  :           // 새 폴더 생성
                function_create_new_folder();
                return true;

            case R.id.action_rename :                   // 이름 변경
                function_rename_selected_item();
                return true;

            case R.id.action_delete :                   // 선택 항목 삭제
                function_delete_selected_items();
                return true;

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

    private void function_paste_items() {
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
                            File file_list[] = current_folder.listFiles();
                            for( File file : file_list ) {
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

            while(( nRead = fis.read( buffer, 0, 102400 )) > 0 && !G.progressDialogCanceled ) {
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

        if( context.getOpCode() == R.id.action_move ) {
            try {
                FileUtils.forceDelete( source_file );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        context.collectSelectedItems(); // UI에서 선택된 항목을 기억한다.
        context.setOpCode( R.id.action_move );

        changeFileListMode( false );   // 기억된 선택항목은 유지 시킨다.
    }

    private void function_copy_selected_items() {
        context.collectSelectedItems(); // UI에서 선택된 항목을 기억한다.
        context.setOpCode( R.id.action_copy );

        changeFileListMode( false );    // 기억된 선택항목은 유지 시킨다.
    }

    private void function_delete_selected_items() {
        context.collectSelectedItems(); // 전체 화면의 선택 항목을 수집한다.

        final List<FileListData> selectedItems = context.getSelectedItems();
        int item_count = selectedItems.size();

        String message = String.format( "%d %s\n\n", item_count, getString( R.string.delete_message ));

        G.no_string_res     = R.string.cancel;
        G.yes_string_res    = R.string.delete;
        G.confirmDialog( context, R.string.delete_title, message, -1, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i) {
                    case G.DIALOG_BUTTON_ID_YES :       // Clicked Delete
                        G.progressDialog( context, R.string.delete_title, "", new ProgressRunnable(){
                            @Override
                            public void run( final ProgressDialog dialog) {
                                dialog.setMax(selectedItems.size());

                                int progress_count = 0;
                                for( final FileListData data : selectedItems ) {
                                    context.runOnUiThread( new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setMessage(data.getFilename());
                                        }
                                    });

                                    try {
                                        FileUtils.forceDelete( data.getFile() );
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    dialog.setProgress( ++progress_count );
                                }
                                context.runOnUiThread( new Runnable(){
                                    @Override
                                    public void run() {
                                        reloadListView();
                                        changeFileListMode( true );
                                    }
                                } );
                            }
                        });
                        break;
                }
            }
        } );
    }

    private void function_rename_selected_item() {
        String title    = getString( R.string.rename_title);
        String text     = null;

        final File srcFile  = context.getSelectedItems().get(0).getFile();
        String hint         = srcFile.getName();

        changeFileListMode( true );             // 선택 모드에서만 호출되므로 선택모드를 해제 한다.
                                                // 이미 선택된 항목의 데이터를 획득하여기 때문에
                                                // 기억된 선택 사항도 제거한다.

        G.no_string_res     = R.string.cancel;
        G.yes_string_res    = R.string.rename;
        G.textInputDialog( context,title, text, hint, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch( i ) {
                    case G.DIALOG_BUTTON_ID_YES :
                        File destFile = new File( srcFile.getParentFile(), G.getTextInputDialogResult());
                        if( srcFile.renameTo( destFile )) {
                            listAdapter.reload();                                       // 리스트를 갱신 시킨다.
                        }
                        break;
                }
            }
        } );
        G.no_string_res     = -1; G.yes_string_res    = -1;
    }

    private void function_create_new_folder() {
        String title    = getString( R.string.create_folder_title);
        String text     = null;
        String hint     = getString( R.string.create_folder_hint);

        File targetFolder = new File( listAdapter.getLastFolder(), hint );
        if( targetFolder.exists()) {
            for( int i = 1; targetFolder.exists(); i++ ) {
                hint = String.format( "%s(%d)", getString( R.string.create_folder_hint), i );
                targetFolder = new File( listAdapter.getLastFolder(), hint );
            }
        }

        G.no_string_res     = R.string.cancel;
        G.yes_string_res    = R.string.create;
        G.textInputDialog( context,title, text, hint, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            switch( i ) {
                case G.DIALOG_BUTTON_ID_YES :
                    File newFolder = new File( listAdapter.getLastFolder(), G.getTextInputDialogResult());
                    G.Log( "Try to create folder : '%s'", newFolder.getAbsolutePath() );
                    if( newFolder.mkdirs()) {
                        listAdapter.reload();                                               // 리스트를 갱신 시킨다.
                        /*
                        context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );    // 액션바에 뒤로 가기 버튼을 표시한다.

                        listAdapter.push_folder( newFolder);                                // 생성한 폴더로 리스트를 갱신시킨다.
                        rel.run();
                        */
                    }
                    break;
            }
            }
        } );
        G.no_string_res     = -1; G.yes_string_res    = -1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileListData data = ( FileListData ) listAdapter.getItem( position );
        data.IncrementClickCount();
        FileClickCount.saveClickCount( data );

        if( listAdapter.isSelectMode()) {                                       // 파일 선택 모드일 경우
            data.setSelected( !data.isSelected());                              // 선택한 항목의 선택을 반전시킨다.
            context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() );    // 선택한 개수를 액션바에서 갱신시킨다.
            listAdapter.notifyDataSetChanged();                                 // 리스트의 내용을 갱신시킨다.
            return;                                                             // 함수를 반환한다.
        }

        if( data.getFile().isDirectory()) {
            context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );    // 액션바에 뒤로 가기 버튼을 표시한다.

            listAdapter.push_folder( data.getFile(), null);                     // 선택한 폴더로 리스트를 갱신시킨다.
            reloadListView();

        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setDataAndType( Uri.fromFile( data.getFile()), data.getMimeType());
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (sendIntent.resolveActivity( context.getPackageManager()) != null) {
                context.startActivity( Intent.createChooser( sendIntent, getString( R.string.choose_application )) );
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        FileListData data = ( FileListData ) listAdapter.getItem( position );
        data.IncrementClickCount();
        FileClickCount.saveClickCount( data );

        if( listAdapter.isSelectMode()) {
            changeFileListMode( true );             // 파일 리스트 모드로 전환한다.
                                                    // 기억된 선택항목들도 삭제한다.
        } else {
            data.setSelected( true );               // 롱 클릭한 항목은 기본으로 선택한다.
            changeFileSelectMode();                 // 파일 선택모드로 전환한다.
        }
        listAdapter.notifyDataSetChanged();                 // 데이터가 변경되어 리스트를 갱신한다.
        return true;
    }

    private void changeFileSelectMode() {
        context.changeFileSelectModeToolbar();               // 액션바를 선택 항목 개수가 나오도록 전환 시킨다.
        context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() ); // 액션바에 선택한 개수를 표시한다.
        //context.clearSelectedItems();                       // 이전에 선택된 항목들을 제거 한다.
        context.setOpCode( -1 );                            // 이전 명령어 코드를 삭제한다.

        listAdapter.notifyDataSetChanged();                 // 데이터가 변경되어 리스트를 갱신한다.
    }

    private void changeFileListMode( boolean bClearSelectedItems ) {
        context.changeFileListModeToolbar();                    // 액션바를 기본으로 전환시킨다.
        context.clearSelectedItems( bClearSelectedItems );      // 이전에 선택된 항목들을 제거 한다.

        listAdapter.notifyDataSetChanged();         // 데이터가 변경되어 리스트를 갱신한다.
    }

    public int getSelectedItemCount() {
        return listAdapter.getSelectedCount();
    }

    public List<FileListData> getSelectedItems() {
        return listAdapter.getSelectedItems();
    }

    public void setSelectALL(boolean b) {
        listAdapter.setSelectALL(b);
    }

    @Override
    public void onClick(View view) {
        switch( view.getId()) {
            case R.id.btnFtpServerSW :
                Intent intent = new Intent( context, FtpServerSettingsActivity.class );
                intent.putExtra("lastFolder", listAdapter.getLastFolder().getAbsolutePath());
                this.startActivityForResult( intent, 100 );
                break;
        }
    }

    public void ftpButtonVisible(boolean b) {
        Button btn = ( Button ) layout.findViewById(R.id.btnFtpServerSW);
        if( btn != null ) {
            btn.setVisibility( b ? View.VISIBLE : View.GONE );
        }
    }
}

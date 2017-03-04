package share.file.purehero.com.fileshare;


import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.purehero.common.FragmentEx;
import com.purehero.common.G;

import java.io.File;
import java.util.Vector;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListFragment extends FragmentEx implements SearchTextChangeListener, OptionsItemSelectListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private View layout = null;
    private ListView listView = null;
    private FileListAdapter listAdapter = null;
    private LinearLayout pathList = null;
    private HorizontalScrollView pathScrollView = null;
    private MainActivity context;

    public FileListFragment setMainActivity( MainActivity activity ) {
        context = activity;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        G.Log( "onCreateView" );

        layout 		= inflater.inflate( R.layout.file_list, container, false);
        if( layout == null ) return null;

        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            pathList        = ( LinearLayout ) layout.findViewById( R.id.pathList ) ;
            pathScrollView = ( HorizontalScrollView ) layout.findViewById( R.id.pathScrollView ) ;

            listAdapter = new FileListAdapter( getActivity() );;
            listAdapter.push_folder( new File("/"));

            listView.setAdapter( listAdapter );
            listView.setOnItemClickListener( this );
            listView.setOnItemLongClickListener( this );

            //new Thread( listUpdateRunnable ).start();
            listUpdateRunnable.run();
        }

        return layout;
    }

    @Override
    public boolean onBackPressed() {
        try {
            if( listAdapter.isSelectMode()) {
                listAdapter.setSelectMode( false );
                context.changeFileListModeToolbar();
                listAdapter.notifyDataSetChanged();
                return true;
            }

            if( listAdapter.is_next_pop_folder()) {
                listAdapter.pop_folder( false );
                listUpdateRunnable.run();

                context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
                return true;
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }

        return false;
    }

    Runnable listUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            listAdapter.reload();
            getActivity().runOnUiThread( pathListUpdateRunnable );
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

            Vector<File> folders = listAdapter.getFolderVector();
            for( File folder : folders ) {
                String name = folder.getName();
                if( name.trim().isEmpty()) continue;

                addPathList( name );
            }

            pathScrollViewPosition.sendEmptyMessageDelayed( 100, 1000 );
        }
    };

    private void addPathList(String pathString) {
        TextView tv = new TextView( getActivity());
        tv.setText("/");
        tv.setTextColor(Color.parseColor( "#CFCFFF" ));
        tv.setIncludeFontPadding( false );
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
        pathList.addView( tv );

        tv = new TextView( getActivity());
        tv.setText(pathString);
        tv.setTextColor(Color.parseColor( "#CFCFFF" ));
        tv.setIncludeFontPadding( false );
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
        tv.setOnClickListener( pathListTextViewOnClickListener );
        pathList.addView( tv );
    }

    View.OnClickListener pathListTextViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView tv = ( TextView ) view;
            String name = tv.getText().toString();

            while( name.compareToIgnoreCase( listAdapter.getLastFolder().getName()) != 0 ) {
                listAdapter.pop_folder( false );
            }
            listUpdateRunnable.run();
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
        G.Log( "FileListFragment::onPrepareOptionsMenu" );
        MenuItem menu_item = menu.findItem( R.id.action_create_folder );
        if( menu_item != null ) {
            menu_item.setEnabled( listAdapter.getLastFolder().canWrite());
        }
    }

    @Override
    public boolean onOptionsItemSelected(int id) {
        switch( id ) {
            case android.R.id.home:
                if( listAdapter.isSelectMode()) {
                    listAdapter.setSelectMode( false );
                    listAdapter.notifyDataSetChanged();
                    return true;
                }

                if( listAdapter.is_next_pop_folder()) {
                    listAdapter.pop_folder( false );
                    listUpdateRunnable.run();

                    context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
                    return true;
                }
                break;

            case R.id.action_select_mode    :           // 파일 선택 모드
                changeFileSelectMode();
                return true;

            case R.id.action_list_mode      :           // 파일 리스트 모드
                changeFileListMode();
                return true;

            case R.id.action_create_folder  :
                function_create_new_folder();
                return true;

            case R.drawable.ck_checked :                // 전체 파일 선택
                listAdapter.setSelectedALL( true );
                listAdapter.notifyDataSetChanged();
                context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() );
                break;
            case R.drawable.ck_nomal :                  // 전체 파일 선택 취소
                listAdapter.setSelectedALL( false );
                listAdapter.notifyDataSetChanged();
                context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() );
                break;
        }

        return false;
    }

    private void function_create_new_folder() {
        String title    = getString( R.string.create_folder_title);
        String text     = null;
        String hint     = getString( R.string.create_folder_hint);

        G.no_string_res     = R.string.cancel;
        G.yes_string_res    = R.string.create;
        G.textInputDialog(getActivity(),title, text, hint, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch( i ) {
                    case G.DIALOG_BUTTON_ID_YES :
                        File newFolder = new File( listAdapter.getLastFolder(), G.getTextInputDialogResult());
                        G.Log( "Try to create folder : '%s'", newFolder.getAbsolutePath() );
                        if( newFolder.mkdirs()) {
                            context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );    // 액션바에 뒤로 가기 버튼을 표시한다.

                            listAdapter.push_folder( newFolder);                                // 생성한 폴더로 리스트를 갱신시킨다.
                            listUpdateRunnable.run();
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
        G.Log( "onItemClick : %d %s", position, data.getFilename() );

        if( listAdapter.isSelectMode()) {                                       // 파일 선택 모드일 경우
            data.setSelected( !data.isSelected());                              // 선택한 항목의 선택을 반전시킨다.
            context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() );    // 선택한 개수를 액션바에서 갱신시킨다.
            listAdapter.notifyDataSetChanged();                                 // 리스트의 내용을 갱신시킨다.
            return;                                                             // 함수를 반환한다.
        }

        if( data.getFile().isDirectory()) {
            context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );    // 액션바에 뒤로 가기 버튼을 표시한다.

            listAdapter.push_folder( data.getFile());                           // 선택한 폴더로 리스트를 갱신시킨다.
            //new Thread( listUpdateRunnable ).start();
            listUpdateRunnable.run();
            //} else {
            //fileListView.showContextMenuForChild(view);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        FileListData data = ( FileListData ) listAdapter.getItem( position );

        if( listAdapter.isSelectMode()) {
            changeFileListMode();                   // 파일 리스트 모드로 전환한다.
        } else {
            data.setSelected( true );               // 롱 클릭한 항목은 기본으로 선택한다.
            changeFileSelectMode();                 // 파일 선택모드로 전환한다.
        }
        return true;
    }

    private void changeFileSelectMode() {
        listAdapter.setSelectMode( true );      // 파일 선택모드로 전환한다.
        context.changeFileSelectModeToolbar();         // 액션바를 선택 항목 개수가 나오도록 전환 시킨다.
        context.setSelectToolbarSelectedCount( listAdapter.getSelectedCount() ); // 액션바에 선택한 개수를 표시한다.
        listAdapter.notifyDataSetChanged();         // 데이터가 변경되어 리스트를 갱신한다.
    }

    private void changeFileListMode() {
        listAdapter.setSelectedALL( false );    // 모든 항목의 선택을 해제한다.
        listAdapter.setSelectMode( false );     // 파일 선택모드를 해제한다.
        context.changeFileListModeToolbar();           // 액션바를 기본으로 전환시킨다.
        listAdapter.notifyDataSetChanged();         // 데이터가 변경되어 리스트를 갱신한다.
    }
}

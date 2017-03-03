package share.file.purehero.com.fileshare;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.purehero.common.FragmentEx;
import com.purehero.common.G;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Stack;
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
                listAdapter.notifyDataSetChanged();
                return true;
            }

            if( listAdapter.is_next_pop_folder()) {
                listAdapter.pop_folder();
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
                addPathList( folder.getName());
            }

            pathScrollViewPosition.sendEmptyMessageDelayed( 100, 1000 );
        }
    };

    private void addPathList(String pathString) {
        Button btn = new Button( getActivity());
        btn.setText( "〉 " + pathString);
        btn.setMaxWidth( 200 );
        btn.setBackgroundResource( android.R.color.transparent );
        btn.setTextColor(Color.parseColor( "#CFCFFF" ));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        params.setMargins(0,0,0,0);
        btn.setLayoutParams( params );
        btn.setPadding(0,0,0,0);

        //pathList.removeAllViews();
        pathList.addView( btn );
    }

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
    public boolean onOptionsItemSelected(int id) {
        switch( id ) {
            case android.R.id.home:
                if( listAdapter.isSelectMode()) {
                    listAdapter.setSelectMode( false );
                    listAdapter.notifyDataSetChanged();
                    return true;
                }

                if( listAdapter.is_next_pop_folder()) {
                    listAdapter.pop_folder();
                    listUpdateRunnable.run();

                    context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
                    return true;
                }
        }
        return false;
    }

    protected void copyFileOrDirectory( File srcFile, File destFolder ) throws IOException {
        Stack<File> folders = new Stack<File>();
        do {
            if( srcFile.isDirectory()) {

            } else {
                copyFile( srcFile, destFolder );
            }
        } while( !folders.empty());
    }

    protected void copyFile(File srcFile, File destFolder) throws IOException {
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        File destFile = new File(destFolder, srcFile.getName());
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel src = null;
        FileChannel dest = null;

        try {
            src = new FileInputStream(srcFile).getChannel();
            dest = new FileOutputStream(destFile).getChannel();
            dest.transferFrom(src, 0, src.size());

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (src != null) {
                try {
                    src.close();
                } catch (Exception e) {
                }
            }
            if (dest != null) {
                try {
                    dest.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileListData data = ( FileListData ) listAdapter.getItem( position );
        G.Log( "onItemClick : %d %s", position, data.getFilename() );

        if( listAdapter.isSelectMode()) {
            data.setSelected( !data.isSelected());
            listAdapter.notifyDataSetChanged();
            return;
        }

        if( data.getFile().isDirectory()) {
            context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );

            listAdapter.push_folder( data.getFile());
            //new Thread( listUpdateRunnable ).start();
            listUpdateRunnable.run();
            //} else {
            //fileListView.showContextMenuForChild(view);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        listAdapter.setSelectMode( !listAdapter.isSelectMode());
        listAdapter.notifyDataSetChanged();

        return true;
    }
}

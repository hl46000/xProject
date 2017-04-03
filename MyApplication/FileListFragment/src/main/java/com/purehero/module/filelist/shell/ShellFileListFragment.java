package com.purehero.module.filelist.shell;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.purehero.module.filelistfragment.R;
import com.purehero.module.tabhost.FragmentEx;

import java.io.File;

/**
 * Created by purehero on 2017-03-22.
 */

public class ShellFileListFragment extends FragmentEx implements AdapterView.OnItemClickListener {

    private View layout = null;
    private ListView listView = null;
    private ShellFileListAdapter listAdapter = null;
    private Activity context;
    private File root_folder = new File( "/" );

    public ShellFileListFragment setMainActivity(Activity activity ) {
        context = activity;
        return this;
    }

    public ShellFileListFragment setRootFolder(File root_folder ) {
        this.root_folder = root_folder;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout 		= inflater.inflate( R.layout.file_list, container, false);
        if( layout == null ) return null;

        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            listAdapter = new ShellFileListAdapter( context );

            listView.setOnItemClickListener( this );
            listView.setAdapter( listAdapter );
        }

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileData data = ( FileData ) listAdapter.getItem( position );

        if( data.isDirectory()) {
            listAdapter.push_folder( data.getAbsolutePath(), null );                     // 선택한 폴더로 리스트를 갱신시킨다.
        }
    }

    @Override
    public boolean onBackPressed() {
        if( listAdapter.pop_folder()) return true;

        return super.onBackPressed();
    }
}
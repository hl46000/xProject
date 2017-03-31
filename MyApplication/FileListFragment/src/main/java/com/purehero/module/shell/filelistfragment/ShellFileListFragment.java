package com.purehero.module.shell.filelistfragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.purehero.module.common.CheckPermissionListener;
import com.purehero.module.common.FileIntentUtils;
import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.filelistfragment.FileClickCount;
import com.purehero.module.filelistfragment.FileListAdapter;
import com.purehero.module.filelistfragment.FileListData;
import com.purehero.module.common.FunctionCreateNewFolder;
import com.purehero.module.common.FunctionRenameSelectedItem;
import com.purehero.module.filelistfragment.R;
import com.purehero.module.common.SearchTextChangeListener;
import com.purehero.module.tabhost.FragmentEx;

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
}
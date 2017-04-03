package com.purehero.module.filelist.shell;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.purehero.module.filelistfragment.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by purehero on 2017-03-31.
 */

class ShellFileListAdapter extends BaseAdapter {
    final Activity context;
    ShellProcess shell = new ShellProcess();
    public static String USER = "";
    private Stack<String> path_stack = new Stack<String>();

    private List<FileData> listData = new ArrayList<FileData>();    // 전체 데이터


    public ShellFileListAdapter(Activity context) {
        this.context = context;

        shell.command( "su" );
        USER = shell.command( "whoami" ).get(0);

        push_folder( "/", null );
    }

    private void reload() {
        listData.clear();

        String currentPath = path_stack.lastElement();

        shell.command( "cd " +  currentPath );
        List<String> result = shell.command( "ls -l");
        for( String line : result ) {
            listData.add( new FileData( currentPath, line ));
        }

        Collections.sort( listData, FileData.ALPHA_COMPARATOR );
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void push_folder( String absolutePath, Object o) {
        path_stack.push( absolutePath );
        reload();
    }

    public boolean pop_folder() {
        if( path_stack.size() == 1 ) return false;

        String currentPath = path_stack.pop();
        reload();

        return true;
    }

    class ViewHolder
    {
        public CheckBox cbSelected;
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvDate;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if( view == null ) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view  = inflater.inflate( R.layout.file_list_cell, null );

            viewHolder.cbSelected   = (CheckBox)  view.findViewById( R.id.file_list_view_item_checkbox );
            //viewHolder.cbSelected.setOnClickListener( this );
            viewHolder.ivIcon 		= (ImageView) view.findViewById( R.id.file_list_view_item_icon );
            viewHolder.tvTitle 	    = (TextView)  view.findViewById( R.id.file_list_view_item_file_name );
            viewHolder.tvSubTitle 	= (TextView)  view.findViewById( R.id.file_list_view_item_sub_title );
            viewHolder.tvDate 		= (TextView)  view.findViewById( R.id.file_list_view_item_date );

            view.setTag( viewHolder );
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        FileData data = ( FileData ) getItem( position );

        viewHolder.tvTitle.setText( data.getName());
        viewHolder.tvSubTitle.setVisibility( View.VISIBLE );
        viewHolder.tvSubTitle.setText( data.getInfoString());
        viewHolder.tvDate.setVisibility( View.VISIBLE );
        viewHolder.tvDate.setText( data.getDateString());

        int res_id = data.getResourceIcon();
        Glide.with( context ).load( res_id ).into( viewHolder.ivIcon );

        return view;
    }
}

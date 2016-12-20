package com.purehero.apk.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter 
{
	private final Context context;
	private List<FileListData> listData = new ArrayList<FileListData>();
	private Stack<File> folders 		= new Stack<File>();
	
	public FileListAdapter( Context context, File base ){
		super();
		this.context = context;
		
		push_folder( base );		
	}
	
	public void push_folder( File base ) {
		folders.push( base );
		reload( context );
	}
	
	public void back_folder() {
		folders.pop();
		reload( context );
	}
	
	public String getFolderPath() {
		String ret = "/";
		for( File folder : folders ) {
			if( ret.length() > 1 ) ret += " > ";
			ret += folder.getName();
		}
		return ret;
	}
	
	private void reload( Context context ){
		listData.clear();
		
		File base = new File("/");
		if( !folders.empty()) {
			base = folders.lastElement();
			listData.add(new FileListData( null, context, true ));
		}
		
		File subItems [] = base.listFiles();
		if( subItems != null ) {
		    for( File file : base.listFiles() ) {
		    	listData.add( new FileListData( file, context, false ));	    	
		    }
		}
		dataChanged();
	}
		
	public void remove( int index ) {
		listData.remove( index );
		dataChanged();
	}
	
	public void sort() {
		Collections.sort( listData, FileListData.ALPHA_COMPARATOR );
		dataChanged();
	}
	
	/**
	 * 데이터가 갱신 되었음을 리스트에게 알림
	 */
	public void dataChanged() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int index) {
		return listData.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent ) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();
			
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.file_list_view_item, null );
			
			viewHolder.icon 		= (ImageView) convertView.findViewById( R.id.file_list_view_item_icon );
			viewHolder.fileName 	= (TextView)  convertView.findViewById( R.id.file_list_view_item_file_name );
			viewHolder.tvSubTitle 	= (TextView)  convertView.findViewById( R.id.file_list_view_item_sub_title );
			viewHolder.tvDate 		= (TextView)  convertView.findViewById( R.id.file_list_view_item_date );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		FileListData data = listData.get( position );
		if( data.isBackFolder()) {
			viewHolder.tvSubTitle.setVisibility( View.GONE );
			viewHolder.tvDate.setVisibility( View.GONE );
			viewHolder.icon.setVisibility( View.GONE );
			
		} else {
			viewHolder.tvSubTitle.setVisibility( View.VISIBLE );
			viewHolder.tvSubTitle.setText( data.getSubTitle());
			viewHolder.tvDate.setVisibility( View.VISIBLE );
			viewHolder.tvDate.setText( data.getFileDate());
			
			Drawable icon = data.getIcon();
			if( icon == null ) {
				viewHolder.icon.setVisibility( View.INVISIBLE );
			} else {
				viewHolder.icon.setImageDrawable( icon );
				viewHolder.icon.setVisibility( View.VISIBLE );
			}
		}
		viewHolder.fileName.setText( data.getFilename());
		
		return convertView;
	}
	
	class ViewHolder 
	{
		public ImageView icon;
		public TextView fileName;
		public TextView tvSubTitle;
		public TextView tvDate;
	}
}

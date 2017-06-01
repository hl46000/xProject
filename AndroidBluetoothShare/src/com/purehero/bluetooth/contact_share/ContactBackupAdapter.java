package com.purehero.bluetooth.contact_share;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.purehero.bluetooth.contact_share.R;
import com.purehero.common.G;
import com.purehero.common.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactBackupAdapter extends BaseAdapter {
	private final Activity context;
	private List<File> listDatas = new ArrayList<File>();
	
	public ContactBackupAdapter( Activity context ) {
		this.context = context;		
	}
	
	public synchronized void getBackupContactDatas() {
		listDatas.clear();
		
		File backup_folder = new File( G.getCacheFolderPath( context, context.getString( R.string.backup_folder), true ));
		if( backup_folder.exists()) {
			File files [] = backup_folder.listFiles();
			for( File file : files ) {
				if( file.isDirectory()) continue;
				if( !file.getName().toLowerCase().endsWith(".vcf")) continue;
				
				listDatas.add( file );
			}
		}
		
		notifyDataSetChanged();
	}
	
	@Override
	public synchronized int getCount() {
		return listDatas.size();
	}

	@Override
	public synchronized Object getItem(int index) {
		return listDatas.get(index);
	}

	@Override
	public synchronized long getItemId(int position) {
		return position;
	}

	SimpleDateFormat data_format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	@Override
	public synchronized View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();
			
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.contact_backup_list_cell, null );
			
			viewHolder.icon	= (ImageView) convertView.findViewById( R.id.backup_icon );
			viewHolder.name	= (TextView)  convertView.findViewById( R.id.backup_name );
			viewHolder.size	= (TextView)  convertView.findViewById( R.id.backup_size );
			viewHolder.date	= (TextView)  convertView.findViewById( R.id.backup_date );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		File backup_file = ( File ) getItem( position );
		viewHolder.icon.setImageResource( R.drawable.ic_backup_contact );
		viewHolder.name.setVisibility( View.VISIBLE );
		viewHolder.name.setText( Utils.getPureFilename( backup_file ));
		viewHolder.size.setText( Utils.sizeToFormatString( backup_file.length() ));
		viewHolder.date.setText( data_format.format( new Date( backup_file.lastModified() )));
		
		return convertView;
	}
	
	class ViewHolder {
		public ImageView icon;
		public TextView name;		
		public TextView size;
		public TextView date;
	}

	private boolean showCheckBox = false;
	public boolean isShowCheckBox() {
		return showCheckBox;
	}
	public void setShowCheckBox( boolean show ) {
		showCheckBox = show;
		notifyDataSetChanged();
	}

	public void remove(int position) {
		listDatas.get( position ).delete();
		listDatas.remove( position );
		this.notifyDataSetChanged();
	}

	public void restore(int position) {
		File backup_file = listDatas.get( position );
		Uri uri = Uri.fromFile( backup_file );
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setDataAndType( uri,"text/x-vcard");
	    context.startActivity(intent);
	}
}

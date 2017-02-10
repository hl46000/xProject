package com.purehero.bluetooth.share;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.purehero.contact.ContactData;

public class ContactBackupAdapter extends BaseAdapter {
	private final Activity context;
	private List<ContactBackupData> listDatas = new ArrayList<ContactBackupData>();
	
	public ContactBackupAdapter( Activity context ) {
		this.context = context;		
	}
	
	class ContactBackupData {
		public File file = null;
		public boolean isSelected = false;
	};
	
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
		
		ContactBackupData data = ( ContactBackupData ) getItem( position );
		if( data != null ) {
			Drawable icon = null;// data.getIcon();
			if( icon == null ) {
				viewHolder.icon.setImageResource( R.drawable.ic_contact );
			} else {
				viewHolder.icon.setImageDrawable( icon );				
			}
			
			viewHolder.name.setVisibility( View.VISIBLE );
			viewHolder.name.setText( data.file.getName());
			viewHolder.size.setText( String.valueOf( data.file.length() ));
			viewHolder.date.setText( data.file.lastModified() ));
			
		} else {
			viewHolder.icon.setVisibility( View.INVISIBLE );
			viewHolder.name.setText( "" );
		}
		
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
	
	public synchronized int getCheckedCount() {
		int ret = 0;
		for( ContactBackupData data : listDatas ) {
			if( data.isSelected ) ++ret;
		}
		return ret;
	}
	
	public synchronized void setAllChecked( boolean checked ) {
		for( ContactBackupData data : listDatas ) {
			data.isSelected = checked;
		}
	}
}

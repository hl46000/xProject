package com.purehero.bluetooth.share;

import com.purehero.common.G;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {
	
	private final Context context;
	private Cursor cursor = null;
	
	public ContactAdapter( Context context ) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		int count = cursor == null ? 0 : cursor.getCount();
		G.Log( "getCount : %d", count );
		return count;
	}

	@Override
	public Object getItem(int index) {
		if( cursor == null ) return null;
		cursor.moveToFirst();
		if( cursor.move( index )) {
			return cursor;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();
			
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.contact_list_cell, null );
			
			viewHolder.icon	= (ImageView) convertView.findViewById( R.id.contact_icon );
			viewHolder.name	= (TextView)  convertView.findViewById( R.id.contact_name );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		Cursor data = ( Cursor ) getItem( position );
		if( data != null ) {
			G.Log( "getItem(%d) not null", position );
			
			Drawable icon = getIcon( data );
			if( icon == null ) {
				viewHolder.icon.setImageResource( R.drawable.ic_contact );
			} else {
				viewHolder.icon.setImageDrawable( icon );				
			}
			
			viewHolder.name.setVisibility( View.VISIBLE );
			viewHolder.name.setText( getName( data ));
			
		} else {
			G.Log( "getItem(%d) null", position );
			
			viewHolder.icon.setVisibility( View.INVISIBLE );
			viewHolder.name.setText( "" );
		}
		
		return convertView;
	}

	private String getName(Cursor data) {
		return cursor.getString(cursor.getColumnIndex( ContactsContract.Contacts.DISPLAY_NAME ));		
	}

	private Drawable getIcon(Cursor data) {
		return null;
	}

	class ViewHolder {
		public ImageView icon;
		public TextView name;		
	}
	
	public void getContactDatas() {
		G.Log( "getContactDatas" );
		
		final Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
		
		ContentResolver contentResolver = context.getContentResolver();
        cursor = contentResolver.query( CONTACT_URI, null, null, null, null );
	}
}

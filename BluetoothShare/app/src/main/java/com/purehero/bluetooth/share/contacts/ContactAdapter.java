package com.purehero.bluetooth.share.contacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.purehero.bluetooth.share.R;
import com.purehero.bluetooth.share.apps.ApkListData;

public class ContactAdapter extends BaseAdapter implements Filterable, View.OnClickListener {
	
	private final Activity context;
	private List<ContactData> listDatas 	= new ArrayList<ContactData>();
	private List<ContactData> filteredData 	= new ArrayList<ContactData>();

	public ContactAdapter( Activity context ) {
		this.context = context;
	}
	
	/* 
	 * 필터링된( 검색어가 적용된 ) Contact의 개수를 반환한다. 
	 */
	@Override
	public synchronized int getCount() {
		return filteredData.size();
	}

	@Override
	public synchronized Object getItem(int index) {
		return filteredData.get( index );
	}

	@Override
	public synchronized long getItemId(int position) {
		return position;
	}
	
	/**
	 * Contact_id 값에 해당하는  ContactData 객체를 반환한다. 
	 * 
	 * @param contact_id
	 * @return
	 */
	public synchronized ContactData getItemByContactID(long contact_id) {
		for( ContactData data : listDatas ) {
			if( data.getContactID() == contact_id ) {
				return data;
			}
		}
		return null;
	}

	@Override
	public synchronized View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();

			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = null;

			if( parent instanceof ListView) {
				convertView  = inflater.inflate( R.layout.contacts_list_cell_layout, null );
			} else if( parent instanceof GridView) {
				convertView  = inflater.inflate( R.layout.contacts_grid_cell_layout, null );
			}

			viewHolder.checkBox = ( CheckBox )convertView.findViewById( R.id.check_box );
			viewHolder.icon	= (ImageView) convertView.findViewById( R.id.contact_icon );
			viewHolder.name	= (TextView)  convertView.findViewById( R.id.contact_name );

			viewHolder.checkBox.setOnClickListener( this );

			convertView.setTag( viewHolder );
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.checkBox.setVisibility( isSelectMode() ? View.VISIBLE : View.GONE );
		viewHolder.checkBox.setId( position );

		ContactData data = ( ContactData ) getItem( position );
		Drawable icon = data.getIcon();
		if( icon == null ) {
			viewHolder.icon.setImageResource( R.drawable.ic_contact );
		} else {
			viewHolder.icon.setImageDrawable( icon );
		}

		viewHolder.name.setVisibility( View.VISIBLE );
		viewHolder.name.setText( data.getDisplayName());
		viewHolder.checkBox.setChecked( data.isSelected() );
        viewHolder.checkBox.setTag( data );

		return convertView;
	}

	@Override
	public void onClick(View v) {
		if( v instanceof CheckBox ) {
			ContactData data = (ContactData) v.getTag();
			CheckBox cb = ( CheckBox ) v;

			data.setSelected( cb.isChecked());
		}
	}

	class ViewHolder {
		public CheckBox checkBox;
		public ImageView icon;
		public TextView name;
	}
	
	Thread IconUpdateThread = null;
	boolean iconUpdateThreadFlag = true;
	public synchronized void getContactDatas() {
		final Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
		
		if( IconUpdateThread != null && IconUpdateThread.isAlive()) {
			iconUpdateThreadFlag = false;
			try {
				IconUpdateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		listDatas.clear();
		
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query( CONTACT_URI, null, null, null, null );
        if( cursor.getCount() > 0 ) {
        	while( cursor.moveToNext()) {
	        	try {
	        		ContactData data = new ContactData( context, cursor );
					listDatas.add( data );
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
        }
        
        Collections.sort( listDatas, ContactData.ALPHA_COMPARATOR );
        
        filteredData.clear();
        filteredData.addAll( listDatas );
        
        IconUpdateThread = new Thread( new Runnable(){
			@Override
			public void run() {
				int len = listDatas.size();
				for( int i = 0; i < len && iconUpdateThreadFlag; i++ ) {
					ContactData data = listDatas.get(i);
					if( data.loadData( context )) {
						context.runOnUiThread( new Runnable(){
							@Override
							public void run() {
								ContactAdapter.this.notifyDataSetChanged();
							}});						
					}
				}
			}});
        iconUpdateThreadFlag = true;
        IconUpdateThread.start();
	}


	boolean bSelectMode = false;
	public boolean isSelectMode() {
		return bSelectMode;
	}
	public void setSelectMode( boolean bMode ) {
		boolean bChanged = bSelectMode != bMode;

		bSelectMode = bMode;
		if( bChanged ) {
			for( ContactData data : filteredData ) {
				data.setSelected( false );
			}
			notifyDataSetChanged();
		}
	}

	public int getSelectedItemCount() {
		int ret = 0;
		for( ContactData data : filteredData ) {
			if( data.isSelected()) ++ ret;
		}
		return ret;
	}

	public List<ContactData> getSelectedItem() {
		List<ContactData> ret = new ArrayList<ContactData>();

		for( ContactData data : filteredData ) {
			if( data.isSelected()) {
				ret.add( data );
			}
		}
		return ret;
	}

	public synchronized void setAllSelected( boolean bSelect ) {
		for( ContactData data : filteredData ) {
			data.setSelected( bSelect );
		}
	}

	@Override
	public Filter getFilter() {
		return new ItemFilter();
	}
	
	class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
        	FilterResults results = new FilterResults();
        	results.values = listDatas;
            results.count = listDatas.size();
            
        	if ( constraint == null ) {
        		return results;
        	}
        	
        	String filterString = constraint.toString().toLowerCase();
        	if ( filterString.length() <= 0) {
        		return results;
        	}
        	
        	ArrayList<ContactData> nlist = new ArrayList<ContactData>();
            for (int i = 0; i < listDatas.size(); i++) {
            	final ContactData item = listDatas.get(i);
                
                if (item.getDisplayName().toLowerCase().contains(filterString) 	||   
                	item.getPhoneNumbers().toLowerCase().contains(filterString) || 
                	item.getEmails().toLowerCase().contains(filterString)) {
                    nlist.add( item );
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (List<ContactData>) results.values;
            notifyDataSetChanged();
        }
    }



	/**
	 * 모든 Contact 을 표시할 수 있는 리스트 정보를 반환한다. ( 아이콘은 제외 )
	 * 
	 * @return
	 */
	public synchronized String getContactListDataALL() {
		StringBuilder ret = new StringBuilder("{\"CONTACTS\":[");
		
		long contact_id = 0;
		String display_name = "";
		
		int i, len = listDatas.size() - 1;
		if( len > 0 ) {
			for( i = 0; i < len; i++ ) {
				ContactData data = listDatas.get(i);
				contact_id 	 = data.getContactID();
				display_name = data.getDisplayName();
				
				ret.append( String.format( "{\"ID\":\"%s\",\"NAME\":\"%s\",\"HAS_ICON\":\"%s\"},", contact_id, display_name, data.getIcon()==null?"false":"true" ));
			}
			
			ContactData data = listDatas.get(i);
			contact_id 	 = data.getContactID();
			display_name = data.getDisplayName();
			
			ret.append( String.format( "{\"ID\":\"%s\",\"NAME\":\"%s\",\"HAS_ICON\":\"%s\"}", contact_id, display_name, data.getIcon()==null?"false":"true" ));
		}
		ret.append( "]}" );
		return ret.toString();
	}

	/*
	public synchronized void deleteCheckedItems() {
		List<ContactData> deleteDatas 	= new ArrayList<ContactData>();
		for( ContactData data : listDatas ) {
			if( data.isSelected()) {
				deleteDatas.add( data );
				data.delete();
			}
		}
		
		listDatas.removeAll( deleteDatas );
		filteredData.removeAll( deleteDatas );
		
		context.runOnUiThread( 
				new Runnable(){
					@Override
					public void run() {
						setAllChecked( false );
						setShowCheckBox( false );					
					}
				}
			);
	}
	*/

	public void backupCheckedItems(String backupName ) {
		if( backupName.length() < 1 ) {
			Toast.makeText( context, context.getString(R.string.contacts_enter_backup_name), Toast.LENGTH_LONG ).show();
			return;
		}

		File baseFolder 	= new File( Environment.getExternalStorageDirectory(), "BluetoothShare" );
		File backupFolder 	= new File( baseFolder, "BackupContacts" );
		if( !backupFolder.exists()) {
			backupFolder.mkdirs();
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( context );

		File backup_file = new File( sharedPref.getString( "contacts_backup_path",  backupFolder.getAbsolutePath()), backupName + ".vcf" );
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream( backup_file );

			int count = 0;
			for( ContactData data : listDatas ) {
				if( data.isSelected()) {
					fos.write( data.readVCardString().getBytes() );
					++ count;
				}
			}
			
			String backup_result = context.getString( R.string.contacts_backup_result );
			backup_result = backup_result.replace( "xxxxx", String.valueOf(count));
			backup_result = backup_result.replace( "yyyyy", backupName );
			Toast.makeText( context, backup_result, Toast.LENGTH_LONG ).show();
			
			context.runOnUiThread( 
				new Runnable(){
					@Override
					public void run() {
						setAllSelected( false );
						setSelectMode( false );
					}
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText( context, String.format("'%s' Error", e.getMessage()), Toast.LENGTH_LONG ).show();
			
		} finally {
			if( fos != null ) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
	}
}

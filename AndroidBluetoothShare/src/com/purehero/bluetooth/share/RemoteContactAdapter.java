package com.purehero.bluetooth.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.common.G;
import com.purehero.common.Utils;
import com.purehero.contact.ContactAdapter;
import com.purehero.contact.ContactData;

public class RemoteContactAdapter extends BaseAdapter implements Filterable {
	
	private static final byte OPCODE_MASK_REQUEST 	= (byte) 0x10;
	private static final byte OPCODE_MASK_RESPONSE 	= (byte) 0x20;
	
	private static final byte OPCODE_CONTACT_LIST	= (byte) 0x01;
	private static final byte OPCODE_CONTACT_ICON	= (byte) 0x02;
	
	private static final byte OPCODE_REQUEST_CONTACT_LIST 	= OPCODE_MASK_REQUEST | OPCODE_CONTACT_LIST;
	private static final byte OPCODE_RESPONSE_CONTACT_LIST	= OPCODE_MASK_RESPONSE | OPCODE_CONTACT_LIST;
	private static final byte OPCODE_REQUEST_CONTACT_ICON 	= OPCODE_MASK_REQUEST | OPCODE_CONTACT_ICON;
	private static final byte OPCODE_RESPONSE_CONTACT_ICON	= OPCODE_MASK_RESPONSE | OPCODE_CONTACT_ICON;
	
	private final Activity context;
	private List<ContactData> listDatas = new ArrayList<ContactData>();
	private List<ContactData> filteredData = new ArrayList<ContactData>();
	
	private BluetoothCommunication btComm = null;
	private ContactAdapter contactAdapter = null;
	
	public RemoteContactAdapter( Activity context ) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return filteredData.size();
	}

	@Override
	public Object getItem(int index) {
		return filteredData.get(index);
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
			convertView  = inflater.inflate( R.layout.remote_contact_list_cell, null );
			
			viewHolder.icon	= (ImageView) convertView.findViewById( R.id.contact_icon );
			viewHolder.name	= (TextView)  convertView.findViewById( R.id.contact_name );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		ContactData data = ( ContactData ) getItem( position );
		if( data != null ) {
			Drawable icon = data.getIcon();
			if( icon == null ) {
				viewHolder.icon.setImageResource( R.drawable.ic_contact );
			} else {
				viewHolder.icon.setImageDrawable( icon );				
			}
			
			viewHolder.name.setVisibility( View.VISIBLE );
			viewHolder.name.setText( data.getDisplayName());
			
		} else {
			viewHolder.icon.setVisibility( View.INVISIBLE );
			viewHolder.name.setText( "" );
		}
		
		return convertView;
	}
	
	class ViewHolder {
		public ImageView icon;
		public TextView name;		
	}

	private boolean showCheckBox = false;
	public boolean isShowCheckBox() {
		return showCheckBox;
	}
	public void setShowCheckBox( boolean show ) {
		showCheckBox = show;
		notifyDataSetChanged();
	}
	
	public int getCheckedCount() {
		int ret = 0;
		for( ContactData data : listDatas ) {
			if( data.isSelected()) ++ret;
		}
		return ret;
	}
	
	public void setAllChecked( boolean checked ) {
		for( ContactData data : listDatas ) {
			data.setSelected( checked );
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

	final byte DEF_MAGIC_VALUE [] = { (byte)0x81, (byte)0x92, (byte)0x29, (byte)0x18 };
	public void dataReceived( byte[] data, int data_length ) {
		G.Log( "dataReceived %dbytes", data_length );
		G.Log( "%s", Utils.byteArrayToHexString( data, 16 ));
		
		byte temp_value [] = new byte[4];
		System.arraycopy( data, 0, temp_value, 0, temp_value.length );
		if( Arrays.equals( temp_value, DEF_MAGIC_VALUE )) {
			G.Log( "command" );
			
			int index = temp_value.length;
			// 새로운 명령어로 인식한다.
			received_op_code = data[index++];
			G.Log( "received_op_code 0x%x", received_op_code );
			
			System.arraycopy( data, index, temp_value, 0, temp_value.length );
			index += temp_value.length;
			
			remnant_size = Utils.byteToInt( temp_value );
			G.Log( "remnant_size = %d", remnant_size );
			
			if( remnant_size > 0 ) {
				dataReceivedStream.write( data, index, data_length - index );
				remnant_size -= ( data_length - index );
			}
			G.Log( "data_length = %d", data_length );
			G.Log( "index = %d", index );
			G.Log( "remnant_size -= ( data_length - index ) = %d", remnant_size );
			
		} else {
			G.Log( "division" );
			
			dataReceivedStream.write( data, 0, data_length );
			remnant_size -= data_length;
		}
		
		if( remnant_size <= 0 ) {
			G.Log( "received_op_code : 0x%x", received_op_code );
			byte received_datas [] = dataReceivedStream.toByteArray();
			int  data_offset = 0;
			
			if(( OPCODE_MASK_REQUEST & received_op_code ) > 0 ) {
				switch( received_op_code ) {
				case OPCODE_REQUEST_CONTACT_LIST :
					G.Log( "OPCODE_REQUEST_CONTACT_LIST" );
					responseContactList();
					break;
				case OPCODE_REQUEST_CONTACT_ICON :
					G.Log( "OPCODE_REQUEST_CONTACT_ICON" );
					byte contact_id_bytes [] = new byte[ Long.SIZE / 8 ];
					System.arraycopy( received_datas, data_offset, contact_id_bytes, 0, contact_id_bytes.length );
					long contact_id = Utils.byteToLong( contact_id_bytes );
					data_offset += contact_id_bytes.length;
					
					responseContactIcon( contact_id );
					break;
				}
				
			} else {
				switch( received_op_code ) {
				case OPCODE_RESPONSE_CONTACT_LIST :
					G.Log( "OPCODE_RESPONSE_CONTACT_LIST" );
					addContactDataFromRemoteJsonString( received_datas );
					break;
				case OPCODE_RESPONSE_CONTACT_ICON :
					G.Log( "OPCODE_RESPONSE_CONTACT_ICON" );
					insertContactIcon( received_datas );
					break;
				}
			}
			
			dataReceivedStream.reset();
			received_op_code = 0;
		}
	}

	private void insertContactIcon(byte[] received_datas) {
		G.Log( "insertContactIcon" );
		G.Log( "%s", Utils.byteArrayToHexString( received_datas, 16 ));
		
		byte contact_id_bytes [] = new byte[ Long.SIZE / 8 ];
		System.arraycopy( received_datas, 0, contact_id_bytes, 0, contact_id_bytes.length );
		long contact_id = Utils.byteToLong( contact_id_bytes );
		
		InputStream is = Utils.byteArrayToInputStream(received_datas, contact_id_bytes.length, received_datas.length-contact_id_bytes.length);
		try {
			G.Log( "inputStream : %d", contact_id );
			Drawable icon = Drawable.createFromStream( is, "icon");
			if( icon != null ) {
				G.Log( "get icon" );
				
				for( ContactData data : listDatas ) {
					if( data.getContactID() == contact_id ) {
						data.setIcon(icon);
						G.Log( "set icon" );
						
						context.runOnUiThread( new Runnable(){
							@Override
							public void run() {
								notifyDataSetChanged();
							}}
						);
						break;
					}
				}
			}					
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addContactDataFromRemoteJsonString( byte [] received_datas ) {
		G.Log( "addContactDataFromRemoteJsonString" );
		
		String json_string = new String( received_datas );
		
		listDatas.clear();
		filteredData.clear();
		
		List<ContactData> request_icon_data = new ArrayList<ContactData>();
		
		try {
			JSONObject jobj = new JSONObject( json_string );
			JSONArray array = jobj.getJSONArray( "CONTACTS" );
			if( array != null ) {
				for (int i = 0; i < array.length(); i++) {
				    JSONObject jsonobject = array.getJSONObject(i);
				    
				    long id 		= Long.valueOf( jsonobject.getString("ID"));
				    String name 	= jsonobject.getString("NAME");
				    boolean hasIcon = Boolean.valueOf( jsonobject.getString("HAS_ICON"));
				    
				    ContactData data = new ContactData( context, id, name );
				    //G.Log( data.toString() ); 
				    
				    listDatas.add( data );
				    filteredData.add( data );
				    if( hasIcon ) {
				    	request_icon_data.add( data );
				    }
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		context.runOnUiThread( new Runnable(){
			@Override
			public void run() {
				notifyDataSetChanged();
			}});
		
		if( request_icon_data.size() > 0 ) {
			new ReqeustIconDataThread( request_icon_data ).start();
		}
	}

	class ReqeustIconDataThread extends Thread implements Runnable {
		final List<ContactData> datas;
		public ReqeustIconDataThread( List<ContactData> datas ) {
			this.datas = datas;
		}
		@Override
		public void run() {
			G.Log( "ReqeustIconDataThread Start" );
			
			for( ContactData data : datas ) {
				requestContactIcon( data );
				try {
					Thread.sleep( 100 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
			
			G.Log( "ReqeustIconDataThread End" );
		}
	};
	
	byte received_op_code = -1;
	int remnant_size = 0;
	ByteArrayOutputStream dataReceivedStream = new ByteArrayOutputStream();
	
	public void disconnected() {
		btComm = null;
	}

	public void connected( BluetoothCommunication btComm, ContactAdapter adapter ) {
		this.btComm = btComm;		
		contactAdapter = adapter;
	}
	
	private void requestRemoteDevice( byte op_code, byte [] data ) {
		if( btComm != null && btComm.isConnected()) {
			G.Log( "requestRemoteDevice" );
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
			try {
				outputStream.write( DEF_MAGIC_VALUE );
				
				byte op_code_bytes [] = { op_code };
				outputStream.write( op_code_bytes );
				
				byte data_size [] = Utils.intTobyte( data == null ? 0 : data.length );
				outputStream.write( data_size );
				
				if( data != null && data.length > 0 ) {
					outputStream.write( data );
				}
				
				btComm.write( outputStream.toByteArray() );
				btComm.flush();
				
			} catch( Exception e ) {
				e.printStackTrace();
				
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void responseRemoteDevice( byte op_code,  byte [] data ) {
		if( btComm == null ) return;
		if( !btComm.isConnected()) return;
		
		G.Log( "responseRemoteDevice : 0x%x %d bytes", op_code, data.length );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write( DEF_MAGIC_VALUE );
			
			byte op_code_bytes [] = { op_code };
			outputStream.write( op_code_bytes );
			
			byte data_size [] = Utils.intTobyte( data == null ? 0 : data.length );
			outputStream.write( data_size );
			
			if( data != null && data.length > 0 ) {
				outputStream.write( data );
			}
			
			btComm.write( outputStream.toByteArray() );
			btComm.flush();
			
		} catch( Exception e ) {
			e.printStackTrace();
			
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void requestContactList() {
		G.Log( "requestContactList" );
		requestRemoteDevice( OPCODE_REQUEST_CONTACT_LIST, null );
	}
	public void requestContactIcon( ContactData data ) {
		G.Log( "requestContactIcon" );
		G.Log( data.toString() );
		requestRemoteDevice( OPCODE_REQUEST_CONTACT_ICON, Utils.longTobyte( data.getContactID() ));			
	}
	
	private void responseContactIcon( long contact_id ) {
		G.Log( "responseContactIcon %d", contact_id );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write( Utils.longTobyte( contact_id ));
			outputStream.write( contactAdapter.getIconBytes( contact_id ));
			
			responseRemoteDevice( OPCODE_RESPONSE_CONTACT_ICON, outputStream.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void responseContactList() {
		G.Log( "responseContactIcon" );
		
		String contactDatas = contactAdapter.getContactListDataALL();
		byte [] contact_bytes = contactDatas.getBytes();
		
		responseRemoteDevice( OPCODE_RESPONSE_CONTACT_LIST, contact_bytes );
	}
}

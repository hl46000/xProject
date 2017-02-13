package com.purehero.bluetooth.contact_share;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.purehero.bluetooth.BluetoothCommunication;
import com.purehero.common.G;
import com.purehero.common.Utils;
import com.purehero.contact.ContactAdapter;
import com.purehero.contact.ContactData;
import com.purehero.contact.ContactUtils;

public class RemoteContactAdapter extends BaseAdapter implements Filterable, OnCheckedChangeListener {
	
	private static final byte OPCODE_MASK_REQUEST 	= (byte) 0x10;
	private static final byte OPCODE_MASK_RESPONSE 	= (byte) 0x20;
	
	private static final byte OPCODE_CONTACT_LIST	= (byte) 0x01;
	private static final byte OPCODE_CONTACT_ICON	= (byte) 0x02;
	private static final byte OPCODE_CONTACT_DATAS	= (byte) 0x03;
	
	private static final byte OPCODE_REQUEST_CONTACT_LIST 	= OPCODE_MASK_REQUEST | OPCODE_CONTACT_LIST;
	private static final byte OPCODE_RESPONSE_CONTACT_LIST	= OPCODE_MASK_RESPONSE | OPCODE_CONTACT_LIST;
	private static final byte OPCODE_REQUEST_CONTACT_ICON 	= OPCODE_MASK_REQUEST | OPCODE_CONTACT_ICON;
	private static final byte OPCODE_RESPONSE_CONTACT_ICON	= OPCODE_MASK_RESPONSE | OPCODE_CONTACT_ICON;
	private static final byte OPCODE_REQUEST_CONTACT_DATAS 	= OPCODE_MASK_REQUEST | OPCODE_CONTACT_DATAS;
	private static final byte OPCODE_RESPONSE_CONTACT_DATAS	= OPCODE_MASK_RESPONSE | OPCODE_CONTACT_DATAS;
	
	private final MainActivity context;
	private List<ContactData> listDatas = new ArrayList<ContactData>();
	private List<ContactData> filteredData = new ArrayList<ContactData>();
	
	//private BluetoothCommunication btComm 	= null;
	private ContactAdapter contactAdapter 	= null;
	private RemoteContactComm remoteComm	= new RemoteContactComm();
	
	public RemoteContactAdapter( MainActivity context ) {
		this.context = context;		
	}
	
	@Override
	public synchronized int getCount() {
		return filteredData.size();
	}

	@Override
	public synchronized Object getItem(int index) {
		return filteredData.get(index);
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
			convertView  = inflater.inflate( R.layout.remote_contact_list_cell, null );
			
			viewHolder.checkBox	= (CheckBox)  convertView.findViewById( R.id.check_box );
			viewHolder.icon		= (ImageView) convertView.findViewById( R.id.contact_icon );
			viewHolder.name		= (TextView)  convertView.findViewById( R.id.contact_name );

			viewHolder.checkBox.setOnCheckedChangeListener( this );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		viewHolder.checkBox.setVisibility( showCheckBox ? View.VISIBLE : View.GONE );
		viewHolder.checkBox.setId( position );
		
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
			viewHolder.checkBox.setChecked( data.isSelected() );
		} else {
			viewHolder.icon.setVisibility( View.INVISIBLE );
			viewHolder.name.setText( "" );
			viewHolder.checkBox.setChecked( false );
		}
		
		return convertView;
	}
	
	class ViewHolder {
		public CheckBox checkBox;
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
	
	public synchronized int getCheckedCount() {
		int ret = 0;
		for( ContactData data : listDatas ) {
			if( data.isSelected()) ++ret;
		}
		return ret;
	}
	
	public synchronized void setAllChecked( boolean checked ) {
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

	
	public void dataReceived( byte[] data, int data_length ) {
		G.Log( "dataReceived %dbytes", data_length );
		G.Log( "%s", Utils.byteArrayToHexString( data, 16 ));
		
		byte temp_value [] = new byte[4];		
		if( remoteComm.isCommandData( data )) {
			// MAGIC_VALUE 로 시작하는 Packet 은 명령어를 포함하고 있다고 판단한다.  
			G.Log( "command" );
			
			int index = temp_value.length;
			// 명령어 코드를 판단한다. 
			received_op_code = data[index++];
			G.Log( "received_op_code 0x%x", received_op_code );
						
			System.arraycopy( data, index, temp_value, 0, temp_value.length );
			index += temp_value.length;
			
			// 명령어의 파라메터로 전달한 데이터를 추출한다. 
			remnant_size = Utils.byteToInt( temp_value );
			G.Log( "remnant_size = %d", remnant_size );
			
			if( remnant_size > 0 ) {
				dataReceivedStream.write( data, index, data_length - index );
				remnant_size -= ( data_length - index );
			}
			//G.Log( "data_length = %d", data_length );
			//G.Log( "index = %d", index );
			//G.Log( "remnant_size -= ( data_length - index ) = %d", remnant_size );
			
		} else {
			// 분할 Packet 으로 인식
			// G.Log( "division" );
			dataReceivedStream.write( data, 0, data_length );
			remnant_size -= data_length;
		}
		
		if( remnant_size <= 0 ) {
			// 전달한 데이터를 모두 수신한 경우, 수신한 명령을 수행한다.  
			
			byte received_datas [] = dataReceivedStream.toByteArray();
			int  data_offset = 0;
			
			G.Log( "received_op_code : 0x%x %dbytes", received_op_code, received_datas.length );
			
			byte contact_id_bytes [] = new byte[ Long.SIZE / 8 ];
			long contact_id = 0;
			List<Long> contactIDs = new ArrayList<Long>();
			
			if(( OPCODE_MASK_REQUEST & received_op_code ) > 0 ) {
				// 데이터 요청 명령어
				switch( received_op_code ) {
				case OPCODE_REQUEST_CONTACT_LIST :
					G.Log( "REQUEST_CONTACT_LIST" );	// 연락처 전체를 요청
					sendResponseContactList();
					break;
					
				case OPCODE_REQUEST_CONTACT_ICON :
					G.Log( "REQUEST_CONTACT_ICON" );	// 특정 연락처의 아이콘 데이터 요청
					while( data_offset < received_datas.length ) {
						System.arraycopy( received_datas, data_offset, contact_id_bytes, 0, contact_id_bytes.length );
						contact_id = Utils.byteToLong( contact_id_bytes );
						data_offset += contact_id_bytes.length;
					
						contactIDs.add( contact_id );
					}
					
					sendResponseContactIcon( contactIDs );
					break;
					
				case OPCODE_REQUEST_CONTACT_DATAS :			// 특정 연락처들의 연락처 데이터 요청
					G.Log( "REQUEST_CONTACT_DATAS" );
					
					while( data_offset < received_datas.length ) {
						System.arraycopy( received_datas, data_offset, contact_id_bytes, 0, contact_id_bytes.length );
						contact_id = Utils.byteToLong( contact_id_bytes );
						data_offset += contact_id_bytes.length;
					
						contactIDs.add( contact_id );
					}
					
					sendResponseContactDatas( contactIDs );
					break;
				}
				 
				
			} else {
								
				switch( received_op_code ) {
				case OPCODE_RESPONSE_CONTACT_LIST :
					G.Log( "RESPONSE_CONTACT_LIST" );
					processContactListData( received_datas );
					break;
					
				case OPCODE_RESPONSE_CONTACT_ICON :
					G.Log( "RESPONSE_CONTACT_ICON" );
					processContactIconData( received_datas );
					break;
					
				case OPCODE_RESPONSE_CONTACT_DATAS :
					G.Log( "RESPONSE_CONTACT_DATAS" );
					processContactsData( received_datas );
					break;
				}
			}
			
			dataReceivedStream.reset();
			received_op_code = 0;
			
			remoteComm.setEnableRequest( true );	// 원격으로부터 들어온 요청에 대한 응답을 모두 보냈으니 송신을 허용한다.
		}
	}

	/**
	 * 응답으로 받은 연락처 데이터를 내 단말기의 연락처에 추가한다.
	 * 
	 * @param received_datas
	 */
	private synchronized void processContactsData(byte[] received_datas) {
		G.Log( "processContactsData" );
		
		File vcf_file = null;
		FileOutputStream fos = null;
		try {
			File backup_folder = new File( context.getString( R.string.backup_folder) );
			File tmp_folder = new File( backup_folder, "tmp");
			if( !tmp_folder.exists()) {
				tmp_folder.mkdirs();
			}
			vcf_file = new File( tmp_folder, "Received Contacts" );
			
			fos = new FileOutputStream( vcf_file );
			fos.write( received_datas );
			fos.close();
			fos = null;
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setDataAndType(Uri.fromFile( vcf_file ), "text/x-vcard");
	        context.startActivityForResult( intent, 100 );
	        
		} catch ( Exception e) {
			e.printStackTrace();
			
		} finally {
			if( fos != null ) {
				try { fos.close(); } catch (IOException e) {}
			}			
		}
	}

	public void deleteCacheFiles() {
		G.Log( "deleteCacheFiles" );
		File backup_folder = new File( context.getString( R.string.backup_folder) );
		File tmp_folder = new File( backup_folder, "tmp");
		if( tmp_folder.exists()) {
			File fileList [] = tmp_folder.listFiles();
			for( File file : fileList ) {
				file.delete();
			}
		}
	}
	
	/**
	 * 응답으로 받은 연락처의 아이콘 데이터를 ContactData 객체에 추가한다.  
	 * 
	 * @param received_datas
	 */
	private synchronized void processContactIconData(byte[] received_datas) {
		G.Log( "insertContactIcon" );
		G.Log( "%s", Utils.byteArrayToHexString( received_datas, 16 ));
		
		int offset = 0;
		
		byte contact_id_bytes [] = new byte[ Long.SIZE / 8 ];
		byte icon_size_bytes [] = new byte[ Integer.SIZE / 8 ];
		
		while( offset < received_datas.length ) {
			System.arraycopy( received_datas, offset, contact_id_bytes, 0, contact_id_bytes.length );
			offset += contact_id_bytes.length;
			
			long contact_id = Utils.byteToLong( contact_id_bytes );
			
			
			System.arraycopy( received_datas, offset, icon_size_bytes, 0, icon_size_bytes.length );
			offset += icon_size_bytes.length;
			
			int icon_size = Utils.byteToInt( icon_size_bytes );
			
			InputStream is = Utils.byteArrayToInputStream(received_datas, offset, icon_size );
			offset += icon_size;
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
		
		
	}

	private synchronized void processContactListData( byte [] received_datas ) {
		G.Log( "addContactDataFromRemoteJsonString" );
		
		String json_string = new String( received_datas );
		
		listDatas.clear();
		filteredData.clear();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JSONObject jobj = new JSONObject( json_string );
			JSONArray array = jobj.getJSONArray( "CONTACTS" );
			if( array != null ) {
				for (int i = 0; i < array.length(); i++) {
				    JSONObject jsonobject = array.getJSONObject(i);
				    
				    long id 		= Long.valueOf( jsonobject.getString("ID"));
				    String name 	= jsonobject.getString("NAME");
				    boolean hasIcon = jsonobject.getString("HAS_ICON").compareTo("true") == 0;
				    
				    ContactData data = new ContactData( context, id, name );
				    //G.Log( data.toString() ); 
				    
				    listDatas.add( data );
				    filteredData.add( data );
				    if( hasIcon ) {
				    	try {
							baos.write( Utils.longTobyte( id ));
						} catch (IOException e) {
							e.printStackTrace();
						}
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
		
		remoteComm.setEnableRequest( true );
		sendRequestContactIcon( baos.toByteArray());
		try {
			baos.close();
		} catch (IOException e) {
		}
	}

	byte received_op_code = -1;
	int remnant_size = 0;
	ByteArrayOutputStream dataReceivedStream = new ByteArrayOutputStream();
	
	public synchronized void disconnected() {
		remoteComm.setBluetoothCommunication( null );
	}

	public synchronized boolean isConnected() {
		return remoteComm.isConnected();
	}
	public synchronized void connected( BluetoothCommunication btComm, ContactAdapter adapter ) {
		remoteComm.setBluetoothCommunication( btComm );
		contactAdapter = adapter;
	}
	
	public synchronized void sendRequestContactList() {
		G.Log( "sendRequestContactList" );
		remoteComm.sendRequestRemoteDevice( OPCODE_REQUEST_CONTACT_LIST, null );
	}
	
	/**
	 * 원격 단말에서 contact_id 에 해당하는 ICON 데이터를 요청한다. 
	 * 
	 * @param bs
	 */
	public synchronized void sendRequestContactIcon( byte[] contact_id_bytes ) {
		G.Log( "sendRequestContactIcon %dbytes", contact_id_bytes.length );
		remoteComm.sendRequestRemoteDevice( OPCODE_REQUEST_CONTACT_ICON, contact_id_bytes );			
	}
	
	/**
	 * 리스트에서 선택된 연락처에 대한 정보를 원격단말기에 요청한다. 
	 * <br> 원격 단말기로부터 응답을 받으면 해당 연락처의 정보를 내 단말기의 연락처에 추가 한다. 
	 */
	public synchronized void sendRequestContactDatas() {
		G.Log( "sendRequestContactDatas" );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			for( ContactData data : listDatas ) {
				if( data.isSelected()) {
					outputStream.write( Utils.longTobyte( data.getContactID() ) );
				}
			}
			remoteComm.sendRequestRemoteDevice( OPCODE_REQUEST_CONTACT_DATAS, outputStream.toByteArray() );
			
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

	public synchronized void sendDeleteContacts() {
		G.Log( "sendDeleteContacts" );
		
	}
	
	
	/**
	 * 연락처 아이콘 데이터 요청에 대한 응답 데이터를 보낸다.<br>
	 * 요청한 데이터를 전부 보낸다. <br>
	 * 보내는 데이터의 구조는 CONTACT_ID(long), ICON_DATA_SIZE(int), ICON_DATA(byte array) 이다. 
	 * 
	 * @param contact_ids
	 */
	private synchronized void sendResponseContactIcon( List<Long> contact_ids ) {
		G.Log( "sendResponseContactIcon contact count : %d", contact_ids.size() );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			for( long contact_id : contact_ids ) {
				outputStream.write( Utils.longTobyte( contact_id ));
				byte icon_bytes [] = ContactUtils.getIconBytes( context, contact_id );
				outputStream.write( Utils.intTobyte( icon_bytes.length ));
				outputStream.write( icon_bytes );
			}
			remoteComm.sendResponseRemoteDevice( OPCODE_RESPONSE_CONTACT_ICON, outputStream.toByteArray());
			
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
	
	/**
	 * 연락처 리스트 요청에 대한 응답 데이터를 보낸다.<br>
	 * 응답 데이터에는 리스트에 필요한 'CONTACT_ID','DISPLAY_NAME','HAS_ICON' 등이 포함된다.  
	 */
	private synchronized void sendResponseContactList() {
		G.Log( "sendResponseContactList" );
		
		String contactDatas = contactAdapter.getContactListDataALL();
		byte [] contact_bytes = contactDatas.getBytes();
		
		remoteComm.sendResponseRemoteDevice( OPCODE_RESPONSE_CONTACT_LIST, contact_bytes );
	}
	
	/**
	 * 연락처 데이터 요청에 대한 응답 데이터를 보낸다. <br>
	 * 응답 데이터에는 수신 단말기의 연락처에 추가할 수 있도록 VCF 데이터를 보낸다.
	 *  
	 * @param contact_ids	
	 */
	public synchronized void sendResponseContactDatas(List<Long> contact_ids) {
		G.Log( "sendResponseContactDatas" );
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			for( long contact_id : contact_ids ) {
				ContactData data = contactAdapter.getItemByContactID( contact_id );
				outputStream.write( data.readVCardString().getBytes() );				
			}
			remoteComm.sendResponseRemoteDevice( OPCODE_RESPONSE_CONTACT_DATAS, outputStream.toByteArray() );
			
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

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		ContactData data = filteredData.get( arg0.getId());
		data.setSelected( arg1 );
	}
}

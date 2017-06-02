package com.purehero.contact;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import org.json.JSONException;

import com.purehero.common.OrderingByKoreanEnglishNumbuerSpecial;
import com.purehero.common.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactData {
	private static final String _ID 				= ContactsContract.Contacts._ID;
	private static final String DISPLAY_NAME 		= ContactsContract.Contacts.DISPLAY_NAME;
			
	private static final Uri PhoneCONTENT_URI 		= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	private static final String Phone_CONTACT_ID 	= ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
	private static final String NUMBER 				= ContactsContract.CommonDataKinds.Phone.NUMBER;
	
	private static final Uri EmailCONTENT_URI 		= ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	private static final String EmailCONTACT_ID 	= ContactsContract.CommonDataKinds.Email.CONTACT_ID;
	private static final String DATA 				= ContactsContract.CommonDataKinds.Email.DATA;
	
	private static final Uri AddressCONTENT_URI 	= ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
	private static final String AddressCONTACT_ID 	= ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
	private static final String ADDRESS				= ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
	
	private final ContentResolver contentResolver;
	private final long contact_id;
	private final String lookupKey;
	private final String display_name;
	private String phoneNumbers;
	private String emails;
	private String address;
	
	public ContactData( Context context, Cursor cursor ) throws JSONException {
		contentResolver = context.getContentResolver();
		contact_id 		= cursor.getLong(cursor.getColumnIndex( _ID ));
		lookupKey 		= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		display_name	= cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
		phoneNumbers 	= "";
		emails 			= "";
		address 		= "";
	}
	
	public ContactData( Context context, long id, String name ) {
		contentResolver = null;
		contact_id 		= id;
		lookupKey 		= "";
		display_name	= name;
		phoneNumbers 	= "";
		emails 			= "";
		address 		= "";
	}
	
	public void delete() {
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
		contentResolver.delete( uri, null, null );
	}
	
	private String readEmails() {
		if( contentResolver == null ) return "";
		
		StringBuilder ret = null;
		// Query and loop for every email of the contact
		Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID+ " = ?", new String[] { String.valueOf( contact_id )}, null);
		if( emailCursor.getCount() > 0 ) {
			ret = new StringBuilder(); 
			while (emailCursor.moveToNext()) {
				String emailAddress = emailCursor.getString(emailCursor.getColumnIndex(DATA));
				int emailType = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE ));
				switch( emailType ) {
				case ContactsContract.CommonDataKinds.Email.TYPE_HOME 	:
					ret.append( String.format( "\"HOME:%s\"", emailAddress)); 
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE :
					ret.append( String.format( "\"MOBILE:%s\"", emailAddress));
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_WORK :
					ret.append( String.format( "\"WORK:%s\"", emailAddress));
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_OTHER :
				default :
					ret.append( String.format( "\"OTHER:%s\"", emailAddress));
					break;
				}				
				if( !emailCursor.isLast()) {
					ret.append( "," );
				}
			}
		}
		emailCursor.close();
		return ret == null ? "" : ret.toString(); 
	}
	
	private String readPhoneNumbers() {
		if( contentResolver == null ) return "";
		
		StringBuilder ret = null;
		// Query and loop for every phone number of the contact
		Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { String.valueOf( contact_id )}, null);
		if( phoneCursor.getCount() > 0 ) {
			ret = new StringBuilder(); 
			while (phoneCursor.moveToNext()) {
				int phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
				switch (phoneType) {
                case Phone.TYPE_MOBILE:
                	ret.append( String.format( "\"MOBILE:%s\"", phoneNumber));
                	break;
                case Phone.TYPE_HOME:
                	ret.append( String.format( "\"HOME:%s\"", phoneNumber));
                    break;
                case Phone.TYPE_WORK:
                	ret.append( String.format( "\"WORK:%s\"", phoneNumber));
                    break;
                case Phone.TYPE_OTHER:
                default:
                	ret.append( String.format( "\"OTHER:%s\"", phoneNumber));
                    break;
				}
				
				if( !phoneCursor.isLast()) {
					ret.append( "," );
				}
			}
		}
		phoneCursor.close();
		return ret == null ? "" : ret.toString(); 
	}
	
	
	private String readAddress() {
		if( contentResolver == null ) return "";
		
		StringBuilder ret = null;
		// Query and loop for every phone number of the contact
		Cursor addressCursor = contentResolver.query(AddressCONTENT_URI, null, AddressCONTACT_ID + " = ?", new String[] { String.valueOf( contact_id )}, null);
		if( addressCursor.getCount() > 0 ) {
			ret = new StringBuilder(); 
			while (addressCursor.moveToNext()) {
				int phoneType = addressCursor.getInt(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				String phoneNumber = addressCursor.getString(addressCursor.getColumnIndex(ADDRESS));
				switch (phoneType) {
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                	ret.append( String.format( "\"HOME:%s\"", phoneNumber));
                	break;
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                	ret.append( String.format( "\"WORK:%s\"", phoneNumber));
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                default:
                	ret.append( String.format( "\"OTHER:%s\"", phoneNumber));
                    break;
				}
				
				if( !addressCursor.isLast()) {
					ret.append( "," );
				}
			}
		}
		addressCursor.close();
		return ret == null ? "" : ret.toString(); 
	}
	
	public String readVCardString() {
		if( contentResolver == null ) return "";
		
		StringBuilder ret = new StringBuilder();
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey );
        
        AssetFileDescriptor fd = null;
        FileInputStream fis = null;
    	try {    		
            fd = contentResolver.openAssetFileDescriptor(uri, "r");
            fis = fd.createInputStream();
            
            byte [] buffer = Utils.inputStreamToByteArray( fis );
            ret.append( new String( buffer ));
            
    	} catch( Exception e ) {
    		if( fis != null ) {
    			try {
					fis.close();
				} catch (IOException e1) {
				}
    		}
    		if( fd != null ) {
    			try {
					fd.close();
				} catch (IOException e1) {
				}
    		}
    	}
		return ret.toString();
	}
	
	@Override
	public String toString() {
		return String.format( 
				"[%d]%s : %s", 
				getContactID(),
				getDisplayName(),
				isSelected() ? "selected" : "");
	}


	private Drawable icon = null;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon2) {
		icon = icon2;
	}
	public boolean loadData( Context context ) {
		if( contentResolver == null ) return false;
		if( icon != null ) return false;
		
		phoneNumbers 	= readPhoneNumbers();
		emails 			= readEmails();
		address 		= readAddress();
		
		InputStream inputStream = null;
		try {
			inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contact_id)));
 
            if (inputStream != null) {
            	icon = Drawable.createFromStream( inputStream, "icon"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
        	if( inputStream != null ) {
        		try {
					inputStream.close();
				} catch (IOException e) {
				}
        	}
        }
		return true;
	}

	
	public long 	getContactID() 	 	{ return contact_id; }
	public String 	getDisplayName() 	{ return display_name; }
	public String 	getPhoneNumbers() 	{ return phoneNumbers; }
	public String 	getEmails() 		{ return emails; }
	public String	getAddress()		{ return address; }
	
	private boolean selected = false;
	public boolean isSelected() { return selected; }
	public void setSelected( boolean selected ) { this.selected = selected; }
	
	
	
	/**
	 * ContactData 의 list 을 정렬에 필요한 비교자
	 */
	public static final Comparator<ContactData> ALPHA_COMPARATOR = new Comparator<ContactData> () {
		@Override
		public int compare(ContactData arg0, ContactData arg1) {
			if( arg0 == arg1 ) return 0;
			if( arg0 == null ) return 1;
			if( arg1 == null ) return -1;
			
			try {
				return OrderingByKoreanEnglishNumbuerSpecial.compare( arg0.getDisplayName(), arg1.getDisplayName());
			} catch( Exception e ) {
				e.printStackTrace();
				return 1;
			}
		}
	};
}

package com.purehero.contact;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.webkit.MimeTypeMap;

import com.purehero.common.G;
import com.purehero.common.Utils;

public class ContactUtils {
	
	public static void contactToStringEx( Context context, Cursor contactCursor ) {
		G.Log();
		
		final ContentResolver contentResolver = context.getContentResolver();
		
		// Get the _ID value
        long mContactId = contactCursor.getLong(contactCursor.getColumnIndex( ContactsContract.Contacts._ID ));
        // Get the selected LOOKUP KEY
        String mContactKey = contactCursor.getString( contactCursor.getColumnIndex( ContactsContract.Contacts.LOOKUP_KEY ));
        // Create the contact's content Uri
        Uri mContactUri = Contacts.getLookupUri(mContactId, mContactKey);
    	
		// Defines the selection clause
	    String SELECTION = Data.LOOKUP_KEY + " = ?";
	    // Defines the array to hold the search criteria
	    String[] mSelectionArgs = { mContactKey };
		
	    // Defines a string that specifies a sort order of MIME type
	    //final String SORT_ORDER = Data.MIMETYPE;
	    
	    //mContactUri = ContactsContract.Contacts.CONTENT_URI;
	    //SELECTION = "contact_id = ?";
	    //mSelectionArgs = new String[] { String.valueOf( mContactId )};
	    
	    Cursor detailCursor = contentResolver.query( mContactUri, null, SELECTION, mSelectionArgs, null );
	    G.Log( "detailCursor.getCount() : %d", detailCursor.getCount() );
	    		
	    if( detailCursor.getCount() > 0 ) {
	    	String columnNames [] = detailCursor.getColumnNames();
	    	String value = null;
	    	while( detailCursor.moveToNext()) {
	    		for( int i = 0; i < columnNames.length; i++ ) {
	    			switch( detailCursor.getType(i) ) {
	    			case Cursor.FIELD_TYPE_INTEGER 	: value = String.valueOf( detailCursor.getInt(i)); break;
	    			case Cursor.FIELD_TYPE_STRING 	: value = detailCursor.getString(i); break;
	    			case Cursor.FIELD_TYPE_FLOAT	: value = String.valueOf( detailCursor.getFloat(i)); break;
	    			default : continue;
	    			}
	    			
	    			if( value != null ) {
	    				G.Log( "\t%s=%s", columnNames[i], value );
	    			}
	    		}
			}
	    	detailCursor.close();
	    }
	}
	
	/**
	 * 연락처의 ICON을 byte array 로 반환한다. 
	 * 
	 * @param context		
	 * @param contact_id	연락처 ID값
	 * @return				연락처 ICON의 Byte Array, 오류 발생 시 null 반환
	 */
	public static byte[] getIconBytes( Context context, long contact_id ) {
		InputStream is = null;
		try {
			is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contact_id)));
 
            byte is_bytes [] = Utils.inputStreamToByteArray( is ); 
			return is_bytes;
			
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
        	if( is != null ) {
        		try {
        			is.close();
				} catch (IOException e) {}
        	}
        }
		return null;
	}
	
	/**
	 * 연락처의 상세보기 화면을 띄워준다. 
	 * 
	 * @param context
	 * @param contact_id	연락처 ID값
	 */
	public static void openDetailView( Context context, long contact_id ) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf( contact_id ));
		intent.setData(uri);
		context.startActivity(intent);
	}
	
	public static void openVCard( Context context, File savedVCard ) {
        String vcfMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("vcf");
        Intent openVcfIntent = new Intent(Intent.ACTION_VIEW);
        openVcfIntent.setDataAndType(Uri.fromFile(savedVCard), vcfMimeType);
        context.startActivity(openVcfIntent);
	}
}

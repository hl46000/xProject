package com.purehero.contact;

import com.purehero.common.G;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

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
}

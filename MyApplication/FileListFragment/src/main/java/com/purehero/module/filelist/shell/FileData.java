package com.purehero.module.filelist.shell;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

import com.purehero.module.filelistfragment.R;

public class FileData {
	final int LINE_INDEX_PERMISSION = 0;
	final int LINE_INDEX_USER		= 1;
	final int LINE_INDEX_GROUP		= 2;
	final int LINE_INDEX_SIZE		= 3;
	final int LINE_INDEX_DATE		= 4;
	final int LINE_INDEX_TIME		= 5;
	final int LINE_INDEX_NAME 		= 6;
	final int LINE_INDEX_LINK		= 8;
	
	private final String parentPath;
	private final String permission;
	private final boolean isFile;
	private final String user;
	private final String group;
	private long fileSize;
	private final String date;
	private final String time;
	private final String name;
	public FileData( String parent, String line) {
		parentPath = parent;
		
		while( line.contains("  ")) {
			line = line.replace( "  ", " " );
		}

		Log.d( "MyLOG", line );
		String [] token = line.split(" ");
		user			= token[LINE_INDEX_USER];
		group			= token[LINE_INDEX_GROUP];
		permission		= token[LINE_INDEX_PERMISSION];
		isFile 		= permission.startsWith("-");

		if( isFile ) {
			fileSize		= Long.valueOf( token[LINE_INDEX_SIZE] );
			date 			= token[LINE_INDEX_DATE];
			time 			= token[LINE_INDEX_TIME];
			name 			= token[LINE_INDEX_NAME];
		} else {
			fileSize		= 0;
			date 			= token[LINE_INDEX_DATE-1];
			time 			= token[LINE_INDEX_TIME-1];
			name 			= token[LINE_INDEX_NAME-1];
		}
	}
	
	public void print() {
		Log.d( "MyLOG", "===================================================");
		Log.d( "MyLOG", String.format( "parentPath : %s", parentPath ));
		Log.d( "MyLOG", String.format( "name : %s", name ));
		Log.d( "MyLOG", String.format( "%s", isDirectory() ? "Folder" : "File" ));
		Log.d( "MyLOG", String.format( "Size : %d", fileSize ));
		Log.d( "MyLOG", String.format( "Date : %s %s", date, time ));
		
		Log.d( "MyLOG", "");
	}
	
	public String getParent() {
		return parentPath;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isDirectory() {
		return !isFile;
	}
	
	public boolean isFile() {
		return !isDirectory(); 
	}
	
	public boolean exists() {
		return true;
	}
	
	public long length() {
		return fileSize;
	}
	
	public String getAbsolutePath() {
		return parentPath + File.separator + name;
	}
	
	public boolean canRead() {
		String curUser = ShellFileListAdapter.USER;
		if( curUser.compareTo( user ) == 0 ) {
			if( permission.charAt(1) == 'r' ) {
				return true;
			}
		} else if( curUser.compareTo( group ) == 0 ) {
			if( permission.charAt(4) == 'r' ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canWrite() {
		String curUser = ShellFileListAdapter.USER;
		if( curUser.compareTo( user ) == 0 ) {
			if( permission.charAt(2) == 'w' ) {
				return true;
			}
		} else if( curUser.compareTo( group ) == 0 ) {
			if( permission.charAt(5) == 'w' ) {
				return true;
			}
		}
		return false;
	}
	
	SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault()); 
	public long lastModified() {
		try {
			Date _date = dataFormat.parse( String.format( "%s %s", date, time ) );
			return _date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String getDateString() {
		return date + " " + time;
	}

	public String getSizeString() {
		String result = "0 B";

		float size = fileSize;
		if( size < 1024.0f ) {
			result = String.format( "%d B", (int)size );
		} else {
			size /= 1024.0f;
			if( size < 1024.0f ) {
				result = String.format( "%.2f KB", size );
			} else {
				size /= 1024.0f;
				if( size < 1024.0f ) {
					result = String.format( "%.2f MB", size );
				} else {
					size /= 1024.0f;
					result = String.format( "%.2f GB", size );
				}
			}
		}

		return result;
	}

	public int getResourceIcon() {
		if( isDirectory()) return R.drawable.fl_ic_folder;
		return R.drawable.fl_ic_text;
	}

	public String getInfoString() {
		if( !isDirectory()) return getSizeString();
		return "";
	}

	public static final Comparator<FileData> ALPHA_COMPARATOR = new Comparator<FileData> () {
		@Override
		public int compare(FileData arg0, FileData arg1) {
			if( arg0.isDirectory() && !arg1.isDirectory() ) return -1;
			if( !arg0.isDirectory() && arg1.isDirectory() ) return  1;
			/*
			if( arg0.getClickCount() > arg1.getClickCount()) {
				return -1;
			} else if( arg0.getClickCount() < arg1.getClickCount() ) {
				return 1;
			}
			*/
			return arg0.getName().compareToIgnoreCase( arg1.getName());
		}
	};
}

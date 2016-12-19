package com.purehero.apk.manager;

import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * @author purehero
 * 
 * File 정보 
 * 
 */
public class FileListData {
	private Drawable icon 	= null;
	private final File file;
	private final String subTitle;
	private final String fileDate;
	
	private int index 		= -1;
	
	
	/**
	 * 생성자, 
	 * 
	 * @param info APK ResulveInfo 객체
	 * @param pm PackageManager 객체
	 */
	public FileListData( File file, Context context ) {
		this.file = file;
		
		if( file.isDirectory()) {
			subTitle = String.format( context.getResources().getString( R.string.file_list_sub_title_folder_format), file.listFiles().length );
		} else {
			float size = file.length();
			if( size < 1024.0f ) {
				subTitle = String.format( "%.2f B", size );
			} else {
				size /= 1024.0f; 
				if( size < 1024.0f ) {
					subTitle = String.format( "%.2f KB", size );
				} else {
					size /= 1024.0f; 
					if( size < 1024.0f ) {
						subTitle = String.format( "%.2f MB", size );
					} else {
						size /= 1024.0f; 
						subTitle = String.format( "%.2f GB", size );						
					}
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy H:mm a");
		fileDate = sdf.format(file.lastModified());
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * Icon 을 반환한다. 
	 * 
	 * @return
	 */
	public Drawable getIcon() {
		if( icon == null ) {
			// icon 을 불러 와야 하는데 어디서?
		}
		return icon;
	}
	
	/**
	 * filename 반환한다. 
	 * 
	 * @return
	 */
	public String getFilename() {
		return file.getName();
	}
	
	/**
	 * subTitle 을 반환한다. 
	 * 
	 * @return
	 */
	public String getSubTitle() {
		return subTitle;
	}
	
	public String getFileDate() {
		return fileDate;
	}
	
	/**
	 * @param value
	 */
	public void setIndex( int value ) {
		index = value;
	}
	
	/**
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * ApkListData 의 list 을 정렬에 필요한 비교자
	 */
	public static final Comparator<FileListData> ALPHA_COMPARATOR = new Comparator<FileListData> () 
	{
		private final Collator collator = Collator.getInstance();
		
		@Override
		public int compare(FileListData arg0, FileListData arg1) {
			return collator.compare( arg0.getFilename(), arg1.getFilename());
		}
	};
}

package com.purehero.apk.manager;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * @author purehero
 * 
 * 단말기에 설치된 APK의 정보 
 * 
 */
public class ApkListData {
	private Drawable icon 	= null;
	private final String appName;
	private final String packageName;
	private final String versionName;
	private final String apkPath;
	private File clickCountFile = null;
	private int clickCount;
	private int index 		= -1;
	
	/**
	 * 생성자, 
	 * 
	 * @param info APK ResulveInfo 객체
	 * @param pm PackageManager 객체
	 */
	public ApkListData( Context context, ResolveInfo info, PackageManager pm ) {
		packageName 	= info.activityInfo.packageName;
		appName 		= (String) info.loadLabel(pm);
		versionName		= null;
		apkPath			= info.activityInfo.applicationInfo.sourceDir;
		
		File base_folder 	= new File( context.getCacheDir(), "package" );
		File folder			= new File( base_folder, packageName );
		if( !folder.exists()) {
			folder.mkdirs();
		}
		clickCountFile	= new File( folder, "count" );
		File iconFile	= new File( folder, "icon" );
		try {
			icon 		= BitmapDrawable.createFromPath( Uri.fromFile( iconFile ).getPath() );
			if( icon == null ) {
				icon		= info.loadIcon(pm);
				
				if( icon != null ) {
					new SaveIconToFileThread( icon, iconFile ).start();					
					//new SaveIconToFileThread( icon, iconFile ).run();
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( clickCountFile.exists()) {
			try {
				clickCount = Integer.valueOf( G.readFile( clickCountFile ));
			} catch( Exception e ) {}
		}
		G.writeFile( new File( folder, "app_name"), appName );
	}
	
	public ApkListData(Context context, PackageInfo pi, PackageManager pm) {
		packageName 	= pi.packageName;
		appName 		= pi.applicationInfo.loadLabel(pm).toString();
		versionName		= pi.versionName;
		apkPath			= pi.applicationInfo.sourceDir;
		
		File base_folder 	= new File( context.getCacheDir(), "package" );
		File folder			= new File( base_folder, packageName );
		if( !folder.exists()) {
			folder.mkdirs();
		}
		clickCountFile	= new File( folder, "count" );
		File iconFile	= new File( folder, "icon" );
		try {
			icon 		= BitmapDrawable.createFromPath( Uri.fromFile( iconFile ).getPath() );
			if( icon == null ) {
				icon		= pi.applicationInfo.loadIcon(pm);
				
				if( icon != null ) {
					new SaveIconToFileThread( icon, iconFile ).start();					
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if( clickCountFile.exists()) {
			try {
				clickCount = Integer.valueOf( G.readFile( clickCountFile ));
			} catch( Exception e ) {}
		}
		G.writeFile( new File( folder, "app_name"), appName );
	}

	/**
	 * @author MY
	 * 불러온 아이콘 파일을 파일로 기록하는 Thread 을 생성한다. 
	 *
	 *
	 */
	class SaveIconToFileThread extends Thread implements Runnable {
		final Drawable drawable;
		final File file;
		
		public SaveIconToFileThread( Drawable drawable, File file ) {
			this.drawable = drawable;
			this.file = file;
		}
		
		@Override
		public void run() {
			if( G.saveBitmapToFile( G.drawableToBitmap( drawable ), file )) {
				//G.log( "Saved icon to file : " + file.getAbsolutePath());
			}
		}
	};
	
	/**
	 * Icon 을 반환한다. 
	 * 
	 * @return
	 */
	public Drawable getIcon() {
		return icon;
	}
	
	/**
	 * APK 의 앱이름을 반환한다. 
	 * 
	 * @return
	 */
	public String getAppName() {
		return appName;
	}
	
	/**
	 * APK의 PackageName 을 반환한다. 
	 * 
	 * @return
	 */
	public String getPackageName() {
		return packageName;
	}
	
	/**
	 * APK 파일의 경로를 반환한다.
	 * 
	 * @return
	 */
	public String getApkFilepath() {
		return apkPath;
	}
	
	public String getVersionName() {
		return versionName == null ? versionName : versionName.split("-")[0].trim();
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
	 * @param count
	 */
	public void setClickCount( int count ) {
		clickCount = count;
		
		G.writeFile( clickCountFile, String.valueOf( clickCount ));
	}
	
	/**
	 * @return
	 */
	public int getClickCount() {
		return clickCount;
	}
	
	/**
	 * ApkListData 의 list 을 정렬에 필요한 비교자
	 */
	public static final Comparator<ApkListData> ALPHA_COMPARATOR = new Comparator<ApkListData> () 
	{
		private final Collator collator = Collator.getInstance();
		
		@Override
		public int compare(ApkListData arg0, ApkListData arg1) {
			if( arg0.getClickCount() == arg1.getClickCount()) {
				return collator.compare( arg0.getAppName(), arg1.getAppName());
			} 
			return arg1.getClickCount() - arg0.getClickCount();
		}
	};

	
}

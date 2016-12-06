package com.purehero.prj01.androidmanager;

import java.text.Collator;
import java.util.Comparator;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * @author purehero
 * 
 * 단말기에 설치된 APK의 정보 
 * 
 */
public class ApkListData {
	private Drawable icon 	= null;
	private String appName	= null;
	private final String packageName;
	private int index 		= -1;
	
	private final ResolveInfo resolveInfo;
	private final PackageManager pm;
	
	/**
	 * 생성자, 
	 * 
	 * @param info APK ResulveInfo 객체
	 * @param pm PackageManager 객체
	 */
	public ApkListData( ResolveInfo info, PackageManager pm ) 
	{
		resolveInfo = info;
		this.pm = pm;
		packageName = info.activityInfo.packageName;
	}
	
	/**
	 * Icon 을 반환한다. 
	 * 
	 * @return
	 */
	public Drawable getIcon() 
	{
		if( icon == null ) {
			icon = resolveInfo.loadIcon(pm);
		}
		return icon;
	}
	
	/**
	 * APK 의 앱이름을 반환한다. 
	 * 
	 * @return
	 */
	public String getAppName() 
	{
		if( appName == null ) {
			appName = (String) resolveInfo.loadLabel(pm);
		}
		return appName;
	}
	
	/**
	 * APK의 PackageName 을 반환한다. 
	 * 
	 * @return
	 */
	public String getPackageName() 
	{
		return packageName;
	}
	
	/**
	 * APK의 Launcher Activity class 의 이름을 반환한다. 
	 * 
	 * @return
	 */
	public String getLauncherActivityName() {
		return resolveInfo.activityInfo.name;
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
	public static final Comparator<ApkListData> ALPHA_COMPARATOR = new Comparator<ApkListData> () 
	{
		private final Collator collator = Collator.getInstance();
		
		@Override
		public int compare(ApkListData arg0, ApkListData arg1) {
			return collator.compare( arg0.getAppName(), arg1.getAppName());
		}
	};
}

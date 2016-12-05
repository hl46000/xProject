package com.purehero.prj01.androidmanager;

import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

public class ApkListData {
	public Drawable icon;
	public String appName;
	public String packageName;

	public static final Comparator<ApkListData> ALPHA_COMPARATOR = new Comparator<ApkListData> () {
		private final Collator collator = Collator.getInstance();
		
		@Override
		public int compare(ApkListData arg0, ApkListData arg1) {
			return collator.compare( arg0.appName, arg1.appName );
		}
	};
}

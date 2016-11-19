package com.example.service;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Covault Service 유지를 위해 적용한 Notification tap시 Action을 위한 더미용 activity 클래스 입니다.  
 *
 */
public class AppSealingNoticeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Notification을 눌렀을 때 Pending intent로 출력되는 Activity를 바로 닫는다. 
		// Game의 마지막 activity로 이동한다.
		Log.d("AppSealingNoticeActivity", "onCreate");
		this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("AppSealingNoticeActivity", "onResume");
		
		Toast.makeText( this, "CovaultNoticeActivity::onResume", Toast.LENGTH_SHORT ).show();
	}
}
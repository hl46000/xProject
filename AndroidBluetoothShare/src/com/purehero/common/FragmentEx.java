package com.purehero.common;

import android.support.v4.app.Fragment;

public class FragmentEx extends Fragment {
	
	/**
	 * Fragment 에서 back key을 처리할때 사용한다.
	 * 
	 * @return
	 */
	public boolean onBackPressed() {
		return false;
	}
}

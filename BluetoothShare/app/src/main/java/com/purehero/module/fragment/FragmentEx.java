package com.purehero.module.fragment;

import android.support.v4.app.Fragment;
import android.view.Menu;

/**
 * Created by MY on 2017-02-25.
 */
public class FragmentEx extends Fragment implements OnBackPressedListener {

    public FragmentEx() {
    }

    /**
     * Fragment 에서 back key을 처리할때 사용한다.
     *
     * @return
     */
    @Override
    public boolean onBackPressed() {
        return false;
    }

    /**
     * Option 메뉴를 생성한다.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public void onPrePause() {}
}

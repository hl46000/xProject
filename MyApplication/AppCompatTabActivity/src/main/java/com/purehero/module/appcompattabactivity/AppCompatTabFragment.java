package com.purehero.module.appcompattabactivity;

import android.support.v4.app.Fragment;

/**
 * Created by MY on 2017-02-25.
 */
public class AppCompatTabFragment extends Fragment {

    public AppCompatTabFragment() {
    }

    /**
     * Fragment 에서 back key을 처리할때 사용한다.
     *
     * @return
     */
    public boolean onBackPressed() {
        return false;
    }
}

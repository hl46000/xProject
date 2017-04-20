package com.purehero.module.common;

import android.content.Context;

/**
 * Created by purehero on 2017-04-18.
 */

public class DipUtils {
    public static int dipToPixel(Context context, int dips ) {
        return ( int )( dips * context.getResources().getDisplayMetrics().density * 0.5f );
    }
}

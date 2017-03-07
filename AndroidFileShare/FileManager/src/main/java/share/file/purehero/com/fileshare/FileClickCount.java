package share.file.purehero.com.fileshare;

import android.content.Context;
import android.content.SharedPreferences;

import com.purehero.common.Cipher;
import com.purehero.common.G;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by purehero on 2017-03-07.
 */

public class FileClickCount {
    private static SharedPreferences prefs = null;
    private static Map<String,Integer> count_datas = new HashMap<String,Integer>();

    public static void saveClickCount( FileListData data ) {
        String hash = Cipher.MD5( data.getFile().getAbsolutePath());
        count_datas.remove( hash );
        count_datas.put( hash, data.getClickCount());

        G.Log( "saveClickCount %s=%d", hash, data.getClickCount());
    }

    public static void loadClickCount( FileListData data ) {
        if( count_datas.isEmpty()) return;

        String hash = Cipher.MD5( data.getFile().getAbsolutePath());
        if( count_datas.containsKey( hash )) {
            data.setClickCount( count_datas.get( hash ));
            G.Log( "loadClickCount %s=%d", hash, data.getClickCount());
        }
    }

    public static void loadDatas( Context context ) {
        prefs = context.getSharedPreferences( String.valueOf( "click_count".hashCode()), MODE_PRIVATE);
    }

    public static void saveDatas( Context context ) {
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> keys = count_datas.keySet();
        for( String key : keys ) {
            Integer value = count_datas.get(key);
            editor.putInt( key, value );
        }
        editor.commit();
    }
}

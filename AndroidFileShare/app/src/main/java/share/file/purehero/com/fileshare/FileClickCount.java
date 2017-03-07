package share.file.purehero.com.fileshare;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by purehero on 2017-03-07.
 */

public class FileClickCount {
    private static Map<Integer,Integer> count_datas = new HashMap<Integer,Integer>();
    public static void saveClickCount( FileListData data ) {
        int hash = data.getFile().getAbsolutePath().hashCode();
        if( count_datas.containsKey( hash )) {
            Integer value = count_datas.get( hash );
            value = data.getClickCount();
        } else {
            count_datas.put( hash, data.getClickCount());
        }
    }

    public static void loadClickCount( FileListData data ) {
        int hash = data.getFile().getAbsolutePath().hashCode();
        if( count_datas.containsKey( hash )) {
            data.setClickCount( count_datas.get( hash ));
        }
    }
}

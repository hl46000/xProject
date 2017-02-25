package share.file.purehero.com.fileshare;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListAdapter extends BaseAdapter {
    private List<FileListData> listData = new ArrayList<FileListData>();    // 전체 데이터
    private List<FileListData> filterData = new ArrayList<FileListData>();  // 검색이 적용된 데이터

    @Override
    public int getCount() {
        return filterData.size();
    }

    @Override
    public Object getItem(int i) {
        return filterData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}

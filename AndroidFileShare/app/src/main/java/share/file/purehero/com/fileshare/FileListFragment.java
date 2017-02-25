package share.file.purehero.com.fileshare;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.purehero.common.FragmentEx;
import com.purehero.common.G;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListFragment extends FragmentEx {
    private View layout = null;
    private ListView listView = null;
    private FileListAdapter listAdapter = new FileListAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        G.Log( "onCreateView" );

        layout 		= inflater.inflate( R.layout.file_list, container, false);
        if( layout == null ) return null;

        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            listView.setAdapter( listAdapter );
        }

        return layout;
    }
}

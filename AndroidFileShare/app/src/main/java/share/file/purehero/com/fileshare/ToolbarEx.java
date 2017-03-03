package share.file.purehero.com.fileshare;

import android.content.Context;
import android.support.v7.widget.Toolbar;

/**
 * Created by purehero on 2017-03-03.
 */

public class ToolbarEx extends Toolbar {
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    private int id = -1;
    public ToolbarEx(Context context) {
        super(context);
    }
}

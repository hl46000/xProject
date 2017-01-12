package com.purehero.qr.reader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentText extends Fragment{
    private String message = "Fragment content";
    
	public FragmentText(String message) {
		super();
		this.message = message;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		TextView text = new TextView(container.getContext());
        text.setText(message);
        text.setGravity(Gravity.CENTER);

        return text;
    }
}

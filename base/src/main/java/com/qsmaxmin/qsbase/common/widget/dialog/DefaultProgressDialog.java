package com.qsmaxmin.qsbase.common.widget.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qsmaxmin.qsbase.R;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/7/17 10:42
 * @Description
 */
public class DefaultProgressDialog extends QsProgressDialog {
    private TextView tv_progress_msg;

    @Override public View onCreateContentView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.qs_default_progress_dialog, parent, true);
        tv_progress_msg = view.findViewById(R.id.tv_progress_msg);
        return view;
    }

    @Override public void onSetMessage(CharSequence text) {
        tv_progress_msg.setText(text);
    }

    @Override public long getDelayedShowingTime() {
        return 300;
    }

    @Override public void onShowing() {
        super.onShowing();
    }

    @Override public void onHidden() {
        super.onHidden();
    }
}

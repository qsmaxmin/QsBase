package com.qsmaxmin.qsbase.common.widget.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qsmaxmin.qsbase.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/7/17 10:42
 * @Description
 */
public class DefaultProgressDialog extends QsProgressDialog {

    @Override protected int layoutId() {
        return R.layout.qs_default_progress_dialog;
    }

    @Override protected void initData() {
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv_progress_msg = view.findViewById(R.id.tv_progress_msg);
        tv_progress_msg.setText(getMessage());
    }

    @Override public void setMessage(CharSequence sequence) {
        super.setMessage(sequence);
        View view = getView();
        if (view != null) {
            TextView tv_progress_msg = view.findViewById(R.id.tv_progress_msg);
            tv_progress_msg.setText(getMessage());
        }
    }
}

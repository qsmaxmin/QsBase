package com.qsmaxmin.qsbase.common.viewbind;

import android.view.View;

final class ViewFinder {

    private View view;

    ViewFinder(View view) {
        this.view = view;
    }

    public View findViewById(int id) {
        if (view != null) return view.findViewById(id);
        return null;
    }
}

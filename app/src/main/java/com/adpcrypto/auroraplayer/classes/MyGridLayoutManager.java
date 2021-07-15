package com.adpcrypto.auroraplayer.classes;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

public class MyGridLayoutManager extends GridLayoutManager {

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}

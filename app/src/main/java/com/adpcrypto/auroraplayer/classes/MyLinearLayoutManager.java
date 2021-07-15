package com.adpcrypto.auroraplayer.classes;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class MyLinearLayoutManager extends StaggeredGridLayoutManager {


    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyLinearLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}


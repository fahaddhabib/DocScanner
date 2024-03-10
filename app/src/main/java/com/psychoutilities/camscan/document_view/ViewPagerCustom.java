package com.psychoutilities.camscan.document_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerCustom extends ViewPager {
    private boolean isPagingEnabled = true;

    public ViewPagerCustom(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.isPagingEnabled && super.onTouchEvent(motionEvent);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(motionEvent);
    }

    public ViewPagerCustom(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setPagingEnabled(boolean z) {
        this.isPagingEnabled = z;
    }
}

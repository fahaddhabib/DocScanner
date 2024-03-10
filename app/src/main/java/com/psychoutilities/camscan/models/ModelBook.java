package com.psychoutilities.camscan.models;

import android.graphics.Bitmap;

public class ModelBook {
    private Bitmap bitmap;
    private String page_name;
    private int pos;

    public ModelBook(Bitmap bitmap2, String str, int i) {
        this.bitmap = bitmap2;
        this.page_name = str;
        this.pos = i;
    }

    public String getPage_name() {
        return this.page_name;
    }

    public void setPage_name(String str) {
        this.page_name = str;
    }

    public int getPos() {
        return this.pos;
    }

    public void setPos(int i) {
        this.pos = i;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

}

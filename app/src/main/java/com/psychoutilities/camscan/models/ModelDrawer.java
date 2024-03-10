package com.psychoutilities.camscan.models;

public class ModelDrawer {

    private String item_name;

    private int item_icon;

    public ModelDrawer(String str, int i) {
        this.item_name = str;
        this.item_icon = i;
    }

    public String getItem_name() {
        return this.item_name;
    }

    public int getItem_icon() {
        return this.item_icon;
    }

    public void setItem_icon(int i) {
        this.item_icon = i;
    }

    public void setItem_name(String str) {
        this.item_name = str;
    }

}

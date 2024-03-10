package com.psychoutilities.camscan.scrapbook;

import android.graphics.Bitmap;

public class ConfigImageSticker implements ConfigInterfaceSticker {
    Bitmap bitmap;
    int stickerId;
    STICKERTYPE stickerType;

    public ConfigImageSticker(Bitmap bitmap2, STICKERTYPE sticker_type) {
        this.bitmap = bitmap2;
        this.stickerType = sticker_type;
    }

    @Override
    public STICKERTYPE getType() {
        return STICKERTYPE.IMAGE;
    }

    public Bitmap getBitmapImage() {
        return this.bitmap;
    }

    @Override
    public int getStickerId() {
        return this.stickerId;
    }

}

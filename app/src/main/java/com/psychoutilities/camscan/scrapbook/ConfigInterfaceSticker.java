package com.psychoutilities.camscan.scrapbook;

public interface ConfigInterfaceSticker {

    public enum STICKERTYPE {
        IMAGE,
        TEXT,
        STICKER
    }

    STICKERTYPE getType();

    int getStickerId();
}

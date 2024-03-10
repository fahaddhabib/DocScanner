package com.psychoutilities.camscan.qrcode_reader;

import android.hardware.Camera;

public class WrapperCamera {
    public final Camera mCamera;
    public final int mCameraId;

    public static WrapperCamera getWrapper(Camera camera, int i) {
        if (camera == null) {
            return null;
        }
        return new WrapperCamera(camera, i);
    }

    private WrapperCamera(Camera camera, int i) {
        if (camera != null) {
            this.mCamera = camera;
            this.mCameraId = i;
            return;
        }
        throw new NullPointerException("Camera cannot be null");
    }

}

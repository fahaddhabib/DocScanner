package com.psychoutilities.camscan.qrcode_reader;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class HandlerThreadCamera extends HandlerThread {
    private static final String LOG_TAG = "CameraHandlerThread";
    
    public ViewBarcodeScanner mScannerView;

    public void startCamera(final int i) {
        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Camera cameraInstance = UtilCamera.getCameraInstance(i);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.setupCameraPreview(WrapperCamera.getWrapper(cameraInstance, i));
                    }
                });
            }
        });
    }

    public HandlerThreadCamera(ViewBarcodeScanner barcodeScannerView) {
        super(LOG_TAG);
        this.mScannerView = barcodeScannerView;
        start();
    }
}

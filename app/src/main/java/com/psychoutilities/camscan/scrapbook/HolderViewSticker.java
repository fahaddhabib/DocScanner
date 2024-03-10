package com.psychoutilities.camscan.scrapbook;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.psychoutilities.camscan.R;

import java.util.ArrayList;
import java.util.Iterator;

public class HolderViewSticker extends RelativeLayout {
    private static final String TAG = "StickerHolderView";
    public static int imageCountBack;
    private CallbackOnStickerSelection callback;
    public ViewSticker currentStickerView;
    public int imageCount;
    long maxStickerMemory;
    private boolean startDeletePoint, startEditPoint;
    private float startStickerRotation,
            startStickerScale,
            startStickerX,
            startStickerY,
            translationX,
            translationY,
            scale;

    private boolean startWithFixedCenterPoint;
    private final ArrayList<ViewSticker> stickerViews;

    public interface CallbackOnStickerSelection {
        void onImageStickerSelected(ConfigImageSticker imageStickerConfig, boolean z);

        void onNoneStickerSelected();

        void onTextStickerSelected(TextStickerConfig textStickerConfig, boolean z);
    }

    public HolderViewSticker(Context context) {
        this(context, (AttributeSet) null);
    }

    @Override
    public void removeView(final View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "alpha", new float[]{view.getAlpha(), 0.0f}), ObjectAnimator.ofFloat(view, "scaleX", new float[]{view.getScaleX(), 0.0f}), ObjectAnimator.ofFloat(view, "scaleY", new float[]{view.getScaleY(), 0.0f})});
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                HolderViewSticker.super.removeView(view);
            }
        });
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    public void setScale(float scale) {
        this.scale = scale;
        Iterator<ViewSticker> it = this.stickerViews.iterator();
        while (it.hasNext()) {
            it.next().setScale(scale);
        }
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
        Iterator<ViewSticker> it = this.stickerViews.iterator();
        while (it.hasNext()) {
            it.next().setTranslationX(translationX);
        }
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
        Iterator<ViewSticker> it = this.stickerViews.iterator();
        while (it.hasNext()) {
            it.next().setTranslationY(translationY);
        }
    }

    public float getTranslationX() {
        return this.translationX;
    }

    public float getTranslationY() {
        return this.translationY;
    }

    public float getScale() {
        return this.scale;
    }

    public HolderViewSticker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HolderViewSticker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.scale = 1.0f;
        this.translationX = 0.0f;
        this.translationY = 0.0f;
        this.maxStickerMemory = Runtime.getRuntime().maxMemory() / 2;
        this.startStickerX = 0.0f;
        this.startStickerY = 0.0f;
        this.startStickerScale = 0.0f;
        this.startStickerRotation = 0.0f;
        this.startWithFixedCenterPoint = false;
        this.startDeletePoint = false;
        this.imageCount = 0;
        this.translationX = 0.0f;
        this.translationY = 0.0f;
        this.maxStickerMemory = Runtime.getRuntime().maxMemory() / 2;
        this.startStickerX = 0.0f;
        this.startStickerY = 0.0f;
        this.startStickerScale = 0.0f;
        this.startStickerRotation = 0.0f;
        this.startWithFixedCenterPoint = false;
        this.startDeletePoint = false;
        this.startEditPoint = false;
        this.stickerViews = new ArrayList<>();
    }

    public void refreshStickerView(TextStickerConfig textStickerConfig) {
        Iterator<ViewSticker> it = this.stickerViews.iterator();
        while (it.hasNext()) {
            ViewSticker next = it.next();
            if (textStickerConfig.equals(next.getConfig())) {
                next.refresh();
            }
        }
    }

    public void addStickerView(TextStickerConfig textStickerConfig) {
        if (textStickerConfig.getText().trim().length() != 0) {
            addStickerView(textStickerConfig, true);
        }
    }

    public void addStickerView(ConfigImageSticker imageStickerConfig) {
        addStickerView(imageStickerConfig, true);
    }

    private void addStickerView(ConfigInterfaceSticker stickerConfigInterface, boolean z) {
        ViewSticker stickerView = new ViewSticker(getContext(), stickerConfigInterface, this, this.stickerViews.size());
        stickerView.setScale(this.scale);
        stickerView.setTranslationX(this.translationX);
        stickerView.setTranslationY(this.translationY);
        if (stickerConfigInterface.getType() == ConfigInterfaceSticker.STICKERTYPE.IMAGE) {
            this.imageCount++;
        }
        if (stickerConfigInterface.getType() == ConfigInterfaceSticker.STICKERTYPE.IMAGE) {
            imageCountBack++;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(stickerView, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(stickerView, "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(stickerView, "scaleY", new float[]{0.0f, 1.0f})});
        addView(stickerView, new RelativeLayout.LayoutParams(-1, -1));
        this.stickerViews.add(stickerView);
        animatorSet.start();
        setCurrentEdit(stickerView, z);
        Log.e(TAG, "addStickerView: ");
    }

    public float takeStickerMemory(ViewSticker stickerView) {
        Iterator<ViewSticker> it = this.stickerViews.iterator();
        long j = 0;
        long j2 = 0;
        while (it.hasNext()) {
            ViewSticker next = it.next();
            j += (long) next.getAllocatedByteCount();
            j2 += next.getRequestedByteCount();
        }
        long maxMemory = (Runtime.getRuntime().maxMemory() - (Runtime.getRuntime().totalMemory() - j)) / 2;
        if (this.maxStickerMemory > maxMemory) {
            this.maxStickerMemory = maxMemory;
        }
        float f = (float) (((double) this.maxStickerMemory) / ((double) j2));
        if (f <= 0.0f) {
            f = 0.0f;
        } else if (f >= 1.0f) {
            f = 1.0f;
        }
        Iterator<ViewSticker> it2 = this.stickerViews.iterator();
        while (it2.hasNext()) {
            ViewSticker next2 = it2.next();
            if (stickerView != next2) {
                next2.rescaleCache(f);
            }
        }
        return f;
    }

    public void deleteSticker() {
        if(currentStickerView != null) {
            stickerViews.remove(currentStickerView);
            ConfigInterfaceSticker config = currentStickerView.getConfig();
            if (config != null) {
                switch (config.getType()) {
                    case IMAGE:
                        break;
                    case TEXT:
                        break;

                }
            }

            removeView(currentStickerView);

            setCurrentEdit(null, false);
        }
    }

    public synchronized Bitmap drawToBitmap(Bitmap bitmap, int i, int i2) {
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            Iterator<ViewSticker> it = this.stickerViews.iterator();
            while (it.hasNext()) {
                it.next().rescaleCache(0.0f);
            }
            Iterator<ViewSticker> it2 = this.stickerViews.iterator();
            while (it2.hasNext()) {
                it2.next().drawStickerToCanvas(canvas, i, i2);
            }
        }
        return bitmap;
    }

    public void setTextStickerSelectionCallback(CallbackOnStickerSelection onStickerSelectionCallback) {
        this.callback = onStickerSelectionCallback;
    }

    public void setEditImageOnSticker(Bitmap bitmap) {
        this.currentStickerView.setStickerPictureCache(bitmap);
        this.currentStickerView.setEditedImage(bitmap, true);
    }

    public void flipSticker(boolean isFlip) {
        if (currentStickerView != null) {
            currentStickerView.flip(isFlip);
        }
    }

    public void flipStickerHorizontal(boolean isFlipHorizontal) {
        if (currentStickerView != null) {
            currentStickerView.flipHorizontal(isFlipHorizontal);
        }
    }

    public void leftRotate(boolean isRotateLeft) {
        if (currentStickerView != null) {
            currentStickerView.roteteLeft(isRotateLeft);
        }
    }

    public void rightRotate(boolean isRotateRight) {
        if (currentStickerView != null) {
            currentStickerView.roteteRight(isRotateRight);
        }
    }

    public void enableSelectableMode(boolean z) {
        ViewSticker stickerView = this.currentStickerView;
        if (stickerView != null) {
            stickerView.setInEdit(false);
            this.currentStickerView = null;
        }
        setEnabled(z);
    }

    public void leaveSticker() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (childAt instanceof ViewSticker) {
                ((ViewSticker) childAt).setInEdit(false);
            }
        }
    }

    public ConfigInterfaceSticker getCurrentStickerConfig() {
        ViewSticker stickerView = this.currentStickerView;
        if (stickerView != null) {
            return stickerView.getConfig();
        }
        return null;
    }

    public void bringStickerToFront() {
        if (currentStickerView != null) {
            int position = stickerViews.indexOf(currentStickerView);
            if (position == stickerViews.size() - 1) {
                return;
            }

            ConfigInterfaceSticker config = currentStickerView.getConfig();
            if (config != null) {
                switch (config.getType()) {
                    case IMAGE:
                        break;
                    case TEXT:
                        break;
                }
            }

            ViewSticker stickerTemp = stickerViews.remove(position);
            stickerViews.add(stickerViews.size(), stickerTemp);
            currentStickerView.bringToFront();
        }
    }

    private synchronized ViewSticker setCurrentEdit(ViewSticker stickerView, boolean z) {
        if (stickerView == null) {
            leaveSticker();
            this.callback.onNoneStickerSelected();
        } else if (!stickerView.equals(this.currentStickerView) || !stickerView.isInEdit()) {
            leaveSticker();
            this.currentStickerView = stickerView;
            if (this.callback != null) {
                if (this.currentStickerView.getConfig().getType() == ConfigInterfaceSticker.STICKERTYPE.TEXT) {
                    this.callback.onTextStickerSelected((TextStickerConfig) this.currentStickerView.getConfig(), z);
                    bringStickerToFront();
                } else {
                    this.callback.onImageStickerSelected((ConfigImageSticker) this.currentStickerView.getConfig(), z);
                    bringStickerToFront();
                }
            }
            post(new Runnable() {
                @Override
                public void run() {
                    if (HolderViewSticker.this.currentStickerView != null) {
                        HolderViewSticker.this.currentStickerView.setInEdit(true);
                    }
                }
            });
        }
        return this.currentStickerView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        EventWrapperScaledMotion scaledMotionEventWrapper = new EventWrapperScaledMotion(motionEvent, this.scale, this.translationX, this.translationY);
        ViewSticker stickerView = this.currentStickerView;
        ViewSticker stickerView2 = (stickerView == null || !stickerView.isInEdit()) ? null : this.currentStickerView;
        if (stickerView2 == null) {
            Iterator<ViewSticker> it = this.stickerViews.iterator();
            while (it.hasNext()) {
                ViewSticker next = it.next();
                if (next.isInEdit()) {
                    this.currentStickerView = next;
                    stickerView2 = next;
                }
            }
        }
        if (scaledMotionEventWrapper.hasClicked()) {
            for (int size = this.stickerViews.size() - 1; size >= 0; size--) {
                ViewSticker stickerView3 = this.stickerViews.get(size);
                if (stickerView3.isInBitmap(scaledMotionEventWrapper)) {
                    setCurrentEdit(stickerView3, false);
                    return true;
                }
            }
            setCurrentEdit((ViewSticker) null, false);
            return true;
        }
        if (stickerView2 != null) {
            if (scaledMotionEventWrapper.isCheckpoint()) {
                float[] currentTransformState = stickerView2.getCurrentTransformState();
                this.startStickerX = currentTransformState[0];
                this.startStickerY = currentTransformState[1];
                this.startStickerScale = currentTransformState[2];
                this.startStickerRotation = currentTransformState[3];
                this.startWithFixedCenterPoint = stickerView2.isOnResizeButton(scaledMotionEventWrapper);
                if (this.startWithFixedCenterPoint) {
                    scaledMotionEventWrapper.setFixedCenterPoint(this.startStickerX, this.startStickerY);
                }
                this.startDeletePoint = stickerView2.isOnDelButton(scaledMotionEventWrapper);
                this.startEditPoint = stickerView2.isOnEditButton(scaledMotionEventWrapper);
            } else {
                if (this.startWithFixedCenterPoint) {
                    scaledMotionEventWrapper.setFixedCenterPoint(this.startStickerX, this.startStickerY);
                }
                EventWrapperScaledMotion.DiffTransform transformDifference = scaledMotionEventWrapper.getTransformDifference();
                float f = this.startStickerX + transformDifference.xDiff;
                float f2 = this.startStickerY + transformDifference.yDiff;
                float f3 = this.startStickerScale * transformDifference.scale;
                float f4 = this.startStickerRotation + transformDifference.angleDiff;
                if (((float) rect.left) > f) {
                    this.startStickerX += ((float) rect.left) - f;
                    f = (float) rect.left;
                }
                if (((float) rect.right) < f) {
                    this.startStickerX += ((float) rect.right) - f;
                    f = (float) rect.right;
                }
                if (((float) rect.top) > f2) {
                    this.startStickerY += ((float) rect.top) - f2;
                    f2 = (float) rect.top;
                }
                if (((float) rect.bottom) < f2) {
                    this.startStickerY += ((float) rect.bottom) - f2;
                    f2 = (float) rect.bottom;
                }
                stickerView2.setTransformation(f, f2, f3, f4);
            }
            if (this.startDeletePoint) {
                this.startDeletePoint = false;
                if (this.stickerViews.size() == 1) {
                    Toast.makeText(getContext(), getResources().getString(R.string.image_dont_close), Toast.LENGTH_LONG).show();
                } else {
                    deleteSticker();
                }
            }
            if (this.startEditPoint) {
                this.startEditPoint = false;
                if (this.currentStickerView.getType() == ConfigInterfaceSticker.STICKERTYPE.TEXT) {
                    deleteSticker();
                }
            }
            invalidate();
        }
        return true;
    }
}

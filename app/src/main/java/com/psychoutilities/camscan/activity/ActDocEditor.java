package com.psychoutilities.camscan.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.divyanshu.colorseekbar.ColorSeekBar;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.itextpdf.text.html.HtmlTags;
import com.kyanogen.signatureview.SignatureView;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.adapter.AdapterColorEffect;
import com.psychoutilities.camscan.adapter.AdapterColorFilter;
import com.psychoutilities.camscan.adapter.AdapterEditTools;
import com.psychoutilities.camscan.adapter.AdapterFont;
import com.psychoutilities.camscan.adapter.AdapterOverlay;
import com.psychoutilities.camscan.adapter.AdapterSignature;
import com.psychoutilities.camscan.adapter.AdapterWatermarkFont;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.document_view.FilterColor;
import com.psychoutilities.camscan.main_utils.UtilAdjust;
import com.psychoutilities.camscan.main_utils.UtilsBitmap;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.models.ToolTypeEdit;
import com.psychoutilities.camscan.utils.AdsAdmob;
import com.psychoutilities.camscan.utils.AdsUtils;
import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkText;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class ActDocEditor extends BaseAct implements View.OnClickListener, AdapterEditTools.ToolOnSelected, SeekBar.OnSeekBarChangeListener {

    //colorSeekBar
    private ColorSeekBar sb_pen_color,
            sb_txt_color,
            sb_watermark_color;

    //String
    public String defaultText,
            current_doc_name,
            edited_doc_grp_name,
            fromTAG,
            scan_doc_grp_name,
            selected_group_name;

    //Relative Layout
    private RelativeLayout rl_main,
            rl_signature,
            rl_signature_list,
            rl_txt_color,
            rl_watermark,
            rl_watermark_color,
            rl_watermark_opacity;

    //RecycleView
     private RecyclerView rv_color_effect,
             rv_color_filter,
             rv_edit_tools,
             rv_font,
             rv_overlay,
             rv_signature,
             rv_watermark_font;

    //seekBar
     private SeekBar sb_adjust,
             sb_eraser_size,
             sb_highlight_size,
             sb_opacity,
             sb_overlay,
             sb_pen_size,
             sb_watermark_opacity;

    //TextViews
    protected TextView tv_clear_signature, tv_no_signature, tv_progress,
            txtBrightness, txtContrast, txtSaturation, txtExposure,
            txtColor, txtEraser, txtHighlight,
            txtCreateSig, txtSavedSig;
    //ImageView
    private ImageView iv_align_center,
            iv_align_left,
            iv_align_right,
            iv_bold,
            iv_brightness,
            iv_contrast,
            iv_create_signature,
            iv_erase,
            iv_exposure,
            iv_highlight,
            iv_italic,
            iv_overlayImg,
            iv_saturation,
            iv_saved_signature,
            iv_done;

    //LinearLayout
    private LinearLayout ly_adjust,
            ly_alignment,
            ly_color_effect,
            ly_color_filter,
            ly_edit_tools,
            ly_highlight,
            ly_opacity,
            ly_overlay,
            ly_seek_view,
            ly_text;

    private static final String TAG = "DocumentEditorActivity";
    public static ActDocEditor documentEditorActivity;
    private int adjustPosition = 0;
    public float brightness = 1.0f;
    public int brush_color;
    protected AdapterColorEffect colorEffectAdapter;
    private FilterColor colorFilter;
    protected AdapterColorFilter colorFilterAdapter;
    private String[] colorFilterName;
    public float contrast = 1.0f;
    public HelperDB dbHelper;
    private Layout.Alignment defaultAlign;
    private Typeface defaultFont;
    public int defaultTxtColor;
    protected AdapterEditTools editToolsAdapter;
    public EditText et_watermark_txt;
    protected AdapterFont fontAdapter;
    private boolean isbold = false;
    private boolean isitalic = false;
    private PhotoEditorView iv_editImg;
    protected AdapterOverlay overlayAdapter;
    public PhotoEditor photoEditor;
    public int position;
    public int signa_pen_color;
    protected AdapterSignature signatureAdapter;
    private ArrayList<String> signatureList = new ArrayList<>();
    public SignatureView signature_view;
    public StickerView stickerView;
    protected TextSticker textSticker;
    private int watermarkFont;
    protected AdapterWatermarkFont watermarkFontAdapter;
    public float saturation = 1.0f;

    @Override
    public void onBackPressed() {
        if (ly_color_filter.getVisibility() == View.VISIBLE) {
            Constant.filterPosition = 0;
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_color_filter);
            iv_editImg.getSource().setImageBitmap(Constant.original);
        } else if (ly_adjust.getVisibility() == View.VISIBLE) {
            Constant.filterPosition = 0;
            iv_brightness.setImageResource(R.drawable.ic_brightness_selection);
            iv_contrast.setImageResource(R.drawable.ic_contrast);
            iv_saturation.setImageResource(R.drawable.ic_saturation);
            iv_exposure.setImageResource(R.drawable.ic_exposure);

            txtBrightness.setTextColor(getResources().getColor(R.color.black));
            txtContrast.setTextColor(getResources().getColor(R.color.white));
            txtSaturation.setTextColor(getResources().getColor(R.color.white));
            txtExposure.setTextColor(getResources().getColor(R.color.white));

            adjustPosition = 0;
            sb_adjust.setProgress(128);
            tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
            Constant.adjustProgressArray[0][1] = 128;
            Constant.adjustProgressArray[1][1] = 78;
            Constant.adjustProgressArray[2][1] = 66;
            Constant.adjustProgressArray[3][1] = 0;
            iv_editImg.getSource().setColorFilter(UtilAdjust.setBrightness(50));
            contrast = 0.975976f;
            changeContrast();
            saturation = 9.0f;
            changeSaturation();
            changeExposure(0);
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_adjust);
        } else if (ly_highlight.getVisibility() == View.VISIBLE) {
            photoEditor.setBrushDrawingMode(false);
            photoEditor.clearAllViews();
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_highlight);
        } else if (ly_opacity.getVisibility() == View.VISIBLE) {
            if (stickerView.getCurrentSticker() != null) {
                stickerView.getCurrentSticker().setAlpha(255);
                stickerView.invalidate();
                sb_opacity.setProgress(255);
            }
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_opacity);
        } else if (rl_signature.getVisibility() == View.VISIBLE) {
            signature_view.clearCanvas();
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            rl_signature.setVisibility(View.GONE);
        } else if (rl_watermark.getVisibility() == View.VISIBLE) {
            hideSoftKeyboard(et_watermark_txt);
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            rv_watermark_font.setVisibility(View.GONE);
            rl_watermark_color.setVisibility(View.GONE);
            rl_watermark_opacity.setVisibility(View.GONE);
            rl_watermark.setVisibility(View.GONE);
        } else if (ly_text.getVisibility() == View.VISIBLE) {
            stickerView.removeCurrentSticker();
            rv_font.setVisibility(View.GONE);
            rl_txt_color.setVisibility(View.GONE);
            ly_alignment.setVisibility(View.GONE);
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_text);
        } else if (ly_overlay.getVisibility() == View.VISIBLE) {
            iv_overlayImg.setVisibility(View.GONE);
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_overlay);
        } else if (ly_color_effect.getVisibility() == View.VISIBLE) {
            iv_editImg.getSource().setColorFilter(Constant.coloreffect[0]);
            ly_edit_tools.setVisibility(View.VISIBLE);
            iv_done.setVisibility(View.VISIBLE);
            slideDownAnimation(ly_color_effect);
        } else {
            final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.exit_dailog_editor_screen_act);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            ( dialog.findViewById(R.id.iv_exit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    finish();
                }
            });
            ( dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onToolSelected(ToolTypeEdit editToolType) {
        switch (editToolType) {
            case COLORFILTER:
                rv_color_filter.setHasFixedSize(true);
                rv_color_filter.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                colorFilterAdapter = new AdapterColorFilter(this, colorFilterName);
                rv_color_filter.setAdapter(colorFilterAdapter);
                colorFilterAdapter.notifyDataSetChanged();
                slideUpAnimation(ly_color_filter);
                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                return;
            case ADJUST:
                slideUpAnimation(ly_adjust);
                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                return;
            case HIGHLIGHT:
                photoEditor.setBrushDrawingMode(true);
                photoEditor.setBrushColor(brush_color);
                sb_highlight_size.setVisibility(View.VISIBLE);
                sb_eraser_size.setVisibility(View.GONE);
                iv_highlight.setImageResource(R.drawable.bic_highlight_selection);
                iv_erase.setImageResource(R.drawable.bic_erase);

                txtHighlight.setTextColor(getResources().getColor(R.color.black));
                txtEraser.setTextColor(getResources().getColor(R.color.white));
                txtColor.setTextColor(getResources().getColor(R.color.white));

                slideUpAnimation(ly_highlight);
                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                return;
            case PICTURE:
                ImagePicker.with(this)
                        .setStatusBarColor("#6db2ff")
                        .setToolbarColor("#6db2ff")
                        .setBackgroundColor("#ffffff")
                        .setFolderMode(true)
                        .setFolderTitle(getResources().getString(R.string.gallery))
                        .setMultipleMode(true)
                        .setShowNumberIndicator(true)
                        .setAlwaysShowDoneButton(true)
                        .setMaxSize(1)
                        .setShowCamera(false)
                        .setLimitMessage("You can select up to 1 images")
                        .setRequestCode(100)
                        .start();
                return;
            case SIGNATURE:
                iv_create_signature.setImageResource(R.drawable.ic_create_sig_selection);
                iv_saved_signature.setImageResource(R.drawable.ic_saved_sig);

                txtCreateSig.setTextColor(getResources().getColor(R.color.black));
                txtSavedSig.setTextColor(getResources().getColor(R.color.light_bg_color));

                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                rl_signature.setVisibility(View.VISIBLE);
                ly_seek_view.setVisibility(View.VISIBLE);
                rl_signature_list.setVisibility(View.GONE);
                signature_view.clearCanvas();
                return;
            case WATERMARK:
                et_watermark_txt.setText("");
                showSoftKeyboard(et_watermark_txt);
                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                rl_watermark.setVisibility(View.VISIBLE);
                return;
            case TEXT:
                addTextDialog("", false);
                return;
            case COLOREFFECT:
                rv_color_effect.setHasFixedSize(true);
                rv_color_effect.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                colorEffectAdapter = new AdapterColorEffect(this);
                rv_color_effect.setAdapter(colorEffectAdapter);
                colorEffectAdapter.notifyDataSetChanged();
                slideUpAnimation(ly_color_effect);
                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                return;
            case OVERLAY:
                rv_overlay.setHasFixedSize(true);
                rv_overlay.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                overlayAdapter = new AdapterOverlay(this);
                rv_overlay.setAdapter(overlayAdapter);
                overlayAdapter.notifyDataSetChanged();
                slideUpAnimation(ly_overlay);
                ly_edit_tools.setVisibility(View.INVISIBLE);
                iv_done.setVisibility(View.INVISIBLE);
                return;
            default:
                return;
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        if (ImagePicker.shouldHandleResult(i, i2, intent, 100)) {
            Iterator<Image> it = ImagePicker.getImages(intent).iterator();
            while (it.hasNext()) {
                Image next = it.next();
                if (Build.VERSION.SDK_INT >= 29) {
                    Glide.with(getApplicationContext()).asDrawable().load(next.getUri()).into(new SimpleTarget<Drawable>() {
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                            stickerView.addSticker(new DrawableSticker(drawable), 1);
                            ly_edit_tools.setVisibility(View.INVISIBLE);
                            iv_done.setVisibility(View.INVISIBLE);
                            documentEditorActivity.slideUpAnimation(documentEditorActivity.ly_opacity);
                            sb_opacity.setProgress(255);
                        }
                    });
                } else {
                    Glide.with(getApplicationContext()).asDrawable().load(next.getPath()).into(new SimpleTarget<Drawable>() {
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                            stickerView.addSticker(new DrawableSticker(drawable), 1);
                            ly_edit_tools.setVisibility(View.INVISIBLE);
                            iv_done.setVisibility(View.INVISIBLE);
                            documentEditorActivity.slideUpAnimation(documentEditorActivity.ly_opacity);
                            sb_opacity.setProgress(255);
                        }
                    });
                }
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_align_center:
                iv_align_left.setImageResource(R.drawable.tic_left);
                iv_align_center.setImageResource(R.drawable.tic_center_selection);
                iv_align_right.setImageResource(R.drawable.tic_right);
                iv_bold.setImageResource(R.drawable.tic_bold);
                iv_italic.setImageResource(R.drawable.tic_italic);
                isbold = false;
                isitalic = false;
                defaultAlign = Layout.Alignment.ALIGN_CENTER;
                ChangeTextSticker(defaultTxtColor, this, stickerView);
                return;
            case R.id.iv_align_left:
                iv_align_left.setImageResource(R.drawable.tic_left_selection);
                iv_align_center.setImageResource(R.drawable.tic_center);
                iv_align_right.setImageResource(R.drawable.tic_right);
                iv_bold.setImageResource(R.drawable.tic_bold);
                iv_italic.setImageResource(R.drawable.tic_italic);
                isbold = false;
                isitalic = false;
                defaultAlign = Layout.Alignment.ALIGN_NORMAL;
                ChangeTextSticker(defaultTxtColor, this, stickerView);
                return;
            case R.id.iv_align_right:
                iv_align_left.setImageResource(R.drawable.tic_left);
                iv_align_center.setImageResource(R.drawable.tic_center);
                iv_align_right.setImageResource(R.drawable.tic_right_selection);
                iv_bold.setImageResource(R.drawable.tic_bold);
                iv_italic.setImageResource(R.drawable.tic_italic);
                isbold = false;
                isitalic = false;
                defaultAlign = Layout.Alignment.ALIGN_OPPOSITE;
                ChangeTextSticker(defaultTxtColor, this, stickerView);
                return;
            case R.id.iv_apply_adjust:
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_adjust);
                return;
            case R.id.iv_apply_effect:
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_color_effect);
                return;
            case R.id.iv_apply_filter:
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_color_filter);
                return;
            case R.id.iv_apply_highlight:
                photoEditor.setBrushDrawingMode(false);
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_highlight);
                return;
            case R.id.iv_apply_opacity:
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_opacity);
                return;
            case R.id.iv_apply_overlay:
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_overlay);
                return;
            case R.id.iv_apply_signature:
                if (!signature_view.isBitmapEmpty()) {
                    Bitmap signatureBitmap = signature_view.getSignatureBitmap();
                    new signatureSave().execute(new Bitmap[]{signatureBitmap});
                    return;
                }
                return;
            case R.id.iv_apply_txt:
                rv_font.setVisibility(View.GONE);
                rl_txt_color.setVisibility(View.GONE);
                ly_alignment.setVisibility(View.GONE);
                ly_edit_tools.setVisibility(View.VISIBLE);
                iv_done.setVisibility(View.VISIBLE);
                slideDownAnimation(ly_text);
                return;
            case R.id.iv_apply_watermark:
                if (et_watermark_txt.getText().toString().length() > 0) {
                    ly_edit_tools.setVisibility(View.VISIBLE);
                    iv_done.setVisibility(View.VISIBLE);
                    rv_watermark_font.setVisibility(View.GONE);
                    rl_watermark_color.setVisibility(View.GONE);
                    rl_watermark_opacity.setVisibility(View.GONE);
                    rl_watermark.setVisibility(View.GONE);
                    WatermarkBuilder.create(getApplicationContext(), iv_editImg.getSource()).loadWatermarkText(new WatermarkText(et_watermark_txt.getText().toString()).setPositionX(2.0d).setPositionY(2.0d).setTextFont(watermarkFont).setTextColor(et_watermark_txt.getCurrentTextColor()).setTextAlpha(sb_watermark_opacity.getProgress()).setRotation(-30.0d).setTextSize(20.0d)).setTileMode(true).getWatermark().setToImageView(iv_editImg.getSource());
                    hideSoftKeyboard(et_watermark_txt);
                    return;
                }
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enter_text), Toast.LENGTH_SHORT).show();
                return;
            case R.id.iv_back:
                onBackPressed();
                return;
            default:
                switch (id) {
                    case R.id.iv_bold:
                        if (isbold) {
                            iv_bold.setImageResource(R.drawable.tic_bold);
                            if (isitalic) {
                                ChangeTextAlignment(Typeface.create(defaultFont, Typeface.ITALIC), defaultAlign);
                                isbold = false;
                                return;
                            }
                            ChangeTextAlignment(defaultFont, defaultAlign);
                            isbold = false;
                            return;
                        }
                        iv_bold.setImageResource(R.drawable.tic_bold_selection);
                        if (isitalic) {
                            ChangeTextAlignment(Typeface.create(defaultFont, Typeface.BOLD_ITALIC), defaultAlign);
                            isbold = true;
                            return;
                        }
                        ChangeTextAlignment(Typeface.create(defaultFont, Typeface.BOLD), defaultAlign);
                        isbold = true;
                        return;
                    case R.id.llBrightness:
                        iv_brightness.setImageResource(R.drawable.ic_brightness_selection);
                        iv_contrast.setImageResource(R.drawable.ic_contrast);
                        iv_saturation.setImageResource(R.drawable.ic_saturation);
                        iv_exposure.setImageResource(R.drawable.ic_exposure);

                        txtBrightness.setTextColor(getResources().getColor(R.color.black));
                        txtContrast.setTextColor(getResources().getColor(R.color.white));
                        txtSaturation.setTextColor(getResources().getColor(R.color.white));
                        txtExposure.setTextColor(getResources().getColor(R.color.white));

                        adjustPosition = 0;
                        sb_adjust.setProgress(Constant.adjustProgressArray[0][1]);
                        tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
                        return;
                    default:
                        switch (id) {
                            case R.id.iv_close_adjust:
                                Constant.filterPosition = 0;
                                iv_brightness.setImageResource(R.drawable.ic_brightness_selection);
                                iv_contrast.setImageResource(R.drawable.ic_contrast);
                                iv_saturation.setImageResource(R.drawable.ic_saturation);
                                iv_exposure.setImageResource(R.drawable.ic_exposure);

                                txtBrightness.setTextColor(getResources().getColor(R.color.black));
                                txtContrast.setTextColor(getResources().getColor(R.color.white));
                                txtSaturation.setTextColor(getResources().getColor(R.color.white));
                                txtExposure.setTextColor(getResources().getColor(R.color.white));

                                adjustPosition = 0;
                                sb_adjust.setProgress(128);

                                tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
                                Constant.adjustProgressArray[0][1] = 128;
                                Constant.adjustProgressArray[1][1] = 78;
                                Constant.adjustProgressArray[2][1] = 66;
                                Constant.adjustProgressArray[3][1] = 0;
                                iv_editImg.getSource().setColorFilter(UtilAdjust.setBrightness(64));
                                contrast = 0.975976f;
                                changeContrast();
                                saturation = 9.0f;
                                changeSaturation();
                                changeExposure(0);
                                ly_edit_tools.setVisibility(View.VISIBLE);
                                iv_done.setVisibility(View.VISIBLE);
                                slideDownAnimation(ly_adjust);
                                return;
                            case R.id.iv_close_effect:
                                iv_editImg.getSource().setColorFilter(Constant.coloreffect[0]);
                                ly_edit_tools.setVisibility(View.VISIBLE);
                                iv_done.setVisibility(View.VISIBLE);
                                slideDownAnimation(ly_color_effect);
                                return;
                            case R.id.iv_close_filter:
                                Constant.filterPosition = 0;
                                ly_edit_tools.setVisibility(View.VISIBLE);
                                iv_done.setVisibility(View.VISIBLE);
                                slideDownAnimation(ly_color_filter);
                                iv_editImg.getSource().setImageBitmap(Constant.original);
                                return;
                            case R.id.iv_close_highlight:
                                photoEditor.setBrushDrawingMode(false);
                                photoEditor.clearAllViews();
                                ly_edit_tools.setVisibility(View.VISIBLE);
                                iv_done.setVisibility(View.VISIBLE);
                                slideDownAnimation(ly_highlight);
                                return;
                            case R.id.iv_close_opacity:
                                if (stickerView.getCurrentSticker() != null) {
                                    stickerView.getCurrentSticker().setAlpha(255);
                                    stickerView.invalidate();
                                    sb_opacity.setProgress(255);
                                }
                                ly_edit_tools.setVisibility(View.VISIBLE);
                                iv_done.setVisibility(View.VISIBLE);
                                slideDownAnimation(ly_opacity);
                                return;
                            case R.id.iv_close_overlay:
                                iv_overlayImg.setVisibility(View.GONE);
                                ly_edit_tools.setVisibility(View.VISIBLE);
                                iv_done.setVisibility(View.VISIBLE);
                                slideDownAnimation(ly_overlay);
                                return;
                            default:
                                switch (id) {
                                    case R.id.iv_close_signature:
                                        signature_view.clearCanvas();
                                        ly_edit_tools.setVisibility(View.VISIBLE);
                                        iv_done.setVisibility(View.VISIBLE);
                                        rl_signature.setVisibility(View.GONE);
                                        return;
                                    case R.id.iv_close_txt:
                                        stickerView.removeCurrentSticker();
                                        rv_font.setVisibility(View.GONE);
                                        rl_txt_color.setVisibility(View.GONE);
                                        ly_alignment.setVisibility(View.GONE);
                                        ly_edit_tools.setVisibility(View.VISIBLE);
                                        iv_done.setVisibility(View.VISIBLE);
                                        slideDownAnimation(ly_text);
                                        return;
                                    case R.id.iv_close_watermark:
                                        ly_edit_tools.setVisibility(View.VISIBLE);
                                        iv_done.setVisibility(View.VISIBLE);
                                        rv_watermark_font.setVisibility(View.GONE);
                                        rl_watermark_color.setVisibility(View.GONE);
                                        rl_watermark_opacity.setVisibility(View.GONE);
                                        rl_watermark.setVisibility(View.GONE);
                                        return;
                                    case R.id.llColor:
                                        ColorPickerDialogBuilder.with(this).setTitle("Choose color").initialColor(brush_color).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(10).setOnColorSelectedListener(new OnColorSelectedListener() {
                                            @Override
                                            public void onColorSelected(int i) {
                                            }
                                        }).setPositiveButton((CharSequence) "ok", (ColorPickerClickListener) new ColorPickerClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i, Integer[] numArr) {
                                                brush_color = i;
                                                photoEditor.setBrushColor(i);
                                                photoEditor.setOpacity(Color.alpha(i));
                                                iv_highlight.setImageResource(R.drawable.bic_highlight_selection);
                                                iv_erase.setImageResource(R.drawable.bic_erase);

                                                txtHighlight.setTextColor(getResources().getColor(R.color.black));
                                                txtEraser.setTextColor(getResources().getColor(R.color.white));
                                                txtColor.setTextColor(getResources().getColor(R.color.white));

                                                sb_highlight_size.setVisibility(View.VISIBLE);
                                                sb_eraser_size.setVisibility(View.GONE);
                                            }
                                        }).setNegativeButton((CharSequence) "cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        }).build().show();
                                        return;
                                    default:
                                        switch (id) {
                                            case R.id.llContrast:
                                                Constant.filterPosition = 0;
                                                iv_brightness.setImageResource(R.drawable.ic_brightness);
                                                iv_contrast.setImageResource(R.drawable.ic_contrast_selection);
                                                iv_saturation.setImageResource(R.drawable.ic_saturation);
                                                iv_exposure.setImageResource(R.drawable.ic_exposure);

                                                txtBrightness.setTextColor(getResources().getColor(R.color.white));
                                                txtContrast.setTextColor(getResources().getColor(R.color.black));
                                                txtSaturation.setTextColor(getResources().getColor(R.color.white));
                                                txtExposure.setTextColor(getResources().getColor(R.color.white));

                                                adjustPosition = 1;
                                                sb_adjust.setProgress(Constant.adjustProgressArray[1][1]);

                                                tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
                                                return;
                                            case R.id.llCreateSig:
                                                iv_create_signature.setImageResource(R.drawable.ic_create_sig_selection);
                                                iv_saved_signature.setImageResource(R.drawable.ic_saved_sig);

                                                txtCreateSig.setTextColor(getResources().getColor(R.color.black));
                                                txtSavedSig.setTextColor(getResources().getColor(R.color.light_bg_color));

                                                ly_seek_view.setVisibility(View.VISIBLE);
                                                rl_signature_list.setVisibility(View.GONE);
                                                return;
                                            case R.id.iv_done:
                                                stickerView.setConstrained(false);
                                                stickerView.setLocked(true);
                                                new bitmapSaveFinal().execute(new Bitmap[0]);
                                                return;
                                            case R.id.llEraser:
                                                iv_highlight.setImageResource(R.drawable.ic_highlight);
                                                iv_erase.setImageResource(R.drawable.bic_erase_selection);

                                                txtHighlight.setTextColor(getResources().getColor(R.color.white));
                                                txtEraser.setTextColor(getResources().getColor(R.color.black));
                                                txtColor.setTextColor(getResources().getColor(R.color.white));

                                                sb_highlight_size.setVisibility(View.GONE);
                                                sb_eraser_size.setVisibility(View.VISIBLE);
                                                photoEditor.brushEraser();
                                                return;
                                            case R.id.llExposure:
                                                iv_brightness.setImageResource(R.drawable.ic_brightness);
                                                iv_contrast.setImageResource(R.drawable.ic_contrast);
                                                iv_saturation.setImageResource(R.drawable.ic_saturation);
                                                iv_exposure.setImageResource(R.drawable.ic_exposure_selection);

                                                txtBrightness.setTextColor(getResources().getColor(R.color.white));
                                                txtContrast.setTextColor(getResources().getColor(R.color.white));
                                                txtSaturation.setTextColor(getResources().getColor(R.color.white));
                                                txtExposure.setTextColor(getResources().getColor(R.color.black));

                                                adjustPosition = 3;
                                                sb_adjust.setProgress(Constant.adjustProgressArray[3][1]);
                                                tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
                                                return;
                                            case R.id.llHighLight:
                                                iv_highlight.setImageResource(R.drawable.bic_highlight_selection);
                                                iv_erase.setImageResource(R.drawable.bic_erase);

                                                txtHighlight.setTextColor(getResources().getColor(R.color.black));
                                                txtEraser.setTextColor(getResources().getColor(R.color.white));
                                                txtColor.setTextColor(getResources().getColor(R.color.white));

                                                sb_highlight_size.setVisibility(View.VISIBLE);
                                                sb_eraser_size.setVisibility(View.GONE);
                                                photoEditor.setBrushDrawingMode(true);
                                                return;
                                            case R.id.iv_italic:
                                                if (isitalic) {
                                                    iv_italic.setImageResource(R.drawable.tic_italic);
                                                    if (isbold) {
                                                        ChangeTextAlignment(Typeface.create(defaultFont, Typeface.BOLD), defaultAlign);
                                                        isitalic = false;
                                                        return;
                                                    }
                                                    ChangeTextAlignment(defaultFont, defaultAlign);
                                                    isitalic = false;
                                                    return;
                                                }
                                                iv_italic.setImageResource(R.drawable.tic_italic_selection);
                                                if (isbold) {
                                                    ChangeTextAlignment(Typeface.create(defaultFont, Typeface.BOLD_ITALIC), defaultAlign);
                                                    isitalic = true;
                                                    return;
                                                }
                                                ChangeTextAlignment(Typeface.create(defaultFont, Typeface.ITALIC), defaultAlign);
                                                isitalic = true;
                                                return;
                                            case R.id.llSaturation:
                                                Constant.filterPosition = 0;
                                                iv_brightness.setImageResource(R.drawable.ic_brightness);
                                                iv_contrast.setImageResource(R.drawable.ic_contrast);
                                                iv_saturation.setImageResource(R.drawable.ic_saturation_selection);
                                                iv_exposure.setImageResource(R.drawable.ic_exposure);

                                                txtBrightness.setTextColor(getResources().getColor(R.color.white));
                                                txtContrast.setTextColor(getResources().getColor(R.color.white));
                                                txtSaturation.setTextColor(getResources().getColor(R.color.black));
                                                txtExposure.setTextColor(getResources().getColor(R.color.white));

                                                adjustPosition = 2;
                                                sb_adjust.setProgress(Constant.adjustProgressArray[2][1]);
                                                tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
                                                return;
                                            case R.id.llSavedSig:
                                                setSignatureAdapter();
                                                iv_create_signature.setImageResource(R.drawable.ic_create_sig);
                                                iv_saved_signature.setImageResource(R.drawable.ic_saved_sig_selection);

                                                txtCreateSig.setTextColor(getResources().getColor(R.color.light_bg_color));
                                                txtSavedSig.setTextColor(getResources().getColor(R.color.black));

                                                ly_seek_view.setVisibility(View.GONE);
                                                rl_signature_list.setVisibility(View.VISIBLE);
                                                return;
                                            case R.id.tv_clear_signature:
                                                signature_view.clearCanvas();
                                                return;
                                            default:
                                                switch (id) {
                                                    case R.id.iv_txt_alignment:
                                                        rv_font.setVisibility(View.GONE);
                                                        rl_txt_color.setVisibility(View.GONE);
                                                        ly_alignment.setVisibility(View.VISIBLE);
                                                        return;
                                                    case R.id.iv_txt_color:
                                                        rv_font.setVisibility(View.GONE);
                                                        rl_txt_color.setVisibility(View.VISIBLE);
                                                        ly_alignment.setVisibility(View.GONE);
                                                        return;
                                                    case R.id.iv_txt_font:
                                                        ArrayList arrayList = new ArrayList(getAssertFile(HtmlTags.FONT));
                                                        rv_font.setHasFixedSize(true);
                                                        rv_font.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                                                        fontAdapter = new AdapterFont(this, arrayList);
                                                        rv_font.setAdapter(fontAdapter);
                                                        rv_font.setVisibility(View.VISIBLE);
                                                        rl_txt_color.setVisibility(View.GONE);
                                                        ly_alignment.setVisibility(View.GONE);
                                                        return;
                                                    default:
                                                        switch (id) {
                                                            case R.id.iv_watermark_color:
                                                                rv_watermark_font.setVisibility(View.GONE);
                                                                rl_watermark_color.setVisibility(View.VISIBLE);
                                                                rl_watermark_opacity.setVisibility(View.GONE);
                                                                return;
                                                            case R.id.iv_watermark_font:
                                                                rv_watermark_font.setHasFixedSize(true);
                                                                rv_watermark_font.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                                                                watermarkFontAdapter = new AdapterWatermarkFont(this, waterMarkFontList());
                                                                rv_watermark_font.setAdapter(watermarkFontAdapter);
                                                                rv_watermark_font.setVisibility(View.VISIBLE);
                                                                rl_watermark_color.setVisibility(View.GONE);
                                                                rl_watermark_opacity.setVisibility(View.GONE);
                                                                return;
                                                            case R.id.iv_watermark_opacity:
                                                                rv_watermark_font.setVisibility(View.GONE);
                                                                rl_watermark_color.setVisibility(View.GONE);
                                                                rl_watermark_opacity.setVisibility(View.VISIBLE);
                                                                return;
                                                            default:
                                                                return;
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".SavedEditDocumentActivity"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.document_editor_act);
        documentEditorActivity = this;
        dbHelper = new HelperDB(this);
        init();
        bindView();

        AdsUtils.loadGoogleInterstitialAd(this, ActDocEditor.this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        switch (seekBar.getId()) {
            case R.id.sb_adjust:

                if (adjustPosition == 0) {
                    Constant.adjustProgressArray[0][1] = i;
                    iv_editImg.getSource().setColorFilter(UtilAdjust.setBrightness(i / 2));

                    tv_progress.setText(Constant.getProgressPercentage(i, 255) + "");
                    return;
                } else if (adjustPosition == 1) {
                    contrast = (float) ((((double) i) / 9.99d) / 8.0d);
                    changeContrast();
                    Constant.adjustProgressArray[1][1] = i;

                    tv_progress.setText(Constant.getProgressPercentage(i, 255) + "");
                    return;
                } else if (adjustPosition == 2) {
                    saturation = (float) (i / 7);
                    changeSaturation();
                    Constant.adjustProgressArray[2][1] = i;

                    tv_progress.setText(Constant.getProgressPercentage(i, 255) + "");
                    return;
                } else if (adjustPosition == 3) {
                    Constant.adjustProgressArray[3][1] = i;
                    changeExposure(i / 2);

                    tv_progress.setText(Constant.getProgressPercentage(i, 255) + "");
                    return;
                } else {
                    return;
                }
            case R.id.sb_eraser_size:
                photoEditor.setBrushEraserSize((float) i);
                photoEditor.brushEraser();
                return;
            case R.id.sb_highlight_size:
                photoEditor.setBrushSize((float) i);
                return;
            case R.id.sb_opacity:

                if (stickerView != null && stickerView.getCurrentSticker() != null) {
                    stickerView.getCurrentSticker().setAlpha(sb_opacity.getProgress());
                    stickerView.invalidate();
                    return;
                }
                return;
            case R.id.sb_overlay:
                if (iv_overlayImg.getVisibility() == View.VISIBLE) {
                    iv_overlayImg.setAlpha(((float) sb_overlay.getProgress()) / 100.0f);
                    return;
                }
                return;
            case R.id.sb_pen_size:
                signature_view.setPenSize((float) i);
                return;
            case R.id.sb_watermark_opacity:
                et_watermark_txt.setAlpha(((float) i) / 255.0f);
                return;
            default:
                return;
        }
    }

    private void init() {
        txtCreateSig =  findViewById(R.id.txtCreateSig);
        txtSavedSig =  findViewById(R.id.txtSavedSig);
        txtColor =  findViewById(R.id.txtColor);
        txtEraser =  findViewById(R.id.txtEraser);
        txtHighlight =  findViewById(R.id.txtHighlight);
        txtBrightness =  findViewById(R.id.txtBrightness);
        txtContrast =  findViewById(R.id.txtContrast);
        txtSaturation =  findViewById(R.id.txtSaturation);
        txtExposure =  findViewById(R.id.txtExposure);
        iv_done =  findViewById(R.id.iv_done);
        rl_main = findViewById(R.id.rl_main);
        iv_editImg = findViewById(R.id.iv_editImg);
        iv_overlayImg =  findViewById(R.id.iv_overlayImg);
        stickerView = findViewById(R.id.stickerView);
        ly_edit_tools = findViewById(R.id.ly_edit_tools);
        rv_edit_tools = findViewById(R.id.rv_edit_tools);
        ly_color_filter = findViewById(R.id.ly_color_filter);
        rv_color_filter = findViewById(R.id.rv_color_filter);
        ly_adjust = findViewById(R.id.ly_adjust);
        sb_adjust = findViewById(R.id.sb_adjust);
        tv_progress =  findViewById(R.id.tv_progress);
        iv_exposure =  findViewById(R.id.iv_exposure);
        iv_contrast =  findViewById(R.id.iv_contrast);
        iv_saturation =  findViewById(R.id.iv_saturation);
        iv_brightness =  findViewById(R.id.iv_brightness);
        ly_highlight = findViewById(R.id.ly_highlight);
        sb_highlight_size = findViewById(R.id.sb_highlight_size);
        sb_eraser_size = findViewById(R.id.sb_eraser_size);
        iv_highlight =  findViewById(R.id.iv_highlight);
        iv_erase =  findViewById(R.id.iv_erase);
        ly_opacity = findViewById(R.id.ly_opacity);
        sb_opacity = findViewById(R.id.sb_opacity);
        rl_signature = findViewById(R.id.rl_signature);
        signature_view =  findViewById(R.id.signature_view);
        ly_seek_view = findViewById(R.id.ly_seek_view);
        sb_pen_size = findViewById(R.id.sb_pen_size);
        sb_pen_color = findViewById(R.id.sb_pen_color);
        rl_signature_list = findViewById(R.id.rl_signature_list);
        rv_signature = findViewById(R.id.rv_signature);
        tv_no_signature =  findViewById(R.id.tv_no_signature);
        tv_clear_signature =  findViewById(R.id.tv_clear_signature);
        iv_create_signature =  findViewById(R.id.iv_create_signature);
        iv_saved_signature =  findViewById(R.id.iv_saved_signature);
        rl_watermark = findViewById(R.id.rl_watermark);
        et_watermark_txt =  findViewById(R.id.et_watermark_txt);
        rv_watermark_font = findViewById(R.id.rv_watermark_font);
        rl_watermark_color = findViewById(R.id.rl_watermark_color);
        sb_watermark_color = findViewById(R.id.sb_watermark_color);
        rl_watermark_opacity = findViewById(R.id.rl_watermark_opacity);
        sb_watermark_opacity = findViewById(R.id.sb_watermark_opacity);
        ly_text = findViewById(R.id.ly_text);
        rv_font = findViewById(R.id.rv_font);
        rl_txt_color = findViewById(R.id.rl_txt_color);
        sb_txt_color = findViewById(R.id.sb_txt_color);
        ly_alignment = findViewById(R.id.ly_alignment);
        iv_bold =  findViewById(R.id.iv_bold);
        iv_italic =  findViewById(R.id.iv_italic);
        iv_align_left =  findViewById(R.id.iv_align_left);
        iv_align_center =  findViewById(R.id.iv_align_center);
        iv_align_right =  findViewById(R.id.iv_align_right);
        ly_overlay = findViewById(R.id.ly_overlay);
        sb_overlay = findViewById(R.id.sb_overlay);
        rv_overlay = findViewById(R.id.rv_overlay);
        ly_color_effect = findViewById(R.id.ly_color_effect);
        rv_color_effect = findViewById(R.id.rv_color_effect);

        rv_color_effect.setOnClickListener(this::onClick);
    }

    private void bindView() {
        fromTAG = getIntent().getStringExtra("TAG");
        scan_doc_grp_name = getIntent().getStringExtra("scan_doc_group_name");
        edited_doc_grp_name = getIntent().getStringExtra("edited_doc_grp_name");
        current_doc_name = getIntent().getStringExtra("current_doc_name");
        position = getIntent().getIntExtra("position", -1);
        photoEditor = new PhotoEditor.Builder(this, iv_editImg).setPinchTextScalable(true).build();
        iv_editImg.getSource().setImageBitmap(Constant.original);
        setEditToolsAdapter();
        Constant.filterPosition = 0;
        colorFilter = new FilterColor();
        colorFilterName = getColorFilterName();
        Constant.adjustProgressArray[0][1] = 128;
        Constant.adjustProgressArray[1][1] = 78;
        Constant.adjustProgressArray[2][1] = 66;
        Constant.adjustProgressArray[3][1] = 0;
        sb_adjust.setProgress(128);

        tv_progress.setText(Constant.getProgressPercentage(sb_adjust.getProgress(), 255) + "");
        brush_color = getResources().getColor(R.color.yellow);
        photoEditor.setBrushSize(25.0f);
        photoEditor.setBrushEraserSize(25.0f);
        photoEditor.setBrushDrawingMode(false);
        setupSticker();
        stickerView.removeAllStickers();
        signa_pen_color = getResources().getColor(R.color.selected_txt_color);
        signature_view.setPenSize((float) sb_pen_size.getProgress());
        signature_view.setPenColor(signa_pen_color);
        sb_pen_color.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                signa_pen_color = i;
                signature_view.setPenColor(signa_pen_color);
            }
        });
        watermarkFont = R.font.roboto_medium;
        sb_watermark_color.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                et_watermark_txt.setTextColor(i);
            }
        });
        defaultFont = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
        defaultTxtColor = getResources().getColor(R.color.txt_color);
        defaultAlign = Layout.Alignment.ALIGN_CENTER;
        sb_txt_color.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                defaultTxtColor = i;
                documentEditorActivity.ChangeTextSticker(defaultTxtColor, documentEditorActivity, documentEditorActivity.stickerView);
            }
        });
        sb_adjust.setOnSeekBarChangeListener(this);
        sb_highlight_size.setOnSeekBarChangeListener(this);
        sb_eraser_size.setOnSeekBarChangeListener(this);
        sb_opacity.setOnSeekBarChangeListener(this);
        sb_pen_size.setOnSeekBarChangeListener(this);
        sb_watermark_opacity.setOnSeekBarChangeListener(this);
        sb_overlay.setOnSeekBarChangeListener(this);
    }

    public void onColorEffectClick(int i) {
        iv_editImg.getSource().setColorFilter(Constant.coloreffect[i]);
    }

    public void slideUpAnimation(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.up));
        view.setVisibility(View.VISIBLE);
    }

    public void slideDownAnimation(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.down));
        view.setVisibility(View.GONE);
    }

    private void setupSticker() {
        BitmapStickerIcon bitmapStickerIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_delete1), 0);
        bitmapStickerIcon.setIconEvent(new DeleteIconEvent());
        BitmapStickerIcon bitmapStickerIcon2 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_rotate), 3);
        bitmapStickerIcon2.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon bitmapStickerIcon3 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_flip), 1);
        bitmapStickerIcon3.setIconEvent(new FlipHorizontallyEvent());
        stickerView.setIcons(Arrays.asList(new BitmapStickerIcon[]{bitmapStickerIcon, bitmapStickerIcon2, bitmapStickerIcon3}));
        stickerView.setBackgroundColor(0);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);
        textSticker = new TextSticker(this);
        textSticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sticker_transparent_background));
        textSticker.setText("Hello World");
        textSticker.setTextColor(getResources().getColor(R.color.txt_color));
        textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        textSticker.resizeText();
        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onStickerAdded");
            }

            @Override
            public void onStickerClicked(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onStickerClicked");
                if (sticker instanceof DrawableSticker) {
                    if (ly_opacity.getVisibility() == View.GONE) {
                        ly_edit_tools.setVisibility(View.INVISIBLE);
                        iv_done.setVisibility(View.INVISIBLE);
                        documentEditorActivity.slideUpAnimation(documentEditorActivity.ly_opacity);
                    }
                } else if (sticker instanceof TextSticker) {
                    defaultText = ((TextSticker) sticker).getText();
                    stickerView.replace(sticker);
                    stickerView.invalidate();
                    if (ly_text.getVisibility() == View.GONE) {
                        ly_edit_tools.setVisibility(View.INVISIBLE);
                        iv_done.setVisibility(View.INVISIBLE);
                        documentEditorActivity.slideUpAnimation(documentEditorActivity.ly_text);
                    }
                }
                if (!stickerView.bringToFrontCurrentSticker) {
                    stickerView.stickers.remove(sticker);
                    stickerView.stickers.add(sticker);
                }
            }

            @Override
            public void onStickerDeleted(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onStickerDeleted");
                if (sticker instanceof TextSticker) {
                    if (ly_text.getVisibility() == View.VISIBLE) {
                        rv_font.setVisibility(View.GONE);
                        rl_txt_color.setVisibility(View.GONE);
                        ly_alignment.setVisibility(View.GONE);
                        ly_edit_tools.setVisibility(View.VISIBLE);
                        iv_done.setVisibility(View.VISIBLE);
                        documentEditorActivity.slideDownAnimation(documentEditorActivity.ly_text);
                    }
                } else if (ly_opacity.getVisibility() == View.VISIBLE) {
                    ly_edit_tools.setVisibility(View.VISIBLE);
                    iv_done.setVisibility(View.VISIBLE);
                    documentEditorActivity.slideDownAnimation(documentEditorActivity.ly_opacity);
                }
            }

            @Override
            public void onStickerDragFinished(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerEditClicked(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(Sticker sticker) {
                Log.d(ActDocEditor.TAG, "onDoubleTapped: double tap will be with two click");
                if (sticker instanceof TextSticker) {
                    addTextDialog(((TextSticker) sticker).getText(), true);
                }
            }
        });
    }

    private void changeSaturation() {
        iv_editImg.getSource().setImageBitmap(UtilAdjust.changeBitmapSaturation(contrast, brightness, UtilAdjust.getConvertedValue(saturation), Constant.original));
    }

    private void changeContrast() {
        iv_editImg.getSource().setImageBitmap(UtilAdjust.changeBitmapContrastBrightness(Constant.original, contrast, brightness));
    }

    private void changeExposure(int i) {
        iv_editImg.getSource().setColorFilter(new PorterDuffColorFilter(Color.argb(i, 255, 255, 255), PorterDuff.Mode.SRC_OVER));
    }

    private void setSignatureAdapter() {
        signatureList.clear();
        setSignatureList();
        if (signatureList.size() > 0) {
            rv_signature.setVisibility(View.VISIBLE);
            tv_no_signature.setVisibility(View.GONE);
        } else {
            tv_no_signature.setVisibility(View.VISIBLE);
            rv_signature.setVisibility(View.GONE);
        }
        Collections.sort(signatureList);
        Collections.reverse(signatureList);
        rv_signature.setHasFixedSize(true);
        rv_signature.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        signatureAdapter = new AdapterSignature(this, signatureList);
        rv_signature.setAdapter(signatureAdapter);
    }

    public void onDeleteSignature(int i) {
        File file = new File(signatureList.get(i));
        if (file.exists()) {
            file.delete();
        }
        signatureList.remove(i);
        signatureAdapter.notifyDataSetChanged();
        if (signatureList.size() == 0) {
            rv_signature.setVisibility(View.GONE);
            tv_no_signature.setVisibility(View.VISIBLE);
        }
    }

    public void onClickSignature(int i) {
        stickerView.addSticker(new DrawableSticker((Drawable) new BitmapDrawable(getResources(), getBitmap(new File(signatureList.get(i))))), 1);
        rl_signature.setVisibility(View.GONE);
        sb_opacity.setProgress(255);
        slideUpAnimation(ly_opacity);
    }

    private void setSignatureList() {
        File[] listFiles = new File(getExternalFilesDir((String) null).getAbsolutePath() + "/Signature").listFiles();
        if (listFiles != null) {
            for (int length = listFiles.length - 1; length >= 0; length--) {
                String file = listFiles[length].toString();
                File file2 = new File(file);
                if (file2.length() <= 1024) {
                    Log.i("Invalid Image", "Delete Image");
                } else if (file2.toString().contains(".jpeg") || file2.toString().contains(".jpg") || file2.toString().contains(".png")) {
                    signatureList.add(file);
                }
                System.out.println(file);
            }
            return;
        }
        System.out.println("Empty Folder");
    }

    private void setEditToolsAdapter() {
        rv_edit_tools.setHasFixedSize(true);
        rv_edit_tools.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        editToolsAdapter = new AdapterEditTools(this);
        rv_edit_tools.setAdapter(editToolsAdapter);
    }

    public void addTextDialog(String str, boolean isText) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_input_text_act);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActDocEditor.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText editText = (EditText) dialog.findViewById(R.id.et_input_txt);
        TextView textView =  dialog.findViewById(R.id.tv_cancel);
        TextView textView2 =  dialog.findViewById(R.id.tv_done);
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(2, 1);
        }
        editText.setTypeface(defaultFont);
        editText.setText(str);
        editText.setSelection(editText.getText().length());
//        editText.setTextColor(defaultTxtColor);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.requestFocus();
            }
        });


        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() >= 1) {
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                    if (isText) {
                        defaultText = editText.getText().toString();
                        dialog.dismiss();
                        int currentTextColor = editText.getCurrentTextColor();
                        documentEditorActivity.ChangeTextSticker(currentTextColor, documentEditorActivity, documentEditorActivity.stickerView);
                    } else {
                        defaultText = editText.getText().toString();
                        dialog.dismiss();
                        int currentTextColor2 = editText.getCurrentTextColor();
                        documentEditorActivity.addTextSticker(currentTextColor2, documentEditorActivity, documentEditorActivity.stickerView);
                    }
                    if (ly_text.getVisibility() == View.GONE) {
                        ly_edit_tools.setVisibility(View.INVISIBLE);
                        iv_done.setVisibility(View.INVISIBLE);

                        documentEditorActivity.slideUpAnimation(documentEditorActivity.ly_text);
                        return;
                    }
                    return;
                }
                Toast.makeText(ActDocEditor.this, getResources().getString(R.string.please_enter_text), Toast.LENGTH_SHORT).show();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                if (ly_text.getVisibility() == View.VISIBLE) {
                    rv_font.setVisibility(View.GONE);
                    rl_txt_color.setVisibility(View.GONE);
                    ly_alignment.setVisibility(View.GONE);
                    ly_edit_tools.setVisibility(View.VISIBLE);
                    iv_done.setVisibility(View.VISIBLE);
                    documentEditorActivity.slideDownAnimation(documentEditorActivity.ly_text);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void addTextSticker(int i, Activity activity, StickerView stickerView2) {
        try {
            textSticker = new TextSticker(activity);
            textSticker.setText(defaultText);
            textSticker.setTypeface(defaultFont);
            textSticker.setTextColor(i);
            textSticker.setTextAlign(defaultAlign);
            textSticker.resizeText();
            stickerView2.addSticker(textSticker);
        } catch (Exception e) {
            Log.e(TAG, "addTextSticker: " + e);
            e.printStackTrace();
        }
    }

    public void ChangeTextSticker(int i, Activity activity, StickerView stickerView2) {
        try {
            textSticker = new TextSticker(activity);
            textSticker.setText(defaultText);
            textSticker.setTypeface(defaultFont);
            textSticker.setTextColor(i);
            textSticker.setTextAlign(defaultAlign);
            textSticker.resizeText();
            stickerView2.replace(textSticker);
            stickerView2.invalidate();
        } catch (Exception e) {
            Log.e(TAG, "changeTextSticker: " + e);
            e.printStackTrace();
        }
    }

    public void ChangeTextAlignment(Typeface typeface, Layout.Alignment alignment) {
        try {
            textSticker = new TextSticker(this);
            textSticker.setText(defaultText);
            textSticker.setTextColor(defaultTxtColor);
            textSticker.setTextAlign(alignment);
            textSticker.resizeText();
            textSticker.setTypeface(typeface);
            stickerView.replace(textSticker);
            stickerView.invalidate();
        } catch (Exception e) {
            Log.e(TAG, "changeTextSticker: " + e);
            e.printStackTrace();
        }
    }

    public void onColorFilterSeleced(Activity activity, int i) {
        switch (i) {
            case 0:
                iv_editImg.getSource().setImageBitmap(Constant.original);
                return;
            case 1:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter1(activity, Constant.original));
                return;
            case 2:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter2(activity, Constant.original));
                return;
            case 3:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter3(activity, Constant.original));
                return;
            case 4:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter4(activity, Constant.original));
                return;
            case 5:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter5(activity, Constant.original));
                return;
            case 6:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter6(activity, Constant.original));
                return;
            case 7:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter7(activity, Constant.original));
                return;
            case 8:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter8(activity, Constant.original));
                return;
            case 9:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter9(activity, Constant.original));
                return;
            case 10:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter10(activity, Constant.original));
                return;
            case 11:
                iv_editImg.getSource().setImageBitmap(colorFilter.filter11(activity, Constant.original));
                return;
            default:
                return;
        }
    }

    public void onFontClick(Typeface typeface) {
        defaultFont = typeface;
        ChangeTextSticker(defaultTxtColor, this, stickerView);
    }

    public void onWatermarkFontClick(int i) {
        watermarkFont = i;
        et_watermark_txt.setTypeface(ResourcesCompat.getFont(this, i));
    }

    public void onOverlaySelected(int i, String str) {
        if (i == 0) {
            iv_overlayImg.setVisibility(View.GONE);
            return;
        }
        iv_overlayImg.setVisibility(View.VISIBLE);
        iv_overlayImg.setImageBitmap(UtilsBitmap.getBitmapFromAsset(getApplicationContext(), str));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("SavedEditDocumentActivity")) {
                Intent intent2 = new Intent(ActDocEditor.this, ActSavedEditDoc.class);
                intent2.putExtra("edit_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_doc_name);
                intent2.putExtra("from", ActDocEditor.TAG);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            }
        }
    };

    private class signatureSave extends AsyncTask<Bitmap, Void, Bitmap> {
        Bitmap bitmap;
        ProgressDialog progressDialog;

        private signatureSave() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActDocEditor.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            if (bitmapArr.length > 0) {
                bitmap = bitmapArr[0];

                if (bitmap != null) {
                    byte[] bytes = UtilsBitmap.getBytes(bitmap);
                    File file = new File(getExternalFilesDir((String) null).getAbsolutePath(), "/Signature");
                    if (!file.exists() && !file.mkdirs()) {
                        return null;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(file.getPath());
                    sb.append(File.separator);
                    sb.append(System.currentTimeMillis() + ".jpg");
                    File file2 = new File(sb.toString());
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        fileOutputStream.write(bytes);
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Log.w(ActDocEditor.TAG, "Cannot write to " + file2, e);
                    }
                }
            }
            return bitmap;
        }

        @Override
        public void onPostExecute(Bitmap bitmap2) {
            super.onPostExecute(bitmap2);
            progressDialog.dismiss();
            stickerView.addSticker(new DrawableSticker((Drawable) new BitmapDrawable(getResources(), bitmap2)), 1);
            rl_signature.setVisibility(View.GONE);
            sb_opacity.setProgress(255);

            documentEditorActivity.slideUpAnimation(documentEditorActivity.ly_opacity);
        }
    }

    private class bitmapSaveFinal extends AsyncTask<Bitmap, Void, Bitmap> {
        String group_name;
        ProgressDialog progressDialog;

        private bitmapSaveFinal() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActDocEditor.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Constant.original = documentEditorActivity.getMainFrameBitmap(documentEditorActivity.rl_main);
            if (Constant.original == null || fromTAG.equals("IDCardPreviewActivity") || fromTAG.equals("ScannerActivity")) {
                return null;
            }
            byte[] bytes = UtilsBitmap.getBytes(Constant.original);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                Log.w(ActDocEditor.TAG, "Cannot write to " + file, e);
            }
            if (fromTAG.equals("SavedDocumentActivity")) {
                group_name = scan_doc_grp_name;
                if (Constant.inputType.equals("Group")) {
                    dbHelper.updateGroupFirstImg(scan_doc_grp_name, file.getPath());
                    dbHelper.updateGroupListDoc(scan_doc_grp_name, current_doc_name, file.getPath());
                    return null;
                }
                dbHelper.updateGroupListDoc(scan_doc_grp_name, current_doc_name, file.getPath());
                return null;
            } else if (!fromTAG.equals("SavedEditDocument")) {
                return null;
            } else {
                group_name = edited_doc_grp_name;
                if (Constant.inputType.equals("Group")) {
                    dbHelper.updateGroupFirstImg(edited_doc_grp_name, file.getPath());
                    dbHelper.updateGroupListDoc(edited_doc_grp_name, current_doc_name, file.getPath());
                    return null;
                } else if (position == 0) {
                    dbHelper.updateGroupFirstImg(edited_doc_grp_name, file.getPath());
                    dbHelper.updateGroupListDoc(edited_doc_grp_name, current_doc_name, file.getPath());
                    return null;
                } else {
                    dbHelper.updateGroupListDoc(edited_doc_grp_name, current_doc_name, file.getPath());
                    return null;
                }
            }
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            if (fromTAG.equals("IDCardPreviewActivity")) {
                setResult(-1, getIntent());
                finish();
            } else if (fromTAG.equals("ScannerActivity")) {
                setResult(-1, getIntent());
                finish();
            } else {
                if (ActSavedEditDoc.savedEditDocumentActivity != null) {
                    ActSavedEditDoc.savedEditDocumentActivity.finish();
                }
                selected_group_name = group_name;
                Constant.IdentifyActivity = "SavedEditDocumentActivity";
                AdsUtils.showGoogleInterstitialAd(ActDocEditor.this, false);
            }
        }
    }

}

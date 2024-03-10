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
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.internal.view.SupportMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.takwolf.android.aspectratio.AspectRatioLayout;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.models.ModelDB;
import com.psychoutilities.camscan.scrapbook.ConfigImageSticker;
import com.psychoutilities.camscan.scrapbook.DisplayLocal;
import com.psychoutilities.camscan.scrapbook.ConfigInterfaceSticker;
import com.psychoutilities.camscan.scrapbook.HolderViewSticker;
import com.psychoutilities.camscan.scrapbook.TextStickerConfig;
import com.psychoutilities.camscan.main_utils.UtilsBitmap;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.utils.AdsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class actPreviewIDCard extends BaseAct implements View.OnClickListener, HolderViewSticker.CallbackOnStickerSelection {
    private static final String TAG = "IDCardPreviewActivity";

    public AspectRatioLayout aspectRatioLayout;
    public int color = SupportMenu.CATEGORY_MASK;
    public HelperDB dbHelper;

    protected Bitmap finalBitmap,
            frontSide,
            backSide;


    public ImageView iv_bg_color,
            iv_done,
            iv_edit,
            iv_horizontal,
            iv_left,
            iv_right,
            iv_scrap,
            iv_vertical,
            iv_add_new,
            iv_back;

    protected LinearLayout ly_color
            ,ly_main
            ,ly_scrap_view;

    private TextView txtScrap;

    public RelativeLayout rl_main;

    public HolderViewSticker stickerHolderView;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("DocumentEditorActivity_IDCard")) {
                Intent intent2 = new Intent(actPreviewIDCard.this, ActDocEditor.class);
                intent2.putExtra("TAG", actPreviewIDCard.TAG);
                startActivityForResult(intent2, 14);
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("IDCardGalleryActivity")) {
                ImagePicker.with((Activity) actPreviewIDCard.this)
                        .setStatusBarColor("#6db2ff")
                        .setToolbarColor("#6db2ff")
                        .setBackgroundColor("#ffffff")
                        .setFolderMode(true)
                        .setFolderTitle("Gallery")
                        .setMultipleMode(true)
                        .setShowNumberIndicator(true)
                        .setAlwaysShowDoneButton(true)
                        .setMaxSize(7)
                        .setShowCamera(false)
                        .setLimitMessage("You can select up to 7 images")
                        .setRequestCode(100)
                        .start();
                Constant.IdentifyActivity = "";
            }
        }
    };

    private void init() {
        txtScrap =  findViewById(R.id.txtScrap);
        ly_main = findViewById(R.id.ly_main);
        iv_back =  findViewById(R.id.iv_back);
        iv_done =  findViewById(R.id.iv_done);
        rl_main =  findViewById(R.id.rl_main);
        iv_bg_color =  findViewById(R.id.iv_bg_color);
        iv_add_new =  findViewById(R.id.iv_add_new);
        ly_color = findViewById(R.id.ly_color);
        iv_scrap =  findViewById(R.id.iv_scrap);
        ly_scrap_view = findViewById(R.id.ly_scrap_view);
        iv_edit =  findViewById(R.id.iv_edit);
        iv_left =  findViewById(R.id.iv_left);
        iv_right =  findViewById(R.id.iv_right);
        iv_horizontal =  findViewById(R.id.iv_horizontal);
        iv_vertical =  findViewById(R.id.iv_vertical);
        aspectRatioLayout =  findViewById(R.id.aspectRatioLayout);
        stickerHolderView =  findViewById(R.id.stickerHolderView);
    }

    private void bindView() {
        aspectRatioLayout.setAspectRatio(3.0f, 4.0f);
        stickerHolderView.setTextStickerSelectionCallback(this);
        if (!Constant.current_camera_view.equals("ID Card") || !Constant.card_type.equals("Single")) {
            frontSide = actScanner.idcardImgList.get(0);
            backSide = actScanner.idcardImgList.get(1);
            stickerHolderView.addStickerView(new ConfigImageSticker(frontSide, ConfigInterfaceSticker.STICKERTYPE.IMAGE));
            stickerHolderView.addStickerView(new ConfigImageSticker(backSide, ConfigInterfaceSticker.STICKERTYPE.IMAGE));
        } else if (Constant.singleSideBitmap != null) {
            stickerHolderView.addStickerView(new ConfigImageSticker(Constant.singleSideBitmap, ConfigInterfaceSticker.STICKERTYPE.IMAGE));
        }
    }

    @Override
    public void onImageStickerSelected(ConfigImageSticker imageStickerConfig, boolean z) {
        iv_scrap.setImageResource(R.drawable.ic_scrap_selection);
        txtScrap.setTextColor(getResources().getColor(R.color.black));
        ly_scrap_view.setVisibility(View.VISIBLE);
        Constant.original = imageStickerConfig.getBitmapImage();
    }

    @Override
    public void onNoneStickerSelected() {
        iv_scrap.setImageResource(R.drawable.ic_scrap);
        txtScrap.setTextColor(getResources().getColor(R.color.white));
        ly_scrap_view.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_add_new:
                Constant.IdentifyActivity = "IDCardGalleryActivity";
                AdsUtils.showGoogleInterstitialAd(actPreviewIDCard.this,true);
                return;
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_done:
                aspectRatioLayout.setDrawingCacheEnabled(true);
                stickerHolderView.leaveSticker();
                iv_scrap.setImageResource(R.drawable.ic_scrap);
                txtScrap.setTextColor(getResources().getColor(R.color.white));
                ly_scrap_view.setVisibility(View.GONE);
                new iDCardSave().execute(new Bitmap[0]);
                return;
            case R.id.ly_edit:
                Constant.IdentifyActivity = "DocumentEditorActivity_IDCard";
                AdsUtils.showGoogleInterstitialAd(actPreviewIDCard.this,true);
                return;
            case R.id.ly_horizontal:
                stickerHolderView.flipStickerHorizontal(true);
                return;
            case R.id.ly_left_rotate:
                stickerHolderView.rightRotate(true);
                return;
            case R.id.ly_right_rotate:
                stickerHolderView.leftRotate(true);
                return;
            case R.id.ly_scrap:
                if (ly_scrap_view.getVisibility() == View.VISIBLE) {
                    iv_scrap.setImageResource(R.drawable.ic_scrap);
                    txtScrap.setTextColor(getResources().getColor(R.color.white));
                    ly_scrap_view.setVisibility(View.GONE);
                    return;
                } else if (ly_scrap_view.getVisibility() == View.GONE) {
                    iv_scrap.setImageResource(R.drawable.ic_scrap_selection);
                    txtScrap.setTextColor(getResources().getColor(R.color.black));
                    ly_scrap_view.setVisibility(View.VISIBLE);
                    return;
                } else {
                    return;
                }
            case R.id.ly_vertical:
                stickerHolderView.flipSticker(true);
                return;
            case R.id.ly_color:
                ColorPickerDialogBuilder.with(this).setTitle("Choose color").initialColor(color).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(10).setOnColorSelectedListener(new OnColorSelectedListener() {
                    public void onColorSelected(int i) {
                    }
                }).setPositiveButton((CharSequence) "ok", (ColorPickerClickListener) new ColorPickerClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] numArr) {
                        color = i;
                        iv_bg_color.setBackgroundColor(i);
                        ly_main.setBackgroundColor(i);
                    }
                }).setNegativeButton((CharSequence) "cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).build().show();
                return;
            default:
                return;
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (ImagePicker.shouldHandleResult(i, i2, intent, 100)) {
            Iterator<Image> it = ImagePicker.getImages(intent).iterator();
            while (it.hasNext()) {
                Image next = it.next();
                if (Build.VERSION.SDK_INT >= 29) {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getUri()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            Constant.original = bitmap;
                            stickerHolderView.addStickerView(new ConfigImageSticker(bitmap, ConfigInterfaceSticker.STICKERTYPE.IMAGE));
                        }
                    });
                } else {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getPath()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            Constant.original = bitmap;
                            stickerHolderView.addStickerView(new ConfigImageSticker(bitmap, ConfigInterfaceSticker.STICKERTYPE.IMAGE));
                        }
                    });
                }
            }
        }
        if (i2 == -1 && i == 14) {
            stickerHolderView.setEditImageOnSticker(Constant.original);
        }
    }
    @Override
    public void onTextStickerSelected(TextStickerConfig textStickerConfig, boolean z) {
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_IDCard"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".IDCardGalleryActivity"));
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
        DisplayLocal.init(this);
        setContentView(R.layout.idcard_preview_act);
        dbHelper = new HelperDB(this);
        AdsUtils.loadGoogleInterstitialAd(this, actPreviewIDCard.this);
        init();
        bindView();
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.exit_dailog_editor_screen_act);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ((TextView) dialog.findViewById(R.id.iv_exit)).setOnClickListener(new View.OnClickListener() {
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

    private class iDCardSave extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private iDCardSave() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(actPreviewIDCard.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            finalBitmap = aspectRatioLayout.getDrawingCache();
            finalBitmap = getMainFrameBitmap(rl_main);
            if (finalBitmap == null) {
                return null;
            }
            byte[] bytes = UtilsBitmap.getBytes(finalBitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                Log.w(actPreviewIDCard.TAG, "Cannot write to " + file, e);
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "DocScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new ModelDB(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = ActGroupDoc.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();

            Intent intent2 = new Intent(actPreviewIDCard.this, ActGroupDoc.class);
            intent2.putExtra("current_group", group_name);
            startActivity(intent2);
            Constant.IdentifyActivity = "";

            finish();
        }
    }
}

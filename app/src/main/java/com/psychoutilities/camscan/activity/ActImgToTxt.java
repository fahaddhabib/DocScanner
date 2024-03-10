package com.psychoutilities.camscan.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.utils.AdsUtils;

import org.bouncycastle.i18n.TextBundle;

public class ActImgToTxt extends BaseAct implements View.OnClickListener {
    private static final String TAG = "ImageToTextActivity";
    protected ImageView iv_back,
            iv_copy_txt,
            iv_preview_img,
            iv_rescan_img,
            iv_share_txt;
    public ProgressDialog progressDialog;
    public TextView tv_ocr_txt, tv_title;
    private AdView adView;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView( R.layout.img_to_text_act);

        init();
        bindView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_copy_txt:
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(TextBundle.TEXT_ENTRY, tv_ocr_txt.getText().toString()));
                Toast.makeText(this, getResources().getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                return;
            case R.id.iv_rescan_img:
                tv_ocr_txt.setText("");
                doOCR(Constant.original);
                return;
            case R.id.iv_share_txt:
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/*");
                intent.putExtra("android.intent.extra.SUBJECT", "OCR Text");
                intent.putExtra("android.intent.extra.TEXT", tv_ocr_txt.getText().toString());
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_text_using)));
                return;
            default:
                return;
        }
    }

    private void init() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_rescan_img = (ImageView) findViewById(R.id.iv_rescan_img);
        iv_preview_img = (ImageView) findViewById(R.id.iv_preview_img);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_share_txt = (ImageView) findViewById(R.id.iv_share_txt);
        iv_copy_txt = (ImageView) findViewById(R.id.iv_copy_txt);
        tv_ocr_txt = (TextView) findViewById(R.id.tv_ocr_txt);

        adView  = findViewById(R.id.adView);
        AdsUtils.showGoogleBannerAd(this, adView);
    }

    private void bindView() {
        tv_title.setText(getIntent().getStringExtra("group_name"));
        iv_preview_img.setImageBitmap(Constant.original);
        doOCR(Constant.original);
    }

    private void doOCR(final Bitmap bitmap) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, "Processing", "Doing OCR...", true);
        } else {
            progressDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextRecognizer build = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!build.isOperational()) {
                    Log.e(ActImgToTxt.TAG, "Detector dependencies not loaded yet");
                    return;
                }
                final SparseArray<TextBlock> detect = build.detect(new Frame.Builder().setBitmap(bitmap).build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (detect.size() != 0) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < detect.size(); i++) {
                                sb.append(((TextBlock) detect.valueAt(i)).getValue());
                                sb.append(" ");
                            }
                            tv_ocr_txt.setText(sb.toString());
                        } else {
                            tv_ocr_txt.setText(getResources().getString(R.string.no_Text_found));
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
}

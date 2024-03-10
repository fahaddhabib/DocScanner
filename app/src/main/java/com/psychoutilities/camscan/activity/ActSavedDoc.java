package com.psychoutilities.camscan.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.utils.AdsAdmob;
import com.psychoutilities.camscan.utils.AdsUtils;


public class ActSavedDoc extends BaseAct implements View.OnClickListener {
    private static final String TAG = "SavedDocumentActivity";

    public String current_doc_name;

    public HelperDB dataBaseHelper;
    protected ImageView iv_back, iv_delete, iv_edit, iv_preview_saved, iv_retake, iv_rotate;

    public String preview_doc_grp_name;
    private AdView adView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.saved_document_act);
        dataBaseHelper = new HelperDB(this);

        init();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("ScannerActivity_Retake2")) {
                startActivity(new Intent(ActSavedDoc.this, actScanner.class));
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("DocumentEditorActivity_Saved")) {
                Intent intent2 = new Intent(ActSavedDoc.this, ActDocEditor.class);
                intent2.putExtra("TAG", ActSavedDoc.TAG);
                intent2.putExtra("scan_doc_group_name", preview_doc_grp_name);
                intent2.putExtra("current_doc_name", current_doc_name);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            }
        }
    };

    private void init() {
        adView = findViewById(R.id.adView);

        AdsUtils.showGoogleBannerAd(this, adView);
        AdsUtils.loadGoogleInterstitialAd(this, ActSavedDoc.this);

        iv_back = findViewById(R.id.iv_back);
        iv_preview_saved = findViewById(R.id.iv_preview_saved);

        iv_retake = findViewById(R.id.iv_retake);
        iv_edit = findViewById(R.id.iv_edit);
        iv_rotate = findViewById(R.id.iv_rotate);
        iv_delete = findViewById(R.id.iv_delete);
        if (Constant.original != null) {
            iv_preview_saved.setImageBitmap(Constant.original);
        }
        preview_doc_grp_name = getIntent().getStringExtra("scan_doc_group_name");
        current_doc_name = getIntent().getStringExtra("current_doc_name");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.llDelete:
                final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.dialog_delete_document_act);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                if (AdsAdmob.SHOW_ADS) {
                    AdsAdmob.loadNativeAds(ActSavedDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }
                ((TextView) dialog.findViewById(R.id.tv_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.inputType.equals("Group")) {
                            dataBaseHelper.deleteGroup(preview_doc_grp_name);
                        } else {
                            dataBaseHelper.deleteSingleDoc(preview_doc_grp_name, current_doc_name);
                        }
                        dialog.dismiss();
                        finish();
                    }
                });
                ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return;
            case R.id.llEdit:
                Constant.IdentifyActivity = "DocumentEditorActivity_Saved";
                AdsUtils.showGoogleInterstitialAd(ActSavedDoc.this,true);
                return;
            case R.id.llRetake:
                Constant.IdentifyActivity = "ScannerActivity_Retake2";
                AdsUtils.showGoogleInterstitialAd(ActSavedDoc.this,true);
                return;
            case R.id.llRotate:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                iv_preview_saved.setImageBitmap(Constant.original);
                return;
            default:
                return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity_Retake2"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_Saved"));
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


}

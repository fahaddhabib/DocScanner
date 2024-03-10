package com.psychoutilities.camscan.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdView;
import com.scanlibrary.ScanActivity;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.main_utils.UtilsBitmap;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.models.ModelDB;
import com.psychoutilities.camscan.utils.AdsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActCurrentFilter extends BaseAct implements View.OnClickListener {

    public String current_docs_name, selected_group_name;
    public PhotoView iv_preview_filter;
    public Bitmap original, tempBitmap;
    private ProgressDialog progressDialog;
    private AdView adView;
    public HelperDB dataBaseHelper;
    protected ImageView iv_done, iv_back;
    protected TextView iv_ocv_black, iv_sharp_black, iv_original, iv_color;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.current_filter_act);
        dataBaseHelper = new HelperDB(this);
        SharedPreferences adsPref = getPreferences(Context.MODE_PRIVATE);

        init();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("SavedDocumentActivity")) {
                Intent intent2 = new Intent(ActCurrentFilter.this, ActSavedDoc.class);
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("IDCardPreviewActivity2")) {
                startActivity(new Intent(ActCurrentFilter.this, actPreviewIDCard.class));
                Constant.IdentifyActivity = "";
                finish();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_color:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getMagicColorBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_filter.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_filter.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_color.setTextColor(getResources().getColor(R.color.white));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_done:
                if (!Constant.current_camera_view.equals("ID Card") || !Constant.card_type.equals("Single")) {
                    Constant.original = tempBitmap;
                    if (Constant.original == null) {
                        Constant.original = original;
                    }
                    new groupInsert().execute(new Bitmap[]{Constant.original});
                    return;
                }
                Constant.singleSideBitmap = tempBitmap;
                if (Constant.singleSideBitmap == null) {
                    Constant.singleSideBitmap = original;
                }
                Constant.IdentifyActivity = "IDCardPreviewActivity2";
                AdsUtils.showGoogleInterstitialAd(ActCurrentFilter.this, true);
                return;
            case R.id.iv_ocv_black:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getBWBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_filter.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_filter.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });

                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.white));

                return;
            case R.id.iv_original:
                try {
                    showProgressDialog();
                    tempBitmap = original;
                    iv_preview_filter.setImageBitmap(original);
                    dismissProgressDialog();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }

                iv_original.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_original.setTextColor(getResources().getColor(R.color.white));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            case R.id.iv_sharp_black:
                showProgressDialog();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tempBitmap = ScanActivity.getGrayBitmap(original);
                        } catch (OutOfMemoryError e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tempBitmap = original;
                                    iv_preview_filter.setImageBitmap(original);
                                    e.printStackTrace();
                                    dismissProgressDialog();
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_preview_filter.setImageBitmap(tempBitmap);
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                iv_original.setBackgroundResource(R.drawable.filter_bg);
                iv_original.setTextColor(getResources().getColor(R.color.black));

                iv_color.setBackgroundResource(R.drawable.filter_bg);
                iv_color.setTextColor(getResources().getColor(R.color.black));

                iv_sharp_black.setBackgroundResource(R.drawable.filter_selection_bg);
                iv_sharp_black.setTextColor(getResources().getColor(R.color.white));

                iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
                iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
                return;
            default:
                return;
        }
    }

    private void init() {
        adView = findViewById(R.id.adView);
        iv_back = findViewById(R.id.iv_back);
        iv_done = findViewById(R.id.iv_done);
        iv_preview_filter = findViewById(R.id.iv_preview_filter);
        iv_original = findViewById(R.id.iv_original);
        iv_color = findViewById(R.id.iv_color);
        iv_sharp_black = findViewById(R.id.iv_sharp_black);
        iv_ocv_black = findViewById(R.id.iv_ocv_black);
        if (Constant.original != null) {
            original = Constant.original;
            iv_preview_filter.setImageBitmap(original);
        }

        AdsUtils.loadGoogleInterstitialAd(this, ActCurrentFilter.this);
        AdsUtils.showGoogleBannerAd(this, adView);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Applying Filter...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".SavedDocumentActivity"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".IDCardPreviewActivity2"));
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


    private class groupInsert extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private groupInsert() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActCurrentFilter.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = Constant.original;
            if (bitmap == null) {
                return null;
            }
            byte[] bytes = UtilsBitmap.getBytes(bitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "DocScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dataBaseHelper.createDocTable(group_name);
                dataBaseHelper.addGroup(new ModelDB(group_name, group_date, file.getPath(), Constant.current_tag));
                dataBaseHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = ActGroupDoc.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dataBaseHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = this.current_doc_name;
            Constant.IdentifyActivity = "SavedDocumentActivity";
            AdsUtils.showGoogleInterstitialAd(ActCurrentFilter.this, true);
        }
    }


}

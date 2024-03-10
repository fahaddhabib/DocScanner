package com.psychoutilities.camscan.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.text.pdf.PdfObject;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.adapter.AdapterGroupDoc;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.models.ModelDB;
import com.psychoutilities.camscan.utils.AdsAdmob;
import com.psychoutilities.camscan.utils.AdsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class ActGroupDoc extends BaseAct implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "GroupDocumentActivity";
    public String selected_group_name, singleDoc;
    public static ArrayList<ModelDB> currentGroupList = new ArrayList<>();
    public static ActGroupDoc groupDocumentActivity;
    public HelperDB dataBaseHelper;
    protected AdapterGroupDoc groupDocAdapter;
    protected ImageView iv_back, iv_create_pdf, iv_doc_camera, iv_doc_more;
    private LinearLayout ly_doc_camera;
    public Uri pdfUri;
    public RecyclerView rv_group_doc;
    public static String current_group;
    public int selected_position;
    public ArrayList<Bitmap> singleBitmap = new ArrayList<>();
    public TextView tv_title;
    private AdView adView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.group_document_act);
        groupDocumentActivity = this;

        dataBaseHelper = new HelperDB(this);
        current_group = getIntent().getStringExtra("current_group");
        init();
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("PDFViewerActivity2")) {
                Intent intent2 = new Intent(ActGroupDoc.this, ActPDFViewer.class);
                intent2.putExtra("title", selected_group_name + ".pdf");
                intent2.putExtra("pdf_path", pdfUri.toString());
                startActivity(intent2);
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("DocumentGalleryActivity")) {
                ImagePicker.with((Activity) ActGroupDoc.this)
                        .setStatusBarColor("#6db2ff")
                        .setToolbarColor("#6db2ff")
                        .setBackgroundColor("#ffffff")
                        .setFolderMode(true)
                        .setFolderTitle("Gallery")
                        .setMultipleMode(true)
                        .setShowNumberIndicator(true)
                        .setAlwaysShowDoneButton(true)
                        .setMaxSize(1)
                        .setShowCamera(false)
                        .setLimitMessage("You can select up to 1 images")
                        .setRequestCode(100)
                        .start();
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("CropDocumentActivity4")) {
                startActivity(new Intent(ActGroupDoc.this, ActDocumentCrop.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("ScannerActivity2")) {
                startActivity(new Intent(ActGroupDoc.this, actScanner.class));
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("SavedDocumentPreviewActivity")) {
                Intent intent3 = new Intent(ActGroupDoc.this, ActSavedDocPreview.class);
                intent3.putExtra("edit_doc_group_name", ActGroupDoc.current_group);
                intent3.putExtra("current_doc_name", ActGroupDoc.currentGroupList.get(selected_position).getGroup_doc_name());
                intent3.putExtra("position", selected_position);
                intent3.putExtra("from", ActGroupDoc.TAG);
                startActivity(intent3);
                Constant.IdentifyActivity = "";
            }
        }
    };

    public void shareGroupDocList(final String str) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.group_doc_share_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        ((RelativeLayout) dialog.findViewById(R.id.rl_share_pdf)).setOnClickListener(view -> {
            new pdfShareAs(PdfObject.TEXT_PDFDOCENCODING, "", "", str, "all").execute(new String[0]);
            dialog.dismiss();
        });
        ((RelativeLayout) dialog.findViewById(R.id.rl_share_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (str.equals("Multiple")) {
                    ArrayList<ModelDB> groupDocs = dataBaseHelper.getGroupDocs(ActGroupDoc.current_group.replace(" ", ""));
                    if (groupDocs.size() > 0) {
                        ArrayList<Uri> arrayList = new ArrayList<>();
                        for (ModelDB groupDoc : groupDocs) {
                            arrayList.add(BaseAct.getURIFromFile(groupDoc.getGroup_doc_img(), ActGroupDoc.this));
                        }
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.SEND_MULTIPLE");
                        intent.setType("image/*");
                        intent.putExtra("android.intent.extra.STREAM", arrayList);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra("android.intent.extra.SUBJECT", ActGroupDoc.current_group);
                        startActivity(Intent.createChooser(intent, (CharSequence) null));
                    } else {
                        Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.noDocumentFound), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Uri uRIFromFile = BaseAct.getURIFromFile(singleDoc, ActGroupDoc.this);
                    Intent intent2 = new Intent();
                    intent2.setAction("android.intent.action.SEND");
                    intent2.setType("image/*");
                    intent2.putExtra("android.intent.extra.STREAM", uRIFromFile);
                    intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    intent2.putExtra("android.intent.extra.SUBJECT", ActGroupDoc.current_group);
                    startActivity(Intent.createChooser(intent2, (CharSequence) null));
                }
                dialog.dismiss();
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.rl_share_pdf_pswrd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePDFWithPassword(str, "share", "");
                dialog.dismiss();
            }
        });
        ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void sharePDFWithPassword(String str, String saveOrShare, String name) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.pdf_pswrd_set_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText et_enter_pass = (EditText) dialog.findViewById(R.id.et_enter_pswrd);
        final ImageView iv_pass_show = (ImageView) dialog.findViewById(R.id.iv_enter_pswrd_show);
        final ImageView iv_pass_hide = (ImageView) dialog.findViewById(R.id.iv_enter_pswrd_hide);
        final EditText et_confirm_pass = (EditText) dialog.findViewById(R.id.et_confirm_pswrd);
        final ImageView iv_confirm_pass_show = (ImageView) dialog.findViewById(R.id.iv_confirm_pswrd_show);
        final ImageView iv_confirm_pass_hide = (ImageView) dialog.findViewById(R.id.iv_confirm_pswrd_hide);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/inter_medium.ttf");
        et_enter_pass.setTypeface(typeface);
        et_confirm_pass.setTypeface(typeface);

        et_enter_pass.setInputType(129);
        et_confirm_pass.setInputType(129);
        iv_pass_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_pass_show.setVisibility(View.GONE);
                iv_pass_hide.setVisibility(View.VISIBLE);
                et_enter_pass.setTransformationMethod(new HideReturnsTransformationMethod());
                et_enter_pass.setSelection(et_enter_pass.getText().length());
            }
        });
        iv_pass_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_pass_show.setVisibility(View.VISIBLE);
                iv_pass_hide.setVisibility(View.GONE);
                et_enter_pass.setTransformationMethod(new PasswordTransformationMethod());
                et_enter_pass.setSelection(et_enter_pass.getText().length());
            }
        });
        iv_confirm_pass_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_confirm_pass_show.setVisibility(View.GONE);
                iv_confirm_pass_hide.setVisibility(View.VISIBLE);
                et_confirm_pass.setTransformationMethod(new HideReturnsTransformationMethod());
                et_confirm_pass.setSelection(et_enter_pass.getText().length());
            }
        });
        iv_confirm_pass_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_confirm_pass_show.setVisibility(View.VISIBLE);
                iv_confirm_pass_hide.setVisibility(View.GONE);
                et_confirm_pass.setTransformationMethod(new PasswordTransformationMethod());
                et_confirm_pass.setSelection(et_enter_pass.getText().length());
            }
        });

        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_enter_pass.getText().toString().equals("") || et_confirm_pass.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                } else if (!et_enter_pass.getText().toString().equals(et_confirm_pass.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_confirm), Toast.LENGTH_LONG).show();
                } else if (saveOrShare.equals("share")) {
                    new pdfShareAs("PDF With Password", et_enter_pass.getText().toString(), "", str, "all").execute(new String[0]);
                    dialog.dismiss();
                } else {
                    new pdfSaveAs("PDF With Password", et_enter_pass.getText().toString(), name).execute(new String[0]);
                    dialog.dismiss();
                }
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class pdfShareAs extends AsyncTask<String, Void, String> {
        String call;
        String inputType;
        String mailId;
        String password;
        ProgressDialog progressDialog;
        String shareType;

        private pdfShareAs(String str, String str2, String str3, String str4, String str5) {
            inputType = str;
            password = str2;
            mailId = str3;
            call = str4;
            shareType = str5;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActGroupDoc.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            if (call.equals("Multiple")) {
                ArrayList<ModelDB> groupDocs = dataBaseHelper.getGroupDocs(ActGroupDoc.current_group.replace(" ", ""));
                ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
                Iterator<ModelDB> it = groupDocs.iterator();
                while (it.hasNext()) {
                    ModelDB next = it.next();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        arrayList.add(BitmapFactory.decodeStream(new FileInputStream(next.getGroup_doc_img()), (Rect) null, options));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputType.equals(PdfObject.TEXT_PDFDOCENCODING)) {
                    createPDFfromBitmap(ActGroupDoc.current_group, arrayList, "temp");
                } else {
                    createProtectedPDFfromBitmap(ActGroupDoc.current_group, arrayList, password, "temp");
                }
            } else if (inputType.equals(PdfObject.TEXT_PDFDOCENCODING)) {
                createPDFfromBitmap(ActGroupDoc.current_group, singleBitmap, "temp");
            } else {
                createProtectedPDFfromBitmap(ActGroupDoc.current_group, singleBitmap, password, "temp");
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            Uri uRIFromFile = BaseAct.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + ActGroupDoc.current_group + ".pdf", ActGroupDoc.this);
            if (shareType.equals("gmail")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("application/pdf");
                intent.putExtra("android.intent.extra.STREAM", uRIFromFile);
                intent.putExtra("android.intent.extra.SUBJECT", ActGroupDoc.current_group);
                intent.putExtra("android.intent.extra.EMAIL", new String[]{mailId});
                intent.setPackage("com.google.android.gm");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent createChooser = Intent.createChooser(intent, (CharSequence) null);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                startActivity(createChooser);
                return;
            }
            Intent intent2 = new Intent();
            intent2.setAction("android.intent.action.SEND");
            intent2.setType("application/pdf");
            intent2.putExtra("android.intent.extra.STREAM", uRIFromFile);
            intent2.putExtra("android.intent.extra.SUBJECT", ActGroupDoc.current_group);
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{mailId});
            intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent createChooser2 = Intent.createChooser(intent2, (CharSequence) null);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startActivity(createChooser2);
        }
    }

    private void updateGroupName(final String str) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.group_name_update_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText editText = (EditText) dialog.findViewById(R.id.et_group_name);
        editText.setText(str);
        editText.setSelection(editText.length());
        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                    Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.valid_doc), Toast.LENGTH_SHORT).show();
                    return;
                }
                dataBaseHelper.updateGroupName(ActGroupDoc.this, str, editText.getText().toString().trim());
                dialog.dismiss();
                ActGroupDoc.current_group = editText.getText().toString();
                tv_title.setText(ActGroupDoc.current_group);
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void sendTomail(String str, String str2) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_enter_email_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText editText = (EditText) dialog.findViewById(R.id.et_emailId);
        final Dialog dialog2 = dialog;
        final String str3 = str;
        final String str4 = str2;
        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    dialog2.dismiss();
                } else if (!editText.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_email_address), Toast.LENGTH_SHORT).show();
                } else {
                    new pdfShareAs(PdfObject.TEXT_PDFDOCENCODING, "", editText.getText().toString(), str3, str4).execute(new String[0]);
                    dialog2.dismiss();
                }
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onClickSingleDoc(int i) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Constant.original = BitmapFactory.decodeStream(new FileInputStream(currentGroupList.get(i).getGroup_doc_img()), (Rect) null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Constant.inputType = "GroupItem";
        selected_position = i;
        Constant.IdentifyActivity = "SavedDocumentPreviewActivity";
        AdsUtils.showGoogleInterstitialAd(ActGroupDoc.this, true);

    }

    public void onClickItemMore(final int i, String str) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(currentGroupList.get(i).getGroup_doc_img()), (Rect) null, options);
            singleBitmap.clear();
            singleBitmap.add(decodeStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        singleDoc = currentGroupList.get(i).getGroup_doc_img();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(getApplicationContext(), R.layout.dailog_groupdoc_bottomsheet_act, (ViewGroup) null);
        final TextView tv_dialog_title = (TextView) inflate.findViewById(R.id.tv_dialog_title);
        final LinearLayout dialog_main = (LinearLayout) inflate.findViewById(R.id.dialog_main);
        final LinearLayout ll_top = (LinearLayout) inflate.findViewById(R.id.ll_top);
        final ScrollView scrLayout = (ScrollView) inflate.findViewById(R.id.scrLayout);
        final TextView txtSavePdf = (TextView) inflate.findViewById(R.id.txtSavePdf);
        final TextView txtShare = (TextView) inflate.findViewById(R.id.txtShare);
        final TextView txtGallery = (TextView) inflate.findViewById(R.id.txtGallery);
        final TextView txtEmail = (TextView) inflate.findViewById(R.id.txtEmail);
        final TextView txtDelete = (TextView) inflate.findViewById(R.id.txtDelete);

        final ImageView iv_share = (ImageView) inflate.findViewById(R.id.iv_share);
        final ImageView iv_delete = (ImageView) inflate.findViewById(R.id.iv_delete);

        dialog_main.setBackgroundColor(getResources().getColor(R.color.dialog_bg_color));
        scrLayout.setBackgroundColor(getResources().getColor(R.color.dialog_bg_color));
        ll_top.setBackgroundColor(getResources().getColor(R.color.dialog_bg_color));

        txtSavePdf.setTextColor(getResources().getColor(R.color.txt_color));
        txtShare.setTextColor(getResources().getColor(R.color.txt_color));
        txtGallery.setTextColor(getResources().getColor(R.color.txt_color));
        txtEmail.setTextColor(getResources().getColor(R.color.txt_color));
        txtDelete.setTextColor(getResources().getColor(R.color.txt_color));
        tv_dialog_title.setTextColor(getResources().getColor(R.color.txt_color));

        iv_delete.setColorFilter(ContextCompat.getColor(ActGroupDoc.this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
        iv_share.setColorFilter(ContextCompat.getColor(ActGroupDoc.this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        tv_dialog_title.setText(current_group);
        ((TextView) inflate.findViewById(R.id.tv_page)).setText(str);
        ((RelativeLayout) inflate.findViewById(R.id.rl_save_as_pdf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ActGroupDoc.this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.dialog_main_save_pdf_act);
                dialog.getWindow().setLayout(-1, -2);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                if (AdsAdmob.SHOW_ADS) {
                    AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }
                ((RelativeLayout) dialog.findViewById(R.id.rl_save_pdf)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveAsPDFDialog(getResources().getString(R.string.saveAsPdf), tv_dialog_title.getText().toString());
                        dialog.dismiss();
                    }
                });
                ((RelativeLayout) dialog.findViewById(R.id.rl_save_pdf_pswrd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sharePDFWithPassword("", "save", tv_dialog_title.getText().toString());
                        dialog.dismiss();
                    }
                });
                ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                bottomSheetDialog.dismiss();
            }
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareGroupDocList("Single");
                bottomSheetDialog.dismiss();
            }
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_save_to_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList arrayList = new ArrayList();
                arrayList.clear();
                arrayList.add(ActGroupDoc.currentGroupList.get(i).getGroup_doc_img());
                new saveInGallery(arrayList).execute(new String[0]);
                bottomSheetDialog.dismiss();
            }
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_send_to_mail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTomail("Single", "gmail");
                bottomSheetDialog.dismiss();
            }
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ActGroupDoc.this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.dialog_delete_document_act);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                if (AdsAdmob.SHOW_ADS) {
                    AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }
                ((TextView) dialog.findViewById(R.id.tv_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (i == 0 && ActGroupDoc.currentGroupList.size() == 2) {
                            dataBaseHelper.deleteGroup(ActGroupDoc.current_group);
                            finish();
                        } else {
                            dataBaseHelper.deleteSingleDoc(ActGroupDoc.current_group, ActGroupDoc.currentGroupList.get(i).getGroup_doc_name());
                            if (i == 0) {
                                dataBaseHelper.updateGroupFirstImg(ActGroupDoc.current_group, ActGroupDoc.currentGroupList.get(i + 1).getGroup_doc_img());
                            }
                            new setAdapterDocGroup().execute(new String[0]);
                        }
                        dialog.dismiss();
                    }
                });
                ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }

    public void saveAsPDFDialog(String str, String str2) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_sub_save_pdf_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final TextView textView = (TextView) dialog.findViewById(R.id.tv_title);
        final EditText editText = (EditText) dialog.findViewById(R.id.et_pdf_name);
        textView.setText(str);
        editText.setText(str2);
        editText.setSelection(editText.length());
        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText().toString().equals(getResources().getString(R.string.saveAsPdf))) {
                    new pdfSaveAs(PdfObject.TEXT_PDFDOCENCODING, "", editText.getText().toString()).execute(new String[0]);
                    dialog.dismiss();
                    return;
                }
                sharePDFWithPassword("", "save", editText.getText().toString());
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        tv_title.setText(current_group);
        new setAdapterDocGroup().execute(new String[0]);
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".PDFViewerActivity2"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentGalleryActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".CropDocumentActivity4"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity2"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".SavedDocumentPreviewActivity"));
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


    private void init() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_create_pdf = (ImageView) findViewById(R.id.iv_create_pdf);
        iv_doc_more = (ImageView) findViewById(R.id.iv_doc_more);
        rv_group_doc = (RecyclerView) findViewById(R.id.rv_group_doc);
        iv_doc_camera = (ImageView) findViewById(R.id.iv_doc_camera);
        ly_doc_camera = (LinearLayout) findViewById(R.id.ly_doc_camera);
        adView = findViewById(R.id.adView);
        AdsUtils.showGoogleBannerAd(this, adView);
        AdsUtils.loadGoogleInterstitialAd(this, ActGroupDoc.this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_create_pdf:
                new pdfOpenAndCreate(current_group).execute(new String[0]);
                return;
            case R.id.ly_doc_camera:
                Constant.inputType = "GroupItem";
                Constant.IdentifyActivity = "ScannerActivity2";
                AdsUtils.showGoogleInterstitialAd(ActGroupDoc.this, true);
                return;
            case R.id.iv_doc_more:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.group_doc_more);
                try {
                    Field declaredField = PopupMenu.class.getDeclaredField("mPopup");
                    declaredField.setAccessible(true);
                    Object obj = declaredField.get(popupMenu);
                    obj.getClass().getDeclaredMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{true});
                    popupMenu.show();
                    return;
                } catch (Exception e) {
                    popupMenu.show();
                    e.printStackTrace();
                    return;
                }
            default:
                return;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete:
                final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.dialog_delete_document_act);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setLayout(-1, -2);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                if (AdsAdmob.SHOW_ADS) {
                    AdsAdmob.loadNativeAds(ActGroupDoc.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
                } else {
                    dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
                }
                ((TextView) dialog.findViewById(R.id.tv_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataBaseHelper.deleteGroup(ActGroupDoc.current_group);
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
                return true;
            case R.id.import_from_gallery:
                Constant.inputType = "GroupItem";
                Constant.current_camera_view = "Document";
                Constant.IdentifyActivity = "DocumentGalleryActivity";
                AdsUtils.showGoogleInterstitialAd(ActGroupDoc.this, true);
                return true;
            case R.id.rename:
                updateGroupName(current_group);
                return true;
            case R.id.save_to_gallery:
                ArrayList<ModelDB> groupDocs = dataBaseHelper.getGroupDocs(current_group.replace(" ", ""));
                if (groupDocs.size() > 0) {
                    ArrayList arrayList = new ArrayList();
                    Iterator<ModelDB> it = groupDocs.iterator();
                    while (it.hasNext()) {
                        arrayList.add(it.next().getGroup_doc_img());
                    }
                    new saveInGallery(arrayList).execute(new String[0]);
                } else {
                    Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.noDocumentFound), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.send_to_mail:
                ArrayList<ModelDB> mailGroupDocs = dataBaseHelper.getGroupDocs(ActGroupDoc.current_group.replace(" ", ""));
                if (mailGroupDocs.size() > 0) {
                    sendTomail("Multiple", "gmail");
                } else {
                    Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.noDocumentFound), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.share:
                ArrayList<ModelDB> shareGroupDocs = dataBaseHelper.getGroupDocs(ActGroupDoc.current_group.replace(" ", ""));
                if (shareGroupDocs.size() > 0) {
                    shareGroupDocList("Multiple");
                } else {
                    Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.noDocumentFound), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        if (ImagePicker.shouldHandleResult(i, i2, intent, 100)) {
            Iterator<Image> it = ImagePicker.getImages(intent).iterator();
            while (it.hasNext()) {
                Image next = it.next();
                if (Build.VERSION.SDK_INT >= 29) {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getUri()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            if (Constant.original != null) {
                                Constant.original.recycle();
                                System.gc();
                            }
                            Constant.original = bitmap;
                            Constant.IdentifyActivity = "CropDocumentActivity4";
                            AdsUtils.showGoogleInterstitialAd(ActGroupDoc.this, true);
                        }
                    });
                } else {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getPath()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            if (Constant.original != null) {
                                Constant.original.recycle();
                                System.gc();
                            }
                            Constant.original = bitmap;
                            Constant.IdentifyActivity = "CropDocumentActivity4";
                            AdsUtils.showGoogleInterstitialAd(ActGroupDoc.this, true);
                        }
                    });
                }
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    private class pdfSaveAs extends AsyncTask<String, Void, String> {
        String inputType;
        String password;
        String pdfName;
        ProgressDialog progressDialog;

        private pdfSaveAs(String inputType, String password, String pdfName) {
            this.inputType = inputType;
            this.password = password;
            this.pdfName = pdfName;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActGroupDoc.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            if (inputType.equals(PdfObject.TEXT_PDFDOCENCODING)) {
                createPDFfromBitmap(pdfName, singleBitmap, "save");
                return null;
            }
            createProtectedPDFfromBitmap(pdfName, singleBitmap, password, "save");
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            progressDialog.dismiss();
            Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    private class saveInGallery extends AsyncTask<String, Void, String> {
        ArrayList<String> pathList;
        ProgressDialog progressDialog;

        private saveInGallery(ArrayList<String> arrayList) {
            pathList = arrayList;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActGroupDoc.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            try {
                Iterator<String> it = pathList.iterator();
                while (it.hasNext()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(it.next()), (Rect) null, options);
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/Images");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(file, System.currentTimeMillis() + ".jpg");
                    if (file2.exists()) {
                        file2.delete();
                    }
                    if (decodeStream != null) {
                        decodeStream.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file2));
                        saveImageToGallery(file2.getPath(), ActGroupDoc.this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            progressDialog.dismiss();
            Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
        }
    }


    public class pdfOpenAndCreate extends AsyncTask<String, Void, String> {
        private String group_name;
        private ProgressDialog progressDialog;
        private boolean isDocExists = false;

        public pdfOpenAndCreate(String str) {
            group_name = str;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActGroupDoc.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            new ArrayList().clear();
            ArrayList<ModelDB> groupDocs = dataBaseHelper.getGroupDocs(group_name.replace(" ", ""));
            ArrayList<Bitmap> arrayList = new ArrayList<>();
            Iterator<ModelDB> it = groupDocs.iterator();
            while (it.hasNext()) {
                ModelDB next = it.next();
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    arrayList.add(BitmapFactory.decodeStream(new FileInputStream(next.getGroup_doc_img()), (Rect) null, options));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (arrayList.size() > 0) {
                isDocExists = true;
                createPDFfromBitmap(group_name, arrayList, "temp");
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActGroupDoc.this, getResources().getString(R.string.noDocumentFound), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            if (isDocExists) {
                pdfUri = BaseAct.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + group_name + ".pdf", ActGroupDoc.this);
                selected_group_name = group_name;
                Constant.IdentifyActivity = "PDFViewerActivity2";
                AdsUtils.showGoogleInterstitialAd(ActGroupDoc.this, true);
            }
            progressDialog.dismiss();
        }
    }

    public class setAdapterDocGroup extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        public setAdapterDocGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActGroupDoc.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            ActGroupDoc.currentGroupList.clear();
            ActGroupDoc.currentGroupList = dataBaseHelper.getGroupDocs(ActGroupDoc.current_group.replace(" ", ""));
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            if (currentGroupList.size() > 0) {
                ly_doc_camera.setVisibility(View.GONE);
                currentGroupList.add(currentGroupList.size(), new ModelDB());
                rv_group_doc.setHasFixedSize(true);
                rv_group_doc.setLayoutManager(new GridLayoutManager((Context) ActGroupDoc.this, 2, RecyclerView.VERTICAL, false));
                groupDocAdapter = new AdapterGroupDoc(groupDocumentActivity, currentGroupList);
                rv_group_doc.setAdapter(groupDocAdapter);
            } else {
                ly_doc_camera.setVisibility(View.VISIBLE);
//                Toast.makeText(getApplicationContext(), "Something Went Wrong...", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

}
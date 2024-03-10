package com.psychoutilities.camscan.activity;

import static com.psychoutilities.camscan.main_utils.Constant.appLangSessionManager;
import static com.psychoutilities.camscan.utils.AppSettings.getPortNumber;
import static com.psychoutilities.camscan.utils.Utility.getLocalIpAddress;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.pdf.PdfObject;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.permissionx.guolindev.PermissionX;
import com.psycho.iap.DataWrappers;
import com.psycho.iap.IapConnector;
import com.psycho.iap.SubscriptionServiceListener;
import com.psychoutilities.camscan.MyApplication;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.adapter.adapterAllGroup;
import com.psychoutilities.camscan.adapter.AdapterDrawerItem;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.main_utils.AppLangSessionManager;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.models.ModelDB;
import com.psychoutilities.camscan.models.ModelDrawer;
import com.psychoutilities.camscan.service.HTTPService;
import com.psychoutilities.camscan.utils.AdsAdmob;
import com.psychoutilities.camscan.utils.AdsUtils;
import com.psychoutilities.camscan.utils.AppSettings;
import com.psychoutilities.camscan.utils.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActMain extends BaseAct implements View.OnClickListener,PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "MainActivity";
    public static ActMain mainActivity;
    protected adapterAllGroup allGroupAdapter;

    private DrawerLayout drawer_ly;
    protected SharedPreferences.Editor editor;
    private EditText et_search;
    public HelperDB dbHelper;
    protected AdapterDrawerItem drawerItemAdapter;
    private ArrayList<ModelDrawer> drawerList = new ArrayList<>();
    private static final float END_SCALE = 0.7f;
    private View contentView;
    public ArrayList<ModelDB> groupList = new ArrayList<>();

    public ImageView iv_clear_txt;
    protected ImageView iv_close_search,
            iv_drawer,
            iv_wifi,
            iv_more,
            iv_search;
    protected LinearLayout ly_group_camera;

    private ListView lv_drawer;

    public LinearLayout ly_empty;
    protected LinearLayoutManager layoutManager;

    public SharedPreferences preferences;
    private RelativeLayout rl_search_bar;

    public RecyclerView rv_group;

    protected int selected_sorting_pos;
    protected String selected_sorting;
    public String current_group;
    protected String current_mode;

    private TabLayout tag_tabs;
    protected ActionBarDrawerToggle toggle;

    private IapConnector iapConnector;

    public TextView tv_empty;
    private MyBroadcastReceiver mBroadcastReceiver;
    private boolean isAdsShow;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_act_dash);
        mainActivity = this;
        dbHelper = new HelperDB(this);
        preferences = getSharedPreferences("mypref", 0);
        init();
        bindView();

        isAdsShow = MyApplication.getInstance().isShowAds();


        mBroadcastReceiver = new MyBroadcastReceiver();


        SharedPreferences adsPref = getPreferences(Context.MODE_PRIVATE);

        AdsUtils.loadGoogleInterstitialAd(mainActivity, ActMain.this);

        PermissionX.init(ActMain.this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.CAMERA)
                .explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
//                    CustomDialog customDialog = new CustomDialog(MainJavaActivity.this, "PermissionX needs following permissions to continue", deniedList);
//                    scope.showRequestReasonDialog(customDialog);
                    scope.showRequestReasonDialog(deniedList, "DocScanner needs following permissions to continue", "Allow");
                })
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "Please allow following permissions in settings", "Allow");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        Toast.makeText(ActMain.this, "All permissions are granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ActMain.this, "The following permissions are denied：" + deniedList, Toast.LENGTH_SHORT).show();
                    }
                });



        //IAP Calls
        //Products list
        List<String> nonConsumablesList = Collections.singletonList("lifetime");
        List<String> consumablesList = Arrays.asList("base", "moderate", "quite", "plenty", "yearly");
        //List<String> subsList = Collections.singletonList("monthlysubs5");


        List<String> subsList = Arrays.asList("coffee_sub", "dinner_sub", "holiday_sub");

        //connect to lib
        iapConnector = new IapConnector(
                this,
                nonConsumablesList,
                consumablesList,
                subsList,
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9vtYZYTzs+wZgFLLOCMWdB+Kkw5etNfaEPSfGj4X7HhQI4WxlmvUi1h6ZwR66nutZApYYY/aKrHx7Y5oZusCot8wuKvOhVQtX4X2H++pYf37i8+6aQwz/j27S5jBcbFkZo7a6W/gMwYKuDcVEupvSVeowRZycFezO0O1ZxEpb0DV6nABbzvyKEvgvfeWJuMFSMfQN5sYH7BxSA533iC02ne6lYzSBLEw2mgvfmNvzJmGVEHY6AG7+t+JA2jYeNqwXRgX6liCdYG3KkGNiKNa8SdQY+x2GT/q0c3jDe90dVPaAgbaWd0vlgIh2eSvG+apoa2+jRjDrmKd4WAgYzMj6QIDAQAB",
                true
        );

        //product purchase listener
        /*iapConnector.addPurchaseListener(new PurchaseServiceListener() {
            public void onPricesUpdated(@NotNull Map iapKeyPrices) {

            }

            public void onProductPurchased(DataWrappers.PurchaseInfo purchaseInfo) {
                if (purchaseInfo.getSku().equals("plenty")) {

                }
                else if (purchaseInfo.getSku().equals("moderate")) {

                }
                else if (purchaseInfo.getSku().equals("base")) {

                }
                else if (purchaseInfo.getSku().equals("quite")) {

                }
            }

            public void onProductRestored(DataWrappers.PurchaseInfo purchaseInfo) {

            }
        });*/

        //Subscription listener
        iapConnector.addSubscriptionListener(new SubscriptionServiceListener() {
            public void onSubscriptionRestored(DataWrappers.PurchaseInfo purchaseInfo) {
                if (purchaseInfo.getSku().equals("coffee_sub")||purchaseInfo.getSku().equals("dinner_sub")||purchaseInfo.getSku().equals("holiday_sub")) {
                    adsPref.edit().putBoolean("showAds",false).apply();
                    Toast.makeText(getApplicationContext(),"Sucessfully Restored!!",Toast.LENGTH_LONG).show();
                }
                else {
                    adsPref.edit().putBoolean("showAds", true).apply();
                }
                }

            public void onSubscriptionPurchased(DataWrappers.PurchaseInfo purchaseInfo) {
                if (purchaseInfo.getSku().equals("coffee_sub")||purchaseInfo.getSku().equals("dinner_sub")||purchaseInfo.getSku().equals("holiday_sub")) {
                    adsPref.edit().putBoolean("showAds",false).apply();

                    AfterpurchaseDailogue();
                 //   Toast.makeText(getApplicationContext(),"Sucessfully Supported, You live in hearts of hundered",Toast.LENGTH_LONG).show();
                }
                else {
                    adsPref.edit().putBoolean("showAds", true).apply();
                }
            }

            public void onPricesUpdated(@NotNull Map iapKeyPrices) {

            }

        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("PrivacyPolicyActivity")) {
                startActivity(new Intent(ActMain.this, ActPrivacyPolicy.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("QRGenerateActivity")) {
                startActivity(new Intent(ActMain.this, ActQRGenerate.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("QRReaderActivity")) {
                startActivity(new Intent(ActMain.this, ActQRReader.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("MainGalleryActivity")) {
                ImagePicker.with(ActMain.this)
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
            } else if (Constant.IdentifyActivity.equals("ScannerActivity")) {
                startActivity(new Intent(ActMain.this, actScanner.class));
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("GroupDocumentActivity")) {
                Intent intent2 = new Intent(ActMain.this, ActGroupDoc.class);
                intent2.putExtra("current_group", current_group);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
            } else if (Constant.IdentifyActivity.equals("CropDocumentActivity")) {
                startActivity(new Intent(ActMain.this, ActDocumentCrop.class));
                Constant.IdentifyActivity = "";
            }
            else if(intent.getAction().equals("do_something")){
                connectivityDailogue();
            }
        }
    };

    private void init() {
        drawer_ly = findViewById(R.id.drawer_ly);
        lv_drawer = findViewById(R.id.lv_drawer);
        iv_drawer = findViewById(R.id.iv_drawer);
        iv_search = findViewById(R.id.iv_search);
        iv_wifi = findViewById(R.id.iv_wifi);
        iv_more = findViewById(R.id.iv_more);
        rl_search_bar = findViewById(R.id.rl_search_bar);
        iv_close_search = findViewById(R.id.iv_close_search);
        et_search = findViewById(R.id.et_search);
        iv_clear_txt = findViewById(R.id.iv_clear_txt);
        tag_tabs = findViewById(R.id.tag_tabs);
        rv_group = findViewById(R.id.rv_group);
        ly_empty = findViewById(R.id.ly_empty);
        tv_empty = findViewById(R.id.tv_empty);
        ly_group_camera = findViewById(R.id.ly_group_camera);
    }
    public void clickOnListItem(String str) {
        current_group = str;
        Constant.IdentifyActivity = "GroupDocumentActivity";
       AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
    }

    private void openNewFolderDialog(String groupName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_create_folder_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        EditText et_folder_name = (EditText) dialog.findViewById(R.id.et_folder_name);
        String folder_name = "DocScanner" + Constant.getDateTime("_ddMMHHmmss");
        et_folder_name.setText(folder_name);

        dialog.findViewById(R.id.tv_create).setOnClickListener(view -> {
            String finalFolderName = et_folder_name.getText().toString().trim();
            if (finalFolderName.contains(" ")) {
            finalFolderName = finalFolderName.replace(" ","_");
            }

            if (!finalFolderName.isEmpty()) {
                String group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                if (groupName.isEmpty()) {        // for create new folder
                    dbHelper.createDocTable(finalFolderName);
                    dbHelper.addGroup(new ModelDB(finalFolderName, group_date, "", Constant.current_tag));
                } else {
                    dbHelper.createDocTable(finalFolderName);
                    dbHelper.addGroup(new ModelDB(finalFolderName, group_date, "", Constant.current_tag));
                    // for move new folder
                    boolean isSuccess = false;
                    ArrayList<ModelDB> allFileList = dbHelper.getGroupDocs(groupName);
                    for (int i = 0; i < allFileList.size(); i++) {
                        ModelDB newDbModel = allFileList.get(i);
                        long isMove = dbHelper.moveGroupDoc(finalFolderName, newDbModel.getGroup_doc_img(), newDbModel.getGroup_doc_name(), "Insert text here...");
                        if (isMove <= 0) {
                            isSuccess = false;
                            break;
                        } else {
                            isSuccess = true;
                        }
                    }
                    if (isSuccess) {
                        Toast.makeText(mainActivity, getResources().getString(R.string.move_successfully), Toast.LENGTH_SHORT).show();
                        dbHelper.deleteGroup(groupName);
                    }
                }
                new adapterSetAllGroup().execute(new String[0]);
                dialog.dismiss();
            } else {
                Toast.makeText(mainActivity, getResources().getString(R.string.folder_name_req), Toast.LENGTH_SHORT).show();
            }

        });
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void clickOnListMore(ModelDB dbModel, final String name, String date) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.bottomsheet_dialog_group_act, null);
        final TextView tv_dialog_title = inflate.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(name);
        ((TextView) inflate.findViewById(R.id.tv_dialog_date)).setText(date);

        RelativeLayout rl_save_as_pdf = inflate.findViewById(R.id.rl_save_as_pdf);
        RelativeLayout rl_share = inflate.findViewById(R.id.rl_share);
        RelativeLayout rl_save_to_gallery = inflate.findViewById(R.id.rl_save_to_gallery);
        RelativeLayout rl_send_to_mail = inflate.findViewById(R.id.rl_send_to_mail);
        RelativeLayout rl_move_folder = inflate.findViewById(R.id.rl_move_folder);
        if (dbModel.getGroup_first_img() != null) {
            if (dbModel.getGroup_first_img().isEmpty()) {
                rl_save_as_pdf.setVisibility(View.GONE);
                rl_share.setVisibility(View.GONE);
                rl_save_to_gallery.setVisibility(View.GONE);
                rl_send_to_mail.setVisibility(View.GONE);
                rl_move_folder.setVisibility(View.GONE);
            }
        }

        rl_move_folder.setOnClickListener(v -> {
            openMoveFolderDialog(dbModel);
            bottomSheetDialog.dismiss();
        });

        rl_save_as_pdf.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ActMain.this, R.style.ThemeWithRoundShape);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.dialog_main_save_pdf_act);
            dialog.getWindow().setLayout(-1, -2);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            if (AdsAdmob.SHOW_ADS) {
                AdsAdmob.loadNativeAds(ActMain.this, null, dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
            } else {
                dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
            }
            dialog.findViewById(R.id.rl_save_pdf).setOnClickListener(view13 -> {
                saveGroupAsPDFDialog(name, getResources().getString(R.string.saveAsPdf), tv_dialog_title.getText().toString());
                dialog.dismiss();
            });
            ((RelativeLayout) dialog.findViewById(R.id.rl_save_pdf_pswrd)).setOnClickListener(view14 -> {
                shareGroupPDFWithPswrd(name, "save", tv_dialog_title.getText().toString());
                dialog.dismiss();
            });
            ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(view15 -> dialog.dismiss());
            dialog.show();
            bottomSheetDialog.dismiss();
        });
        rl_share.setOnClickListener(view -> {
            shareGroup(name);
            bottomSheetDialog.dismiss();
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_rename)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroupName(name);
                bottomSheetDialog.dismiss();
            }
        });
        rl_save_to_gallery.setOnClickListener(view -> {
            new groupSaveToGallery(name).execute(new String[0]);
            bottomSheetDialog.dismiss();
        });
        rl_send_to_mail.setOnClickListener(view -> {
            sendTomail(name, "gmail");
            bottomSheetDialog.dismiss();
        });
        ((RelativeLayout) inflate.findViewById(R.id.rl_delete)).setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ActMain.this, R.style.ThemeWithRoundShape);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.dialog_delete_document_act);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (AdsAdmob.SHOW_ADS) {
                AdsAdmob.loadNativeAds(ActMain.this, null, dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
            } else {
                dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
            }
            dialog.findViewById(R.id.tv_delete).setOnClickListener(view1 -> {
                dbHelper.deleteGroup(name);
                new adapterSetAllGroup().execute(new String[0]);
                dialog.dismiss();
            });
            dialog.findViewById(R.id.iv_close).setOnClickListener(view12 -> dialog.dismiss());
            dialog.show();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }

    ArrayList<ModelDB> modelArrayList = new ArrayList<>();
    String selectedFolderName = "";

    private void openMoveFolderDialog(ModelDB selectedDBModel) {
        selectedFolderName = "";
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_move_folder_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        HelperDB dbHelper = new HelperDB(this);

        modelArrayList.clear();
        modelArrayList = dbHelper.getOnlyAllGroups();

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        LinearLayout ll_new_folder_add = (LinearLayout) dialog.findViewById(R.id.ll_new_folder_add);

        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "inter_medium.ttf");

        for (int i = 0; i < modelArrayList.size(); i++) {
            RadioButton rb = new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(modelArrayList.get(i).group_name);
            rb.setTypeface(createFromAsset);
            rb.setTextSize(15.0f);
            rb.setTextColor(ContextCompat.getColorStateList(mainActivity, R.color.black));
            rg.addView(rb);
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if (isChecked) {
                selectedFolderName = (String) checkedRadioButton.getText();
            }

        });
        dialog.findViewById(R.id.tv_move).setOnClickListener(view -> {
            if (!selectedFolderName.isEmpty()) {
                boolean success = false;
                ArrayList<ModelDB> allFileList = dbHelper.getGroupDocs(selectedDBModel.getGroup_name().replace(" ", ""));
                for (int i = 0; i < allFileList.size(); i++) {
                    ModelDB newDbModel = allFileList.get(i);
                    long isMove = dbHelper.moveGroupDoc(selectedFolderName, newDbModel.getGroup_doc_img(), newDbModel.getGroup_doc_name(), "Insert text here...");
                    if (isMove <= 0) {
                        success = false;
                        break;
                    } else {
                        success = true;
                    }
                }
                if (success) {
                    Toast.makeText(mainActivity, getResources().getString(R.string.move_successfully), Toast.LENGTH_SHORT).show();
                    dbHelper.deleteGroup(selectedDBModel.getGroup_name().replace(" ", ""));
                    new adapterSetAllGroup().execute();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(mainActivity, getResources().getString(R.string.select_a_folder), Toast.LENGTH_SHORT).show();
            }

        });
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());
        ll_new_folder_add.setOnClickListener(view -> {
            openNewFolderDialog(selectedDBModel.getGroup_name().replace(" ", ""));
            dialog.dismiss();
        });
        dialog.show();
    }


    public void saveGroupAsPDFDialog(String name, String title, String str3) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_sub_save_pdf_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActMain.this, null, dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final TextView textView = dialog.findViewById(R.id.tv_title);
        final EditText et_pdf_name = dialog.findViewById(R.id.et_pdf_name);
        textView.setText(title);
        et_pdf_name.setText(str3);
        et_pdf_name.setSelection(et_pdf_name.length());

        dialog.findViewById(R.id.tv_done).setOnClickListener(view -> {
            if (textView.getText().toString().equals(getResources().getString(R.string.saveAsPdf))) {
                new savePdfAsGroup(name, PdfObject.TEXT_PDFDOCENCODING, "", et_pdf_name.getText().toString()).execute(new String[0]);
                dialog.dismiss();
                return;
            }
            shareGroupPDFWithPswrd(name, "save", et_pdf_name.getText().toString());
            dialog.dismiss();
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void shareGroup(final String name) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.group_doc_share_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActMain.this, null, dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        dialog.findViewById(R.id.rl_share_pdf).setOnClickListener(view -> {
            new pdfShareAsGroup(name, PdfObject.TEXT_PDFDOCENCODING, "", "", "all").execute(new String[0]);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.rl_share_img).setOnClickListener(view -> {
            ArrayList<ModelDB> groupDocs = dbHelper.getGroupDocs(name.replace(" ", ""));
            ArrayList arrayList = new ArrayList();
            Iterator<ModelDB> it = groupDocs.iterator();
            while (it.hasNext()) {
                arrayList.add(BaseAct.getURIFromFile(it.next().getGroup_doc_img(), ActMain.this));
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND_MULTIPLE");
            intent.setType("image/*");
            intent.putExtra("android.intent.extra.STREAM", arrayList);
            intent.putExtra("android.intent.extra.SUBJECT", name);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, (CharSequence) null));
            dialog.dismiss();
        });
        dialog.findViewById(R.id.rl_share_pdf_pswrd).setOnClickListener(view -> {
            shareGroupPDFWithPswrd(name, "share", "");
            dialog.dismiss();
        });
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    public void shareGroupPDFWithPswrd(String name, String saveOrShare, String pdfName) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.pdf_pswrd_set_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActMain.this, null, dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }

        final EditText et_enter_pswrd = dialog.findViewById(R.id.et_enter_pswrd);
        final EditText et_confirm_pswrd = dialog.findViewById(R.id.et_confirm_pswrd);
        final ImageView iv_confirm_pswrd_show = dialog.findViewById(R.id.iv_confirm_pswrd_show);
        final ImageView iv_confirm_pswrd_hide = dialog.findViewById(R.id.iv_confirm_pswrd_hide);
        final ImageView iv_enter_pswrd_show = dialog.findViewById(R.id.iv_enter_pswrd_show);
        final ImageView iv_enter_pswrd_hide = dialog.findViewById(R.id.iv_enter_pswrd_hide);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/inter_medium.ttf");
        et_enter_pswrd.setTypeface(typeface);
        et_confirm_pswrd.setTypeface(typeface);

        et_enter_pswrd.setInputType(129);
        et_confirm_pswrd.setInputType(129);

        iv_enter_pswrd_show.setOnClickListener(view -> {
            iv_enter_pswrd_show.setVisibility(View.GONE);
            iv_enter_pswrd_hide.setVisibility(View.VISIBLE);
            et_enter_pswrd.setTransformationMethod(new HideReturnsTransformationMethod());
            et_enter_pswrd.setSelection(et_enter_pswrd.getText().length());
        });
        iv_enter_pswrd_hide.setOnClickListener(view -> {
            iv_enter_pswrd_show.setVisibility(View.VISIBLE);
            iv_enter_pswrd_hide.setVisibility(View.GONE);
            et_enter_pswrd.setTransformationMethod(new PasswordTransformationMethod());
            et_enter_pswrd.setSelection(et_enter_pswrd.getText().length());
        });
        iv_confirm_pswrd_show.setOnClickListener(view -> {
            iv_confirm_pswrd_show.setVisibility(View.GONE);
            iv_confirm_pswrd_hide.setVisibility(View.VISIBLE);
            et_confirm_pswrd.setTransformationMethod(new HideReturnsTransformationMethod());
            et_confirm_pswrd.setSelection(et_enter_pswrd.getText().length());
        });
        iv_confirm_pswrd_hide.setOnClickListener(view -> {
            iv_confirm_pswrd_show.setVisibility(View.VISIBLE);
            iv_confirm_pswrd_hide.setVisibility(View.GONE);
            et_confirm_pswrd.setTransformationMethod(new PasswordTransformationMethod());
            et_confirm_pswrd.setSelection(et_enter_pswrd.getText().length());
        });

        dialog.findViewById(R.id.tv_done).setOnClickListener(view -> {
            if (et_enter_pswrd.getText().toString().equals("") || et_confirm_pswrd.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            } else if (!et_enter_pswrd.getText().toString().equals(et_confirm_pswrd.getText().toString())) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_confirm), Toast.LENGTH_LONG).show();
            } else if (saveOrShare.equals("share")) {
                new pdfShareAsGroup(name, "PDF With Password", et_enter_pswrd.getText().toString(), "", "all").execute(new String[0]);
                dialog.dismiss();
            } else {
                new savePdfAsGroup(name, "PDF With Password", et_enter_pswrd.getText().toString(), pdfName).execute(new String[0]);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void updateGroupName(final String name) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.group_name_update_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActMain.this, null, dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText editText = dialog.findViewById(R.id.et_group_name);
        editText.setText(name);
        editText.setSelection(editText.length());
        dialog.findViewById(R.id.tv_done).setOnClickListener(view -> {
            if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                Toast.makeText(ActMain.this, getResources().getString(R.string.valid_doc), Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.updateGroupName(ActMain.this, name, editText.getText().toString().trim());
            dialog.dismiss();
            new adapterSetAllGroup().execute(new String[0]);
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    public void sendTomail(String name, String shareType) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_enter_email_act);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (AdsAdmob.SHOW_ADS) {
            AdsAdmob.loadNativeAds(ActMain.this, (View) null, (ViewGroup) dialog.findViewById(R.id.admob_native_container), (NativeAdView) dialog.findViewById(R.id.native_ad_view));
        } else {
            dialog.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        final EditText editText = dialog.findViewById(R.id.et_emailId);

        dialog.findViewById(R.id.tv_done).setOnClickListener(view -> {
            if (editText.getText().toString().equals("")) {
                dialog.dismiss();
            } else if (!editText.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_email_address), Toast.LENGTH_SHORT).show();
            } else {
                new pdfShareAsGroup(name, PdfObject.TEXT_PDFDOCENCODING, "", editText.getText().toString(), shareType).execute(new String[0]);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    public void onDrawerItemSelected(int i) {
        if (i == 0) {
            drawer_ly.closeDrawer(GravityCompat.START);
        } else if (i == 1) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 3);
        } else if (i == 2) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 4);
        }
        /*else if (i == 3) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            changeLanguage();
        }*/
        else if (i == 3) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            purchaseDailogue();

        }
        else if (i == 4) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            Constant.IdentifyActivity = "PrivacyPolicyActivity";
            AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
        } else if (i == 5) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            Constant.shareApp(this);
        } else if (i == 6) {
            if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
                drawer_ly.closeDrawer(GravityCompat.START);
            }
            try {
                Constant.rateApp(this);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public void changeLanguage(){

        appLangSessionManager = new AppLangSessionManager(ActMain.this);

        final BottomSheetDialog dialogSortBy = new BottomSheetDialog(ActMain.this, R.style.SheetDialog);
        dialogSortBy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSortBy.setContentView(R.layout.dialog_language);
        final TextView tv_english = dialogSortBy.findViewById(R.id.tv_english);
        final TextView tv_hindi = dialogSortBy.findViewById(R.id.tv_hindi);
        final TextView tv_cancel = dialogSortBy.findViewById(R.id.tv_cancel);
        final TextView tvArabic = dialogSortBy.findViewById(R.id.tvArabic);
        dialogSortBy.show();
        tv_english.setOnClickListener(view -> {
            setLocale("en");
            appLangSessionManager.setLanguage("en");
        });
        tv_hindi.setOnClickListener(view -> {
            setLocale("es");
            appLangSessionManager.setLanguage("es");
        });
        tvArabic.setOnClickListener(view -> {
            setLocale("ar");
            appLangSessionManager.setLanguage("ar");
        });
        tv_cancel.setOnClickListener(view -> dialogSortBy.dismiss());
    }

    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent refresh = new Intent(ActMain.this, ActMain.class);
        startActivity(refresh);
        finish();
    }


    private void bindView() {
        drawerList.add(new ModelDrawer(getResources().getString(R.string.my_documents), R.drawable.ic_my_documents));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.qrReader), R.drawable.ic_qr_reader));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.qrGenerator), R.drawable.ic_qr_generate));
        //drawerList.add(new ModelDrawer(getResources().getString(R.string.change_language), R.drawable.ic_baseline_language_24));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.remove_ads), R.drawable.ic_baseline_block_24));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.privacy_policy), R.drawable.ic_privacy_policy));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.share_app), R.drawable.ic_share_app));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.rate_us), R.drawable.ic_rate_us));
        drawerList.add(new ModelDrawer(getResources().getString(R.string.darkTheme), R.drawable.theme_light_dark));


        toggle = new ActionBarDrawerToggle(this, drawer_ly, R.string.drawer_open, R.string.drawer_close);
        drawer_ly.addDrawerListener(toggle);
        drawerItemAdapter = new AdapterDrawerItem(this, drawerList);
        lv_drawer.setAdapter(drawerItemAdapter);
        slidingDrawer();
        setTab();
    }

    private void slidingDrawer() {
        contentView = findViewById(R.id.holder);
        drawer_ly.setScrimColor(Color.TRANSPARENT);
        drawer_ly.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                           @Override
                                           public void onDrawerSlide(View drawerView, float slideOffset) {

                                               // Scale the View based on current slide offset
                                               final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                               final float offsetScale = 1 - diffScaledOffset;
                                               contentView.setScaleX(offsetScale);
                                               contentView.setScaleY(offsetScale);

                                               // Translate the View, accounting for the scaled width
                                               final float xOffset = drawerView.getWidth() * slideOffset;
                                               final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                               final float xTranslation = xOffset - xOffsetDiff;
                                               contentView.setTranslationX(xTranslation);
                                           }

                                           @Override
                                           public void onDrawerClosed(View drawerView) {
                                           }
                                       }
        );
    }

    private void setTab() {


        String[] tabList = {
                getResources().getString(R.string.all),
                getResources().getString(R.string.business_card),
                getResources().getString(R.string.id_card),
                getResources().getString(R.string.academic_docs),
                getResources().getString(R.string.personal_tag)
        };

        int[] tabListicon = {
                R.drawable.all_docs,
                R.drawable.business_doc,
                R.drawable.id_card,
                R.drawable.acedmy_docs,
                R.drawable.personal_tag
        };

/*        LinkedHashMap<String, Integer> tabTextIconList = new LinkedHashMap<String, Integer>();
        //tablist , tabIcons
        tabTextIconList.put(getResources().getString(R.string.all), R.drawable.all_docs);
        tabTextIconList.put(getResources().getString(R.string.business_card), R.drawable.business_doc);
        tabTextIconList.put(getResources().getString(R.string.id_card), R.drawable.id_card);
        tabTextIconList.put(getResources().getString(R.string.academic_docs), R.drawable.acedmy_docs);
        tabTextIconList.put(getResources().getString(R.string.personal_tag), R.drawable.personal_tag);
        */

/*
        for (Map.Entry<String, Integer> entry : tabTextIconList.entrySet()) {
            TabLayout tabLayout = tag_tabs;
            tabLayout.addTab(tabLayout.newTab().setIcon(entry.getValue())
                    .setText(entry.getKey()));
        }*/

        for(int i =0; i<5; i++){
            TabLayout tabLayout = tag_tabs;
            tabLayout.addTab(tabLayout.newTab().setIcon(tabListicon[i])
                    .setText(tabList[i]));
        }

        Constant.current_tag = "All Docs";
        tag_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(ActMain.TAG, "onTabSelected: " + Constant.current_tag);

                Constant.current_tag = tabList[tab.getPosition()];
                new adapterSetAllGroup().execute(new String[0]);
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i3 == 0) {
                    iv_clear_txt.setVisibility(View.INVISIBLE);
                } else if (i3 == 1) {
                    iv_clear_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (groupList.size() > 0) {
                    filter(editable.toString());
                }
            }
        });
    }

    public void filter(String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<ModelDB> it = groupList.iterator();
        while (it.hasNext()) {
            ModelDB next = it.next();
            if (next.getGroup_name().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(next);
            }
        }
        allGroupAdapter.filterList(arrayList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear_txt:
                et_search.setText("");
                iv_clear_txt.setVisibility(View.GONE);
                return;
            case R.id.iv_close_search:
                iv_search.setVisibility(View.VISIBLE);
                rl_search_bar.setVisibility(View.GONE);
                et_search.setText("");
                hideSoftKeyboard(et_search);
                return;
            case R.id.iv_drawer:
                drawer_ly.openDrawer(GravityCompat.START);
                return;
            case R.id.ly_group_camera:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 2);
                return;
            case R.id.iv_more:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.group_more);
                try {
                    Field declaredField = PopupMenu.class.getDeclaredField("mPopup");
                    declaredField.setAccessible(true);
                    Object obj = declaredField.get(popupMenu);
                    obj.getClass().getDeclaredMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{true});
                    popupMenu.show();
                    return;
                } catch (Exception exception) {
                    popupMenu.show();
                    return;
                }
            case R.id.iv_search:
                iv_search.setVisibility(View.GONE);
                rl_search_bar.setVisibility(View.VISIBLE);
                showSoftKeyboard(et_search);
                return;
            case R.id.iv_wifi:
/*                Intent intent = new Intent(ActMain.this, TestActivity.class);
                startActivity(intent);*/
                connectivityDailogue();
                return;
            default:
                return;
        }
    }

    @Override
    public void onResume() {
        new adapterSetAllGroup().execute(new String[0]);
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".PrivacyPolicyActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".QRGenerateActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".QRReaderActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".MainGalleryActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".GroupDocumentActivity"));

        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".CropDocumentActivity"));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("do_something");
        registerReceiver(broadcastReceiver, intentFilter);

        if (AppSettings.isServiceStarted(this)){
            connectivityDailogue();
        }
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                            Constant.IdentifyActivity = "QRGenerateActivity";
                            AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
                            return;
                        }
                        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 4);
                    }
                } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                    Constant.IdentifyActivity = "QRReaderActivity";
                    AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 3);
                }
            } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                Constant.inputType = "Group";
                Constant.IdentifyActivity = "ScannerActivity";
                AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 2);
            }
        } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
            Constant.inputType = "Group";
            Constant.IdentifyActivity = "MainGalleryActivity";
            AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.grid_view:
                editor = preferences.edit();
                editor.putString("ViewMode", "Grid");
                editor.apply();
                new adapterSetAllGroup().execute(new String[0]);
                break;
            case R.id.import_from_gallery:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
                break;
            case R.id.test_item:
                Intent intent = new Intent(ActMain.this, TestActivity.class);
                startActivity(intent);
                break;

            case R.id.create_new_folder:
                openNewFolderDialog("");
                break;
            case R.id.list_view:
                editor = preferences.edit();
                editor.putString("ViewMode", "List");
                editor.apply();
                new adapterSetAllGroup().execute(new String[0]);
                break;
            case R.id.share_all:
                new groupShareAll().execute(new String[0]);
                break;
            case R.id.sort_by:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle((CharSequence) "Sort By");
                String[] strArr = {"Ascending date", "Descending date", "Ascending name", "Descending name"};
                if (selected_sorting.equals(Constant.ascending_date)) {
                    selected_sorting_pos = 0;
                } else if (selected_sorting.equals(Constant.descending_date)) {
                    selected_sorting_pos = 1;
                } else if (selected_sorting.equals(Constant.ascending_name)) {
                    selected_sorting_pos = 2;
                } else if (selected_sorting.equals(Constant.descending_name)) {
                    selected_sorting_pos = 3;
                }
                builder.setSingleChoiceItems(strArr, selected_sorting_pos, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            mainActivity.editor = mainActivity.preferences.edit();
                            editor.putString("sortBy", Constant.ascending_date);
                            editor.apply();
                            new adapterSetAllGroup().execute(new String[0]);
                            dialogInterface.dismiss();
                        } else if (i == 1) {
                            mainActivity.editor = mainActivity.preferences.edit();
                            editor.putString("sortBy", Constant.descending_date);
                            editor.apply();
                            new adapterSetAllGroup().execute(new String[0]);
                            dialogInterface.dismiss();
                        } else if (i == 2) {
                            mainActivity.editor = mainActivity.preferences.edit();
                            editor.putString("sortBy", Constant.ascending_name);
                            editor.apply();
                            new adapterSetAllGroup().execute(new String[0]);
                            dialogInterface.dismiss();
                        } else if (i == 3) {
                            mainActivity.editor = mainActivity.preferences.edit();
                            editor.putString("sortBy", Constant.descending_name);
                            editor.apply();
                            new adapterSetAllGroup().execute(new String[0]);
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
                break;
        }
        return true;
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
                            Constant.current_camera_view = "Document";
                            Constant.original = bitmap;
                            Constant.IdentifyActivity = "CropDocumentActivity";
                            AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
                        }
                    });
                } else {
                    Glide.with(getApplicationContext()).asBitmap().load(next.getPath()).into(new SimpleTarget<Bitmap>() {
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            if (Constant.original != null) {
                                Constant.original.recycle();
                                System.gc();
                            }
                            Constant.current_camera_view = "Document";
                            Constant.original = bitmap;
                            Constant.IdentifyActivity = "CropDocumentActivity";
                            AdsUtils.showGoogleInterstitialAd(ActMain.this, false);
                        }
                    });
                }
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer_ly.isDrawerOpen(GravityCompat.START)) {
            drawer_ly.closeDrawer(GravityCompat.START);
        } else if (rl_search_bar.getVisibility() == View.VISIBLE) {
            rl_search_bar.setVisibility(View.GONE);
            iv_search.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    private class pdfShareAsGroup extends AsyncTask<String, Void, String> {
        private String group_name;
        private String inputType;
        private String mailId;
        private String password;
        private ProgressDialog progressDialog;
        private String shareType;

        private pdfShareAsGroup(String group_name, String inputType, String password, String mailId, String shareType) {
            this.group_name = group_name;
            this.inputType = inputType;
            this.password = password;
            this.mailId = mailId;
            this.shareType = shareType;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActMain.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            new ArrayList().clear();
            ArrayList<ModelDB> groupDocs = dbHelper.getGroupDocs(group_name.replace(" ", ""));
            ArrayList arrayList = new ArrayList();
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
                createPDFfromBitmap(group_name, arrayList, "temp");
            } else {
                createProtectedPDFfromBitmap(group_name, arrayList, password, "temp");
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            Uri uRIFromFile = BaseAct.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + group_name + ".pdf", ActMain.this);
            if (shareType.equals("gmail")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("application/pdf");
                intent.putExtra("android.intent.extra.STREAM", uRIFromFile);
                intent.putExtra("android.intent.extra.SUBJECT", group_name);
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
            intent2.putExtra("android.intent.extra.SUBJECT", group_name);
            intent2.putExtra("android.intent.extra.EMAIL", new String[]{mailId});
            intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent createChooser2 = Intent.createChooser(intent2, (CharSequence) null);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startActivity(createChooser2);
        }
    }

    private class savePdfAsGroup extends AsyncTask<String, Void, String> {
        String group_name;
        String inputType;
        String password;
        String pdfName;
        ProgressDialog progressDialog;

        private savePdfAsGroup(String str, String str2, String str3, String str4) {
            group_name = str;
            inputType = str2;
            password = str3;
            pdfName = str4;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActMain.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            new ArrayList().clear();
            ArrayList<ModelDB> groupDocs = dbHelper.getGroupDocs(group_name.replace(" ", ""));
            ArrayList arrayList = new ArrayList();
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
                createPDFfromBitmap(pdfName, arrayList, "save");
            } else {
                createProtectedPDFfromBitmap(pdfName, arrayList, password, "save");
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            progressDialog.dismiss();
            Toast.makeText(ActMain.this, getResources().getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    private class groupShareAll extends AsyncTask<String, Void, String> {
        ArrayList<Uri> allPDFList;
        ProgressDialog progressDialog;

        private groupShareAll() {
            allPDFList = new ArrayList<>();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActMain.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            Iterator it = groupList.iterator();
            while (it.hasNext()) {
                String group_name = ((ModelDB) it.next()).getGroup_name();
                new ArrayList().clear();
                ArrayList<ModelDB> groupDocs = dbHelper.getShareGroupDocs(group_name.replace(" ", ""));
                ArrayList arrayList = new ArrayList();
                Iterator<ModelDB> it2 = groupDocs.iterator();
                while (it2.hasNext()) {
                    ModelDB next = it2.next();
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        arrayList.add(BitmapFactory.decodeStream(new FileInputStream(next.getGroup_doc_img()), (Rect) null, options));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (arrayList.size() > 0) {
                    createPDFfromBitmap(group_name, arrayList, "temp");
                    allPDFList.add(BaseAct.getURIFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/" + group_name + ".pdf", ActMain.this));
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND_MULTIPLE");
            intent.setType("application/pdf");
            intent.putExtra("android.intent.extra.STREAM", allPDFList);
            intent.putExtra("android.intent.extra.SUBJECT", "Share All");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent createChooser = Intent.createChooser(intent, (CharSequence) null);
            progressDialog.dismiss();
            startActivity(createChooser);
        }
    }

    public class adapterSetAllGroup extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        public adapterSetAllGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ActMain.this);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setMessage("Loading Data...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            if (Constant.current_tag.equals("All Docs")) {
                groupList = dbHelper.getAllGroups();
                return null;
            } else if (Constant.current_tag.equals("Business Card")) {
                groupList = dbHelper.getGroupsByTag("Business Card");
                return null;
            } else if (Constant.current_tag.equals("ID Card")) {
                groupList = dbHelper.getGroupsByTag("ID Card");
                return null;
            } else if (Constant.current_tag.equals("Academic Docs")) {
                groupList = dbHelper.getGroupsByTag("Academic Docs");
                return null;
            } else if (Constant.current_tag.equals("Personal Tag")) {
                groupList = dbHelper.getGroupsByTag("Personal Tag");
                return null;
            } else {
                groupList = dbHelper.getAllGroups();
                return null;
            }
        }

        @Override
        public void onPostExecute(String str) {
            super.onPostExecute(str);
            if (groupList.size() > 0) {
                rv_group.setVisibility(View.VISIBLE);
                ly_empty.setVisibility(View.GONE);

                mainActivity.selected_sorting = mainActivity.preferences.getString("sortBy", Constant.descending_date);
                if (selected_sorting.equals(Constant.ascending_date)) {
                    Log.e(ActMain.TAG, "onPostExecute: ascending_date");
                } else if (selected_sorting.equals(Constant.descending_date)) {
                    Collections.reverse(groupList);
                } else if (selected_sorting.equals(Constant.ascending_name)) {
                    Collections.sort(groupList, new sortingWithName());
                } else if (selected_sorting.equals(Constant.descending_name)) {
                    Collections.sort(groupList, new sortingWithName());
                }

                mainActivity.current_mode = mainActivity.preferences.getString("ViewMode", "List");
                if (current_mode.equals("Grid")) {
                    mainActivity.layoutManager = new GridLayoutManager((Context) mainActivity, 2, RecyclerView.VERTICAL, false);
                } else {
                    mainActivity.layoutManager = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
                }
                rv_group.setHasFixedSize(true);
                rv_group.setLayoutManager(layoutManager);
                mainActivity.allGroupAdapter = new adapterAllGroup(mainActivity, mainActivity.groupList, current_mode);
                rv_group.setAdapter(allGroupAdapter);
            } else {
                mainActivity.selected_sorting = mainActivity.preferences.getString("sortBy", Constant.descending_date);
                rv_group.setVisibility(View.GONE);
                ly_empty.setVisibility(View.VISIBLE);
                if (Constant.current_tag.equals("All Docs")) {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty));
                } else if (Constant.current_tag.equals("Business Card")) {
                    tv_empty.setText(getResources().getString(R.string.business_card_empty));
                } else if (Constant.current_tag.equals("ID Card")) {
                    tv_empty.setText(getResources().getString(R.string.id_card_empty));
                } else if (Constant.current_tag.equals("Academic Docs")) {
                    tv_empty.setText(getResources().getString(R.string.academic_docs_empty));
                } else if (Constant.current_tag.equals("Personal Tag")) {
                    tv_empty.setText(getResources().getString(R.string.personal_tag_empty));
                } else {
                    tv_empty.setText(getResources().getString(R.string.all_docs_empty));
                }
            }
            progressDialog.dismiss();
        }
    }

    private class groupSaveToGallery extends AsyncTask<String, Void, String> {
        private String group_name;
        private ProgressDialog progressDialog;

        private groupSaveToGallery(String group_name) {
            this.group_name = group_name;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActMain.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public String doInBackground(String... strArr) {
            try {
                ArrayList<ModelDB> groupDocs = dbHelper.getGroupDocs(group_name.replace(" ", ""));
                ArrayList arrayList = new ArrayList();
                Iterator<ModelDB> it = groupDocs.iterator();
                while (it.hasNext()) {
                    arrayList.add(it.next().getGroup_doc_img());
                }
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream((String) it2.next()), (Rect) null, options);
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getResources().getString(R.string.app_name) + "/Images");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(file, System.currentTimeMillis() + ".jpg");
                    if (file2.exists()) {
                        file2.delete();
                    }
                    if (decodeStream != null) {
                        decodeStream.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file2));
                        saveImageToGallery(file2.getPath(), ActMain.this);
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
            Toast.makeText(ActMain.this, getResources().getString(R.string.save_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    class sortingWithName implements Comparator<ModelDB> {
        sortingWithName() {
        }

        @Override
        public int compare(ModelDB dBModel, ModelDB dBModel2) {
            if (selected_sorting.equals(Constant.ascending_name)) {
                return new File(dBModel.group_name).getName().compareToIgnoreCase(new File(dBModel2.group_name).getName());
            }
            if (selected_sorting.equals(Constant.descending_name)) {
                return new File(dBModel2.group_name).getName().compareToIgnoreCase(new File(dBModel.group_name).getName());
            }
            return 0;
        }
    }

    public void connectivityDailogue(){

        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.connectivity_dialog);
        dialog.getWindow().setBackgroundDrawable( getDrawable(R.color.black_translucent));
        dialog.getWindow().setLayout(-1, -1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView ipText = dialog.findViewById(R.id.show_IP);
        ( dialog.findViewById(R.id.iv_close)).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        int serverPort = getPortNumber(getApplicationContext());
        Intent intent = new Intent(ActMain.this, HTTPService.class);
        (dialog.findViewById(R.id.btn_srv_Off)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AppSettings.isServiceStarted(ActMain.this)) {
                    stopService(intent);
                    AppSettings.setServiceStarted(ActMain.this, false);
                    dialog.dismiss();
                }
            }
        });

        if (Utility.available(serverPort)&&!AppSettings.isServiceStarted(ActMain.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }
            else{
            startService(intent);
            }
            AppSettings.setServiceStarted(ActMain.this, true);
        }

        if (AppSettings.isServiceStarted(ActMain.this)){
        ipText.setText("http://"+getLocalIpAddress(1) + ":" + getPortNumber(this));
    }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action) {
                case "do_something":
                    connectivityDailogue();
                    break;
            }
        }
    }


    public void purchaseDailogue(){

        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.purchase_dailog_screen_act);
        dialog.getWindow().setBackgroundDrawable( getDrawable(R.color.black_translucent));
        dialog.getWindow().setLayout(-1, -1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ( dialog.findViewById(R.id.iv_coffeeSub)).setOnClickListener(view -> {
            dialog.dismiss();
            //iapConnector.subscribe(ActMain.this, "testfund");
            iapConnector.subscribe(ActMain.this,"coffee_sub");
        });
        ( dialog.findViewById(R.id.iv_dinnerSub)).setOnClickListener(view -> {
            dialog.dismiss();
            //iapConnector.subscribe(ActMain.this, "testfund");
            iapConnector.subscribe(ActMain.this,"dinner_sub");
        });
        ( dialog.findViewById(R.id.iv_holidaySub)).setOnClickListener(view -> {
            dialog.dismiss();
            //iapConnector.subscribe(ActMain.this, "testfund");
            iapConnector.subscribe(ActMain.this,"holiday_sub");
        });
        ( dialog.findViewById(R.id.iv_close)).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void AfterpurchaseDailogue(){

        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.thanks_support_screen_act);
        dialog.getWindow().setBackgroundDrawable( getDrawable(R.color.black_translucent));
        dialog.getWindow().setLayout(-1, -1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ( dialog.findViewById(R.id.iv_close)).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

}

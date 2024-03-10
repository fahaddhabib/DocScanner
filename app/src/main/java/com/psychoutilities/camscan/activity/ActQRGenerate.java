package com.psychoutilities.camscan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.main_utils.AppLangSessionManager;
import com.psychoutilities.camscan.qrcode_generate.Contents;
import com.psychoutilities.camscan.qrcode_generate.Intents;
import com.psychoutilities.camscan.qrcode_generate.EncoderQRCode;
import org.bouncycastle.crypto.tls.CipherSuite;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ActQRGenerate extends BaseAct implements View.OnClickListener {
    private static final String TAG = "QRGenerateActivity";
    public EditText et_value;
    protected ImageView iv_back, iv_qrcode;

    AppLangSessionManager appLangSessionManager;


    private TextView iv_generate, iv_refresh;

    private Bitmap qrImg;
    private File qrPath;
    public String qrType, qr_value;
    private Spinner qrtype_spinner;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.qrgenerate_act);
        init();
    }

    private void init() {
        appLangSessionManager = new AppLangSessionManager(getApplicationContext());
        setLocale(appLangSessionManager.getLanguage());
        iv_back =  findViewById(R.id.iv_back);
        qrtype_spinner =  findViewById(R.id.qrtype_spinner);
        et_value =  findViewById(R.id.et_value);
        iv_qrcode =  findViewById(R.id.iv_qrcode);
        iv_generate =  findViewById(R.id.iv_generate);
        iv_refresh =  findViewById(R.id.iv_refresh);
        qrType = Contents.Type.TEXT;
        initSpinner();
    }

    public void setLocale(String lang) {
        if (lang.equals("")){
            lang="en";
        }
        Log.d("Support",lang+"");
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void initSpinner() {
            String[] spinner_item = {getResources().getString(R.string.text),
                getResources().getString(R.string.email),
                getResources().getString(R.string.phone),
                getResources().getString(R.string.sms),
                getResources().getString(R.string.url_key)
        };

        qrtype_spinner.setAdapter(new AdapterCustomSpinner(spinner_item));
        qrtype_spinner.setPopupBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialog_bg_color)));

        qrtype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i == 0) {
                    qrType = Contents.Type.TEXT;
                    et_value.setHint(getResources().getString(R.string.enter_your_text));
                    et_value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (i == 1) {
                    qrType = Contents.Type.EMAIL;
                    et_value.setHint(getResources().getString(R.string.enter_your_email));
                    et_value.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                } else if (i == 2) {
                    qrType = Contents.Type.PHONE;
                    et_value.setHint(getResources().getString(R.string.enter_your_phone));
                    et_value.setInputType(InputType.TYPE_CLASS_PHONE);
                } else if (i == 3) {
                    qrType = Contents.Type.SMS;
                    et_value.setHint(getResources().getString(R.string.enter_your_sms));
                    et_value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (i == 4) {
                    qrType = Contents.URL_KEY;
                    et_value.setHint(getResources().getString(R.string.enter_your_url_key));
                    et_value.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    qrType = Contents.Type.TEXT;
                    et_value.setHint(getResources().getString(R.string.enter_your_text));
                    et_value.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });
    }

    private File makeDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + "/QRCode/");
        if (!file.exists() && !file.mkdirs()) {
            return null;
        }
        File file2 = new File(file.getPath() + File.separator + "temp.jpg");
        qrPath = file2;
        return file2;
    }

    public void saveBitmap() {
        File makeDir = makeDir();
        if (makeDir != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(makeDir);
                qrImg.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View view) {
        Uri uri;
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.iv_generate:
                qr_value = et_value.getText().toString();
                hideSoftKeyboard(view);
                if (qr_value.length() > 0) {
                    Display defaultDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    Point point = new Point();
                    defaultDisplay.getSize(point);
                    int x = point.x;
                    int y = point.y;
                    if (x >= y) {
                        x = y;
                    }
                    try {
                        Intent intent = new Intent("com.google.zxing.client.android.ENCODE");
                        intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
                        intent.putExtra(Intents.Encode.TYPE, qrType);
                        intent.putExtra(Intents.Encode.DATA, qr_value);
                        EncoderQRCode qRCodeEncoder = new EncoderQRCode(this, intent, (x * 3) / 4, false);
                        Log.e(TAG, "onClick: " + qrType);
                        qrImg = qRCodeEncoder.encodeAsBitmap();
                        iv_qrcode.setVisibility(View.VISIBLE);
                        iv_qrcode.setImageBitmap(qrImg);
                        iv_generate.setVisibility(View.GONE);
                        iv_refresh.setVisibility(View.VISIBLE);
                        if(qrImg!=null)saveBitmap();
                        return;
                    } catch (WriterException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    et_value.setError("Required");
                    return;
                }
            case R.id.iv_qrcode:
                try {
                    File file = new File(qrPath.getPath());
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/*");
                    if (Build.VERSION.SDK_INT >= 23) {
                        uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share image using"));
                    return;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                }
            case R.id.iv_refresh:
                et_value.setText("");
                iv_qrcode.setVisibility(View.GONE);
                iv_refresh.setVisibility(View.GONE);
                iv_generate.setVisibility(View.VISIBLE);
                return;
            default:
                return;
        }
    }

    public class AdapterCustomSpinner extends BaseAdapter implements SpinnerAdapter {
        private String[] array;

        @Override
        public long getItemId(int i) {
            return (long) i;
        }

        public AdapterCustomSpinner(String[] strArr) {
            array = strArr;
        }

        @Override
        public int getCount() {
            return array.length;
        }

        @Override
        public Object getItem(int i) {
            return array[i];
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "inter_medium.ttf");
            TextView textView = new TextView(ActQRGenerate.this);
            textView.setText(array[i]);
            textView.setTextSize(14.0f);
            textView.setTypeface(createFromAsset);
            textView.setBackground(getResources().getDrawable(R.drawable.round_shape));
            textView.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{getResources().getColor(R.color.light_txt_color)}));
            textView.setTextColor(getResources().getColor(R.color.bg_color1));
            textView.setGravity(17);
            textView.setPadding(43, 15, 43, 15);
            return textView;
        }

        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "inter_medium.ttf");
            TextView textView = new TextView(ActQRGenerate.this);
            textView.setText(array[i]);
            textView.setTextSize(16.0f);
            textView.setTypeface(createFromAsset);
            textView.setTextColor(getResources().getColor(R.color.selected_txt_color));
            textView.setGravity(16);
            textView.setPadding(60, 35, CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, 35);
            return textView;
        }
    }

}

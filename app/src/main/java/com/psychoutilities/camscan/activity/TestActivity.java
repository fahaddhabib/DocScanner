package com.psychoutilities.camscan.activity;

import static com.psychoutilities.camscan.utils.AppSettings.getPortNumber;
import static com.psychoutilities.camscan.utils.Utility.getLocalIpAddress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.db.HelperDB;
import com.psychoutilities.camscan.main_utils.Constant;
import com.psychoutilities.camscan.models.ModelDB;
import com.psychoutilities.camscan.service.HTTPService;
import com.psychoutilities.camscan.utils.AppSettings;
import com.psychoutilities.camscan.utils.Utility;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    public HelperDB dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView testIpText= findViewById(R.id.show_IP);


        //1st test working for server-client, lan web sharing module
        /*
        TextView testIpText= findViewById(R.id.show_IP);
        int serverPort = getPortNumber(getApplicationContext());
        Intent intent = new Intent(TestActivity.this, HTTPService.class);
        if (AppSettings.isServiceStarted(TestActivity.this)) {
            stopService(intent);
            AppSettings.setServiceStarted(TestActivity.this, false);
        }
            if (Utility.available(serverPort)) {
            startForegroundService(intent);
            AppSettings.setServiceStarted(TestActivity.this, true);
            testIpText.setText(""+getLocalIpAddress(1) + ":" + getPortNumber(this)+":started");
        }
        else {
                testIpText.setText("not started");
            }*/
    }
}
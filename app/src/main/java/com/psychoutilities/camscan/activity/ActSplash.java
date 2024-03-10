package com.psychoutilities.camscan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.psychoutilities.camscan.R;

public class ActSplash extends BaseAct {

    TextView textView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash_act);

        textView =findViewById(R.id.txt_splash);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;

        textView.startAnimation(fadeIn);
        fadeIn.setDuration(4000);
        fadeIn.setFillAfter(true);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(ActSplash.this, ActMain.class));
            finish();
        },4000);
    }
}

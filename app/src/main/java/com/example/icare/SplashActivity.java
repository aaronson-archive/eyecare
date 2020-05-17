package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            boolean callValue = mPref.getBoolean("isFrist", false);
            boolean isAuto = mPref.getBoolean("autoLogin", false);
            if (!callValue) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else if (isAuto) {
                startActivity(new Intent(getApplicationContext(), RealMainActivity.class));
            } else if(!isFinishing()) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}

package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

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

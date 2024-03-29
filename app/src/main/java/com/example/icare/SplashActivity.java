package com.example.icare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    SharedPreferences mPref;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        btn_next = findViewById(R.id.btn_next);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(SplashActivity.this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + SplashActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(runnable, 200);
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean isSecure = mPref.getBoolean("isSecure", false);
            if (!isSecure) {
                startActivity(new Intent(getApplicationContext(), SecureActivity.class));
                finish();
            } else if (!isFinishing()) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}

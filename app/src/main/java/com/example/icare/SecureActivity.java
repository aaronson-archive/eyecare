package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class SecureActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private Button finger, password, next;
    private String authType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();

        finger = (Button)findViewById(R.id.finger);
        password = (Button)findViewById(R.id.password);
        next = (Button)findViewById(R.id.next);

        finger.setOnClickListener(this);
        password.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.finger :
                finger.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                password.setBackground(getResources().getDrawable(R.drawable.button));
                next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                authType = "finger";
                break;
            case R.id.password :
                finger.setBackground(getResources().getDrawable(R.drawable.button));
                password.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                authType = "password";
                break;
            case R.id.next :
                editor.putString("authType",authType);
                editor.apply();
                if (authType == "password") {
                    startActivity(new Intent(getApplicationContext(), PasswordActivity.class));
                    finish();
                } else if (authType == "finger") {
                    startActivity(new Intent(getApplicationContext(), FingerActivity.class));
                    finish();
                }
                break;
        }
    }
}

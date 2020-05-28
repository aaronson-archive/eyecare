package com.example.icare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private Button next;
    private EditText pwd, repwd;
    private boolean canNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();

        pwd = (EditText) findViewById(R.id.pwd);
        repwd = (EditText) findViewById(R.id.repwd);

        next = (Button) findViewById(R.id.next);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwdStr = pwd.getText().toString();
                String repwdStr = repwd.getText().toString();

                if (pwdStr.equals(repwdStr)) {
                    if (pwdStr.length() >= 4) {
                        next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                        canNext = true;
                    }
                } else {
                    next.setBackground(getResources().getDrawable(R.drawable.button));
                    canNext = false;
                }
            }

        };

        pwd.addTextChangedListener(textWatcher);
        repwd.addTextChangedListener(textWatcher);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canNext) {
                    editor.putString("password", pwd.getText().toString());
                    editor.putBoolean("isSecure", true);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });
    }
}

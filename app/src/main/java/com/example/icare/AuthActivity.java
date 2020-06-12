package com.example.icare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private Button finger, password, next;
    private Matcher matcher;
    private String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private EditText pwd;
    private String authType = "";
    private SharedPreferences mPref;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        finger = (Button) findViewById(R.id.finger);
        password = (Button) findViewById(R.id.password);
        next = (Button) findViewById(R.id.next);
        pwd = (EditText) findViewById(R.id.pwd);

        finger.setOnClickListener(this);
        password.setOnClickListener(this);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        intent = new Intent();

        final BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(), "설정된 지문이 없거나, 센서가 없습니다.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        final BiometricPrompt biometricPrompt = new BiometricPrompt(AuthActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "지문인식 성공", Toast.LENGTH_SHORT).show();
                loginComplete();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "지문인식 실패", Toast.LENGTH_SHORT).show();
                intent.putExtra("authResult", "실패");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pwd.length() == 4) {
                    next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                } else {
                    next.setBackground(getResources().getDrawable(R.drawable.button));
                }
            }

        };

        pwd.addTextChangedListener(textWatcher);

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인식")
                .setDescription("사용자의 지문을 인식하여 등록합니다.")
                .setNegativeButtonText("취소")
                .build();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (authType) {
                    case "finger":
                        biometricPrompt.authenticate(promptInfo);
                        break;
                    case "password":
                        // 로그인
                        String DBPW = mPref.getString("password", "none");

                        if (pwd.getText().toString().equals(DBPW)) {
                            loginComplete();
                        } else {
                            intent.putExtra("authResult", "실패");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        Log.e("PW", DBPW);
                        Log.e("RPW", pwd.getText().toString());
                        break;
                }

            }
        });


    }

    void loginComplete() {
        intent.putExtra("authResult", "성공");
        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finger:
                authType = "finger";
                finger.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                password.setBackground(getResources().getDrawable(R.drawable.button));
                break;
            case R.id.password:
                authType = "password";
                password.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                finger.setBackground(getResources().getDrawable(R.drawable.button));
                pwd.setVisibility(View.VISIBLE);
                break;
        }
    }
}

package com.example.icare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button finger, password, next;
    EditText pwd, name, email;
    private Matcher matcher;
    private String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    boolean canNext = false;
    String authType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        finger = (Button)findViewById(R.id.finger);
        password = (Button)findViewById(R.id.password);
        next = (Button)findViewById(R.id.next);
        pwd = (EditText)findViewById(R.id.pwd);
        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);

        finger.setOnClickListener(this);
        password.setOnClickListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (name.length() > 0 && email.length() > 0 || email.length() > 0 && pwd.length() > 0) {
                    next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                    canNext = true;
                } else {
                    next.setBackground(getResources().getDrawable(R.drawable.button));
                    canNext = false;
                }
            }

        };

        pwd.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        final BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
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

        final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "지문인식 성공", Toast.LENGTH_SHORT).show();
                // 여기에 DB에서 검색 후 스크린 이동
                next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "지문인식 실패", Toast.LENGTH_SHORT).show();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인식")
                .setDescription("사용자의 지문을 인식하여 등록합니다.")
                .setNegativeButtonText("취소")
                .build();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEmail = validate(email.getText().toString(), Pattern.compile(emailPattern));

                if (!isEmail) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (canNext) {
                    switch (authType) {
                        case "finger":
                            biometricPrompt.authenticate(promptInfo);
                            break;
                        case "password":
                            Toast.makeText(getApplicationContext(), "비밀번호.", Toast.LENGTH_SHORT).show();
                            // 여기서 성공해서 스크린 이동
                            break;
                    }
                }
            }
        });
    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finger:
                authType = "finger";
                finger.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                password.setBackground(getResources().getDrawable(R.drawable.button));
                name.setVisibility(View.VISIBLE);
                pwd.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                name.setText("");
                email.setText("");
                pwd.setText("");
                break;
            case R.id.password:
                authType = "password";
                password.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                finger.setBackground(getResources().getDrawable(R.drawable.button));
                name.setVisibility(View.GONE);
                pwd.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                name.setText("");
                email.setText("");
                pwd.setText("");
                break;
        }
    }

    public boolean validate(String text, Pattern pattern) {
        matcher = pattern.matcher(text);
        return matcher.matches();
    }
}

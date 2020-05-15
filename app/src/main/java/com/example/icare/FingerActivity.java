package com.example.icare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class FingerActivity extends AppCompatActivity {

    TextView fingerText;
    Button start, next;
    boolean canNext = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger);

        fingerText = (TextView)findViewById(R.id.finger_text);
        start = (Button)findViewById(R.id.start);
        next = (Button)findViewById(R.id.next);

        final BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                fingerText.setText("지문인식 센서가 없습니다. 앱을 종료해주세요.");
                fingerText.setTextColor(Color.parseColor("#c04444"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        final BiometricPrompt biometricPrompt = new BiometricPrompt(FingerActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                fingerText.setText("지문인식이 성공했습니다.");
                fingerText.setTextColor(Color.parseColor("#44c067"));
                Toast.makeText(getApplicationContext(), "지문인식 성공", Toast.LENGTH_SHORT).show();
                next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                canNext = true;
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                fingerText.setText("지문이 정확하지 않습니다, 다시 한 번 해주세요.");
                fingerText.setTextColor(Color.parseColor("#c04444"));
                Toast.makeText(getApplicationContext(), "지문인식 실패", Toast.LENGTH_SHORT).show();
                canNext = false;
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인식")
                .setDescription("사용자의 지문을 인식하여 등록합니다.")
                .setNegativeButtonText("취소")
                .build();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canNext) {
                    startActivity(new Intent(getApplicationContext(), CompleteActivity.class));
                    finish();
                }
            }
        });

    }
}

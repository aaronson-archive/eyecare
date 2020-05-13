package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordActivity extends AppCompatActivity {

    Button next;
    EditText pwd, repwd;
    boolean canNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        pwd = (EditText)findViewById(R.id.pwd);
        repwd = (EditText)findViewById(R.id.repwd);

        next = (Button)findViewById(R.id.next);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (pwd.getText() == repwd.getText() ) {
                    next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                    canNext = true;
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
                    startActivity(new Intent(getApplicationContext(), CompleteActivity.class));
                    finish();
                }

            }
        });

    }
}
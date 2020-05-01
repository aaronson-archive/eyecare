package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    EditText name, phone, email, age;
    Button man, girl, next;
    boolean canNext = false;
    String sex = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        email = (EditText)findViewById(R.id.email);
        age = (EditText)findViewById(R.id.age);

        man = (Button)findViewById(R.id.man);
        girl = (Button)findViewById(R.id.girl);
        next = (Button)findViewById(R.id.next);

        man.setOnClickListener(this);
        girl.setOnClickListener(this);
        next.setOnClickListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (name.length() > 0 && phone.length() > 0 && girl.length() > 0 && age.length() > 0 && sex != "") {
                    next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                    canNext = true;
                } else {
                    next.setBackground(getResources().getDrawable(R.drawable.button));
                    canNext = false;
                }

            }

        };

        name.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        girl.addTextChangedListener(textWatcher);
        age.addTextChangedListener(textWatcher);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.man :
                man.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                girl.setBackground(getResources().getDrawable(R.drawable.button));
                sex = "man";
                if (name.length() > 0 && phone.length() > 0 && girl.length() > 0 && age.length() > 0 && sex != "") {
                    next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                    canNext = true;
                } else {
                    next.setBackground(getResources().getDrawable(R.drawable.button));
                    canNext = false;
                }
                break;
            case R.id.girl :
                man.setBackground(getResources().getDrawable(R.drawable.button));
                girl.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                sex = "girl";
                if (name.length() > 0 && phone.length() > 0 && girl.length() > 0 && age.length() > 0 && sex != "") {
                    next.setBackground(getResources().getDrawable(R.drawable.button_actvie));
                    canNext = true;
                } else {
                    next.setBackground(getResources().getDrawable(R.drawable.button));
                    canNext = false;
                }
                break;
            case R.id.next :
                if (canNext && sex != "") {
                    startActivity(new Intent(getApplicationContext(), SecureActivity.class));
                    finish();
                }
                break;
        }
    }

}

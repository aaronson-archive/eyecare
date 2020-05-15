package com.example.icare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Matcher matcher;
    private EditText name, phone, email, age;
    private Button man, girl, next;
    private boolean canNext = false;
    private String sex = "";
    private String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        age = (EditText) findViewById(R.id.age);

        man = (Button) findViewById(R.id.man);
        girl = (Button) findViewById(R.id.girl);
        next = (Button) findViewById(R.id.next);

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
        switch (v.getId()) {
            case R.id.man:
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
            case R.id.girl:
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
            case R.id.next:
                boolean isEmail = validate(email.getText().toString(), Pattern.compile(emailPattern));

                if (isEmail) {
                    if (canNext && sex != "") {
                        String key = mDatabase.push().getKey();
                        mDatabase.child(key).setValue(firebaseMap());
                        startActivity(new Intent(getApplicationContext(), SecureActivity.class));
                        finish();
                    }
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

        }
    }

    public boolean validate(String text, Pattern pattern) {
        matcher = pattern.matcher(text);
        return matcher.matches();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private Map<String, Object> firebaseMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", email.getText().toString());
        map.put("name", name.getText().toString());
        map.put("age", age.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("sex", sex);

        return map;
    }
}

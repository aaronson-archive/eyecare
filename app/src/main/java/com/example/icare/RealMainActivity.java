package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RealMainActivity extends AppCompatActivity implements View.OnClickListener {

    Button setting, start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        setting = (Button)findViewById(R.id.setting);
        start = (Button)findViewById(R.id.start);

        setting.setOnClickListener(this);
        start.setOnClickListener(this);
    }

    public void onClick(View v) {

        switch (v.getId()){
            case R.id.start :
                break;
            case R.id.setting :
                startActivity(new Intent(getApplicationContext(), ConfigureActivity.class));
                finish();
                break;
        }
    }
}

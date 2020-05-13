package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                Intent intent = new Intent(Intent.ACTION_MAIN); //태스크의 첫 액티비티로 시작
                intent.addCategory(Intent.CATEGORY_HOME);   //홈화면 표시
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "모니터링을 시작합니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting :
                startActivity(new Intent(getApplicationContext(), ConfigureActivity.class));
                finish();
                break;
        }
    }
}

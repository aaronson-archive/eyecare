package com.example.icare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;


public class ConfigureActivity extends AppCompatActivity {

    Button prev;
    LinearLayout moniter, alram, birghtness;
    ConfigureActivity c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        prev = (Button)findViewById(R.id.prev);
        moniter = (LinearLayout)findViewById(R.id.moniter);
        alram = (LinearLayout)findViewById(R.id.alram);
        birghtness = (LinearLayout)findViewById(R.id.birghtness);


        moniter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListDialogFragment
                        .createBuilder(c, getSupportFragmentManager())
                        .setTitle("모니터링 거리지정")
                        .setItems(new String[]{"15cm", "30cm", "45cm"})
                        .setRequestCode(11)
                        .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                        .show();ㅈㅁㅁ
                return false;
            }
        });

        alram.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListDialogFragment
                        .createBuilder(c, getSupportFragmentManager())
                        .setTitle("모니터링 종류 선택")
                        .setItems(new String[]{"밝기줄이기", "알림띄우기"})
                        .setRequestCode(11)
                        .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                        .show();


                return false;
            }
        });

        birghtness.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListDialogFragment
                        .createBuilder(c, getSupportFragmentManager())
                        .setTitle("화면 밝기 지정")
                        .setItems(new String[]{"50%", "40%", "30%", "20%", "10%", "0%"})
                        .setRequestCode(11)
                        .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                        .show();
                return false;
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RealMainActivity.class));
                finish();
            }
        });
    }


}

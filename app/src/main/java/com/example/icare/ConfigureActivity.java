package com.example.icare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;


public class ConfigureActivity extends AppCompatActivity implements IListDialogListener {

    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private Button prev;
    private LinearLayout moniter, brightness;
    private ConfigureActivity c = this;
    private int moniterIdx, brightnessIdx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();

        prev = (Button) findViewById(R.id.prev);
        moniter = (LinearLayout) findViewById(R.id.moniter);
        brightness = (LinearLayout) findViewById(R.id.brightness);

        moniterIdx = mPref.getInt("moniterIdx", 0);
        brightnessIdx = mPref.getInt("brightnessIdx", 0);


        moniter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListDialogFragment a = ListDialogFragment
                        .createBuilder(c, getSupportFragmentManager())
                        .setTitle("모니터링 거리지정")
                        .setItems(new String[]{"15cm", "30cm", "45cm"})
                        .setSelectedItem(moniterIdx)
                        .setRequestCode(11)
                        .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
                        .show();
                return false;
            }
        });

        brightness.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListDialogFragment
                        .createBuilder(c, getSupportFragmentManager())
                        .setTitle("화면 밝기 지정")
                        .setItems(new String[]{"50%", "40%", "30%", "20%", "10%", "0%"})
                        .setSelectedItem(brightnessIdx)
                        .setRequestCode(13)
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

    @Override
    public void onListItemSelected(CharSequence value, int number, int requestCode) {
        switch (requestCode) {
            case 11:
                moniterIdx = number;
                editor.putInt("moniterIdx", number);
                editor.putString("moniter", value.toString());
                break;

            case 13:
                brightnessIdx = number;
                editor.putInt("brightnessIdx", number);
                editor.putString("brightness", value.toString());
                break;
        }
        editor.apply();
    }
}

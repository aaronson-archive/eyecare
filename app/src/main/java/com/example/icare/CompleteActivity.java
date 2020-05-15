package com.example.icare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CompleteActivity extends AppCompatActivity {

    private Button next;
    private DatabaseReference mDatabase;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isFrist", true);
        editor.commit();

        next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mDatabase.push().getKey();
                mDatabase.child(key).setValue(firebaseMap());
                startActivity(new Intent(getApplicationContext(), RealMainActivity.class));
                finish();
            }
        });
    }

    private Map<String, Object> firebaseMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", mPref.getString("email", ""));
        map.put("name", mPref.getString("name", ""));
        map.put("age", mPref.getString("age", ""));
        map.put("phone", mPref.getString("phone", ""));
        map.put("sex", mPref.getString("sex", ""));
        map.put("authType", mPref.getString("authType", ""));
        map.put("password", mPref.getString("password", ""));
        return map;
    }
}

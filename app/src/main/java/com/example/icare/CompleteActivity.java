package com.example.icare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompleteActivity extends AppCompatActivity {

    private Button next;
    private SharedPreferences mPref;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        db  = FirebaseFirestore.getInstance();

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isFrist", true);
        editor.commit();

        next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(mPref.getString("email","")).set(firebaseMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

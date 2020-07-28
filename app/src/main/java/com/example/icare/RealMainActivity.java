package com.example.icare;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

public class RealMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button setting, start;
    private Context context;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private static int SPLASH_TIME_OUT = 4000;
    float F = 1f;           //focal length
    float sensorX, sensorY; //camera sensor dimensions
    float angleX, angleY;
    private CameraSource cameraSource;
    private WindowManager.LayoutParams params;
    private float mBrightness;
    private int pBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        params = getWindow().getAttributes();
        mBrightness = params.screenBrightness;

        try {
            pBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        mPref = mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();

        setting = (Button) findViewById(R.id.setting);
        start = (Button) findViewById(R.id.start);

        start.setText("START");

        setting.setOnClickListener(this);
        start.setOnClickListener(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                switch (start.getText().toString()) {
                    case "START":
                        context = getApplicationContext();
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                            Toast.makeText(this, "앱을 사용하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_MAIN); //태스크의 첫 액티비티로 시작
                            intent.addCategory(Intent.CATEGORY_HOME);   //홈화면 표시
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
                            startActivity(intent);

                            if (cameraSource != null) {
                                cameraSource.stop();
                            }

                            Camera camera = frontCam();
                            Camera.Parameters campar = camera.getParameters();
                            F = campar.getFocalLength();
                            angleX = campar.getHorizontalViewAngle();
                            angleY = campar.getVerticalViewAngle();
                            sensorX = (float) (Math.tan(Math.toRadians(angleX / 2)) * 2 * F);
                            sensorY = (float) (Math.tan(Math.toRadians(angleY / 2)) * 2 * F);
                            camera.stopPreview();
                            camera.release();
                            createCameraSource();

                            Toast.makeText(getApplicationContext(), "모니터링을 시작합니다.", Toast.LENGTH_SHORT).show();
                            start.setText(mPref.getString("stauts", "STOP"));
                            editor.putString("status", "STOP");
                        }
                        break;
                    case "STOP":
                        AlertDialog alertDialog = new AlertDialog.Builder(RealMainActivity.this)
                                .setTitle("본인 인증")
                                .setMessage("모니터링 종료를 위해서는 본인 인증이 필요합니다.\n인증하시겠습니까?\nIdentity verification for an end to monitoring is required.\nAuthentication failed!!\nDo you want to approve?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivityForResult(new Intent(getApplicationContext(), AuthActivity.class), 1000);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();

                        alertDialog.show();
                        break;

                }
                editor.apply();
                break;
            case R.id.setting:
                if (start.getText().toString() == "Stop")
                    cameraSource.stop();
                startActivity(new Intent(getApplicationContext(), ConfigureActivity.class));
                finish();
                editor.putString("status", "START");
                editor.apply();
                break;
        }
    }

    private Camera frontCam() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            Log.v("CAMID", camIdx + "");
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("FAIL", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }


    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new FaceTracker()));


        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(50.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraSource.start();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class FaceTracker extends Tracker<Face> {
        private FaceTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            float p = (float) Math.sqrt(
                    (Math.pow((face.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x -
                            face.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x), 2) +
                            Math.pow((face.getLandmarks().get(Landmark.LEFT_EYE).getPosition().y -
                                    face.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().y), 2)));

            float H = 63;
            float d = F * (H / sensorX) * (768 / (2 * p));
            Log.e("Distance", String.format("%.0f", d));

            int moniter = Integer.parseInt(mPref.getString("moniter", "20cm").replaceAll("cm", "")) * 10;
            final float brightness = Float.parseFloat("0." + mPref.getString("brightness", "0%").replace("%", ""));

            if (d < moniter) {
                if (params.screenBrightness != brightness) {
                    RealMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            params.screenBrightness = brightness;
                            getWindow().setAttributes(params);
                            Settings.System.putInt(getContentResolver(), "screen_brightness", (int) (brightness * 255));
                        }
                    });
                }
            }
            if (d >= moniter) {
                try {
                    if (Settings.System.getInt(getContentResolver(), "screen_brightness") < pBrightness) {
                        RealMainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                params.screenBrightness = mBrightness;
                                getWindow().setAttributes(params);
                                android.provider.Settings.System.putInt(getContentResolver(), "screen_brightness", pBrightness);
                            }
                        });
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putString("status", "START");
        editor.putBoolean("alert", true);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            switch (data.getStringExtra("authResult")) {
                case "성공":
                    if (cameraSource != null)
                        cameraSource.stop();
                    Toast.makeText(getApplicationContext(), "모니터링을 종료합니다.", Toast.LENGTH_SHORT).show();
                    start.setText(mPref.getString("stauts", "START"));
                    editor.putString("status", "START");
                    params.screenBrightness = mBrightness;
                    getWindow().setAttributes(params);
                    Settings.System.putInt(getContentResolver(), "screen_brightness", pBrightness);
                    break;
                case "실패":
                    Toast.makeText(RealMainActivity.this, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}

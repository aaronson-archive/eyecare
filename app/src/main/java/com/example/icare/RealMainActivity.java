package com.example.icare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    Button setting, start;
    Context context;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private static int SPLASH_TIME_OUT = 4000;
    float F = 1f;           //focal length
    float sensorX, sensorY; //camera sensor dimensions
    float angleX, angleY;
    private CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        mPref = mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();

        setting = (Button) findViewById(R.id.setting);
        start = (Button) findViewById(R.id.start);

        start.setText(mPref.getString("status", "모니터링 시작"));

        setting.setOnClickListener(this);
        start.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                switch (start.getText().toString()) {
                    case "모니터링 시작":
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                       /* Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);*/
                            }
                        }, SPLASH_TIME_OUT);

                        context = getApplicationContext();
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
                        } else {
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

                        }
                        Toast.makeText(getApplicationContext(), "모니터링을 시작합니다.", Toast.LENGTH_SHORT).show();
                        start.setText(mPref.getString("stauts", "모니터링 종료"));
                        editor.putString("status", "모니터링 종료");
                        break;
                    case "모니터링 종료":
                        cameraSource.stop();
                        Toast.makeText(getApplicationContext(), "모니터링을 종료합니다.", Toast.LENGTH_SHORT).show();
                        start.setText(mPref.getString("stauts", "모니터링 시작"));
                        editor.putString("status", "모니터링 시작");
                        break;
                }
                editor.commit();
                break;
            case R.id.setting:
                if (start.getText().toString() == "모니터링 종료")
                    cameraSource.stop();
                startActivity(new Intent(getApplicationContext(), ConfigureActivity.class));
                finish();
                editor.putString("status", "모니터링 시작");
                editor.commit();
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
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new FaceTracker()));

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
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

}

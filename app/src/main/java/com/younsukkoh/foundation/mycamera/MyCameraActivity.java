package com.younsukkoh.foundation.mycamera;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Younsuk on 12/9/2016.
 */

public class MyCameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final String TAG = MyCameraActivity.class.getSimpleName();

    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private boolean mCameraFacingBack = true;

    private Unbinder mUnbinder;
    @BindView(R.id.mca_tv_cameraPreview)
    TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDeviceForCamera();
        setUpUI();
    }

    /**
     * If the device does not have camera, return.
     */
    private void checkDeviceForCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getApplicationContext(), "Your device does not have camera.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    /**
     * Hides status bar. Call it before setContentView(), otherwise it does not work.
     */
    private void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void setUpUI() {
        hideStatusBar();

        setContentView(R.layout.my_camera_activity);

        mUnbinder = ButterKnife.bind(this);

        mTextureView.setSurfaceTextureListener(this);
    }

    @OnClick(R.id.mca_b_capture)
    void capture(View view) {
        // shutter, picture callback, picture callback post view, jpeg
        mCamera.takePicture(null, null,
                new ImageHandler(getApplicationContext(), this, mCameraFacingBack)
        );

        // TODO right now, releasing of camera takes place before file can be saved.
//        if (mCameraFacingBack)
//            openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
//        else
//            openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @OnClick(R.id.mca_b_changeCamera)
    void changeCamera(View view) {
        if (mCameraFacingBack)
            openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
        else
            openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);

        mCameraFacingBack = !mCameraFacingBack;
    }

    @OnCheckedChanged(R.id.mca_tb_toggleFlash)
    void toggleFlash(CompoundButton compoundButton, boolean flashOn) {
        if (flashOn) {

        }
        else {

        }
        Log.i(Constants.DEBUG, "On? " + flashOn);
    }

    /**
     * Open camera and start preview
     *
     * @param cameraFacing open rear facing camera or front facing camera
     */
    public void openCamera_startPreview(int cameraFacing) {
        //If camera is already running, stop preview and release.
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        mCamera = Camera.open(cameraFacing);

        //Rotate the preview 90 degrees clockwise for correct portrait view.
        mCamera.setDisplayOrientation(90);

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurfaceTexture = surfaceTexture;
        openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        // Camera does all the work.
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        // Invoked every time there's a new camera preview frame.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind butter knife
        mUnbinder.unbind();
    }

}

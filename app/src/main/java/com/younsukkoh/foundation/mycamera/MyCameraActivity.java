package com.younsukkoh.foundation.mycamera;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private OrientationEventListener mOrientationListener;
    private int mCameraAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkDeviceForCamera();
        setUpUI();
        setUpOrientationListener();
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

    /**
     * Set up user interface
     */
    private void setUpUI() {
        hideStatusBar();

        setContentView(R.layout.my_camera_activity);

        mUnbinder = ButterKnife.bind(this);

        mTextureView.setSurfaceTextureListener(this);
    }

    /**
     * Set up listener for orientation change by calculating degrees of tilt in the user's device.
     */
    private void setUpOrientationListener() {
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int i) {
                mCameraAngle = i;
            }
        };

        if (mOrientationListener.canDetectOrientation())
            mOrientationListener.enable();
        else
            mOrientationListener.disable();
    }

    @OnClick(R.id.mca_b_capture)
    void capture(View view) {
        Log.i(Constants.DEBUG, "clicked");

        takePicture();
    }

    private void takePicture() {
        mCamera.takePicture(
                null, // ShutterCallback
                null, // Raw PictureCallback
                new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        Log.i(Constants.DEBUG, "onPictureTaken: Start!");

                        File image = getImageFile();
                        Bitmap bitmap = getImageBitmap(bytes);

                        try {
                            //Save file with bytes (data) from the image that was taken.
                            FileOutputStream fos = new FileOutputStream(image);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();

                            Log.i(Constants.DEBUG, "File created!");

                            addImageToGallery(image);

                        } catch (IOException ioe) {
                            Log.e(TAG, ioe.getMessage());
                        } finally {
                            // Re-start Preview
                            if (mCameraFacingBack)
                                openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
                            else
                                openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);

                            Log.i(Constants.DEBUG, "onPictureTaken: All Done!");
                        }
                    }
                }
        );
    }

    /**
     * @return Directory of file that the image will be stored in. Null otherwise.
     */
    private File getImageFile() {
        // Directory for saving file externally onto device's sd card.
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCamera");

        // If directory does not exist, create a new directory using mkdirs, but if that doesn't work return.
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e(TAG, "Directory for the image could not be found nor created.");
            Toast.makeText(getApplicationContext(), "Directory for the image could not be found nor created.", Toast.LENGTH_LONG).show();

            return null;
        }
        else {
            Log.i(Constants.DEBUG, "Empty file created.");

            // File directory created for image, path/IMG_date
            return new File(directory.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        }
    }

    /**
     * @return bitmap of the image taken with correct rotation depending on camera orientation.
     */
    private Bitmap getImageBitmap(byte[] bytes) {
        // Bitmap for the actual image.
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Bitmap rotatedBitmap = bitmap; // If camera is landscape no need to change.
        Matrix matrix = new Matrix(); // Matrix for rotation of bitmap

        // Device is upright: Portrait
        if ((300 < mCameraAngle && mCameraAngle < 360) || (0 <= mCameraAngle && mCameraAngle < 60)) matrix.postRotate(90f);

        // Device is tilted to the right: Landscape
        if (60 < mCameraAngle && mCameraAngle < 120) matrix.postRotate(180f);

        // If device is tilted to the left (mCameraAngle from 240 to 300), no need for additional rotation. Landscape.

        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return rotatedBitmap;
    }

    /**
     * Add the image to gallery. Provide information of image
     */
    private void addImageToGallery(File image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
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
        // Check the device for camera flash feature
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(getApplicationContext(), "Device does not have flash feature.", Toast.LENGTH_LONG).show();
            return;
        }

        Camera.Parameters parameters = mCamera.getParameters();
        if (flashOn) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(parameters);

        Log.i(Constants.DEBUG, "On? " + flashOn);
    }

    /**
     * Open camera and start preview
     *
     * @param cameraFacing open rear facing camera or front facing camera
     */
    public void openCamera_startPreview(int cameraFacing) {
        closeCamera();

        try {
            mCamera = Camera.open(cameraFacing);

            // Rotate the preview 90 degrees clockwise for correct portrait view.
            mCamera.setDisplayOrientation(90);

            // Set camera to continually auto-focus
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters);

            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        }
        catch (RuntimeException re) {
            Log.e(TAG, "Error while trying to open Camera ", re);
        }
        catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }

    }

    /**
     * If camera is already running, stop preview and release.
     */
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void startReview() {
        Intent intent = new Intent(MyCameraActivity.this, MainActivity.class);
        startActivity(intent);
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
        closeCamera();

        mSurfaceTexture = null;

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

        // Disable orientation listener
        mOrientationListener.disable();
    }

}

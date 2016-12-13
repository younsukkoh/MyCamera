package com.younsukkoh.foundation.mycamera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Younsuk on 12/10/2016.
 */

public class ImageHandler implements Camera.PictureCallback {

    private static final String TAG = ImageHandler.class.getSimpleName();

    private Context mContext;
    private MyCameraActivity mMyCameraActivity;
    private boolean mCameraFacingBack;

    public ImageHandler(Context context, Activity myCameraActivity, boolean cameraFacingBack) {
        Log.i(Constants.DEBUG, "Image Handler started");
        this.mContext = context;
        this.mMyCameraActivity = (MyCameraActivity) myCameraActivity;
        this.mCameraFacingBack = cameraFacingBack;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Log.i(Constants.DEBUG, "Picture taken!");

        // Directory for saving file externally onto device's sd card.
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCamera");

        // If directory does not exist, create a new directory using mkdirs, but if that doesn't work return.
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e(TAG, "Directory for the image could not be found nor created.");
            Toast.makeText(mContext, "Directory for the image could not be found nor created.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(Constants.DEBUG, "Empty file created.");

        // File created for image, path/IMG_date
        File image = new File(directory.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));

        try {
            //Save file with bytes (data) from the image that was taken.
            FileOutputStream fos = new FileOutputStream(image);
            fos.write(bytes);
            fos.close();

            Log.i(Constants.DEBUG, "File created!");

            addToGallery(image);
        }
        catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        }
        finally {
            // Whether succeed or not re-start preview
            if (mCameraFacingBack)
                mMyCameraActivity.openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
            else
                mMyCameraActivity.openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);

            mMyCameraActivity = null;
            mContext = null;
        }
    }

    /**
     * Add image to the gallery.
     * @param image file that will be added to the gallery.
     */
    private void addToGallery(File image) {
        // Provide information of image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

    }
}

package com.younsukkoh.foundation.mycamera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Younsuk on 12/10/2016.
 */

public class PictureCallback implements Camera.PictureCallback {

    private static final String TAG = PictureCallback.class.getSimpleName();

    private Context mContext;
    private MyCameraActivity mMyCameraActivity;
    private boolean mCameraFacingBack = false;

    public PictureCallback(Context context, Activity myCameraActivity) {
        this.mContext = context;
        this.mMyCameraActivity = (MyCameraActivity) myCameraActivity;
    }

    private void saveImageTask(byte[] bytes) {
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

//            //TODO
//            ExifInterface exifInterface = new ExifInterface(image.getAbsolutePath());
//            int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//            Log.i(Constants.DEBUG, "ROTATION 1: " + rotation);
//
//            int rotationDegree = 0;
//            switch (rotation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotationDegree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotationDegree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotationDegree = 270;
//                    break;
//                default:
//                    rotationDegree = 0;
//                    break;
//            }
//            String exifOri = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//            Log.i(Constants.DEBUG, "ROTATION 2: " + exifOri);
//            //TODO

            addToGallery(image);
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
        } finally {
            if (mCameraFacingBack)
                mMyCameraActivity.openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
            else
                mMyCameraActivity.openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
    }

    public void setCameraFacingBack(boolean cameraFacingBack) {
        mCameraFacingBack = cameraFacingBack;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Log.i(Constants.DEBUG, "onPictureTaken: Start!");

        new SaveImageTask().execute(bytes);

        Log.i(Constants.DEBUG, "onPictureTaken: All Done!");
    }


    /**
     * Add image to the gallery.
     *
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

    private class SaveImageTask extends AsyncTask<byte[], String, String> {

        @Override
        protected String doInBackground(byte[]... bytes) {
            // Directory for saving file externally onto device's sd card.
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCamera");

            // If directory does not exist, create a new directory using mkdirs, but if that doesn't work return.
            if (!directory.exists() && !directory.mkdirs()) {
                Log.e(TAG, "Directory for the image could not be found nor created.");
                Toast.makeText(mContext, "Directory for the image could not be found nor created.", Toast.LENGTH_LONG).show();
            }

            Log.i(Constants.DEBUG, "Empty file created.");

            // File created for image, path/IMG_date
            File image = new File(directory.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));

            try {
                //Save file with bytes (data) from the image that was taken.
                FileOutputStream fos = new FileOutputStream(image);
                fos.write(bytes[0]);
                fos.close();

                Log.i(Constants.DEBUG, "File created!");

//            //TODO
//            ExifInterface exifInterface = new ExifInterface(image.getAbsolutePath());
//            int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//            Log.i(Constants.DEBUG, "ROTATION 1: " + rotation);
//
//            int rotationDegree = 0;
//            switch (rotation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotationDegree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotationDegree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotationDegree = 270;
//                    break;
//                default:
//                    rotationDegree = 0;
//                    break;
//            }
//            String exifOri = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//            Log.i(Constants.DEBUG, "ROTATION 2: " + exifOri);
//            //TODO

                addToGallery(image);
            } catch (IOException ioe) {
                Log.e(TAG, ioe.getMessage());
            } finally {
                if (mCameraFacingBack)
                    mMyCameraActivity.openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
                else
                    mMyCameraActivity.openCamera_startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            return null;
        }
    }
}

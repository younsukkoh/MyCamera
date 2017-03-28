package com.younsukkoh.foundation.mycamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.VoicemailContract;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.younsukkoh.foundation.mycamera.camera.CameraActivity;
import com.younsukkoh.foundation.mycamera.gallery.GalleryActivity;
import com.younsukkoh.foundation.mycamera.util.Constants;
import com.younsukkoh.foundation.mycamera.util.RuntimePermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Younsuk on 2/10/2017.
 */

public class MainActivity extends AppCompatActivity{

//    private EditText mFirstNameET, mLastNameET, mUserNameET, mPasswordET;
//    private String mFirstName, mLastName, mUserName, mPassword;

    private Button mButton;
    private String mEncodedString, mImageName;
    private Bitmap mBitmap;
    private File mFile;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mButton = (Button) findViewById(R.id.ma_b_upload);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                RuntimePermissions.checkAndRequestPermissions(getApplicationContext(), MainActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(intent, 10);
            }
        });

//        mFirstNameET = (EditText) findViewById(R.id.ma_et_first_name);
//        mLastNameET = (EditText) findViewById(R.id.ma_et_last_name);
//        mUserNameET = (EditText) findViewById(R.id.ma_et_user_name);
//        mPasswordET = (EditText) findViewById(R.id.ma_et_password);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.

        if (requestCode == RuntimePermissions.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                RuntimePermissions.showPermissionDialog(getApplicationContext(), MainActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);
            }
        }

    }

    private void getFileUri() {
        mImageName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        mFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator
                + mImageName
        );

        mUri = Uri.fromFile(mFile);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Log.i(Constants.DEBUG, "start!");
            new EncodeImage().execute();
        }
    }

    private class EncodeImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(Constants.DEBUG, "background");

            mBitmap = BitmapFactory.decodeFile(mFile.getPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            mBitmap.recycle();

            byte[] array = outputStream.toByteArray();
            mEncodedString = Base64.encodeToString(array, 0);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(Constants.DEBUG, "post execute");
            makeRequest();
        }
    }

    private void makeRequest() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.i(Constants.DEBUG, "make request to " + ipAddress);

        ipAddress = "146.113.67.160";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://" + ipAddress + "/mycamera/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(Constants.DEBUG, "response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(Constants.DEBUG, "fail response: " + error.getMessage());
                        error.getMessage();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("encoded_string", mEncodedString);
                map.put("image_name", mImageName);

                Log.i(Constants.DEBUG, "mapping! " + mEncodedString);
                Log.i(Constants.DEBUG, "mapping! " + mImageName);
                return map;
            }
        };
        requestQueue.add(request);
    }

    //    public void upload(View view) {
//        mFirstName = mFirstNameET.getText().toString();
//        mLastName = mLastNameET.getText().toString();
//        mUserName = mUserNameET.getText().toString();
//        mPassword = mPasswordET.getText().toString();
//
//        BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
//        backgroundTask.execute(mFirstName, mLastName, mUserName, mPassword);
//    }
}

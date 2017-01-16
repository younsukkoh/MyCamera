package com.younsukkoh.foundation.mycamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.younsukkoh.foundation.mycamera.camera.CameraActivity;
import com.younsukkoh.foundation.mycamera.gallery.GalleryActivity;
import com.younsukkoh.foundation.mycamera.util.RuntimePermissions;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Unbinder mUnbinder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.

        if (requestCode == RuntimePermissions.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                RuntimePermissions.showPermissionDialog(getApplicationContext(), MainActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);
            }
        }

        //else if
    }

    /**
     * TODO First time using, it correctly works. But once the permission is granted, it will no longer take the user to camera.
     */
    @OnClick(R.id.am_b_camera)
    void myCamera() {
        RuntimePermissions.checkAndRequestPermissions(getApplicationContext(), MainActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);

        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.am_b_settings)
    void mySettings() {
        Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind butter knife
        mUnbinder.unbind();
    }
}
package com.younsukkoh.foundation.mycamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final Button checkPermission = (Button) findViewById(R.id.am_b_camera);
        checkPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RuntimePermissions.checkAndRequestPermissions(getApplicationContext(), MainActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.

        if (requestCode == RuntimePermissions.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                RuntimePermissions.showPermissionDialog(getApplicationContext(), MainActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);
            }
        }

        //else if
    }

    private void launchCamera() {
        Intent intent = new Intent(MainActivity.this, MyCameraActivity.class);
        startActivity(intent);
    }

}
package com.younsukkoh.foundation.mycamera.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.younsukkoh.foundation.mycamera.R;
import com.younsukkoh.foundation.mycamera.util.Constants;

import java.io.File;

/**
 * Class for displaying a single image onto the screen
 *
 * Created by Younsuk on 1/15/2017.
 */

public class ImageActivity extends AppCompatActivity {

    ImageView mImageView;
    File mImageFile;

    /**
     *
     * @param context
     * @param file Image file that will be displayed
     * @return intent for starting this activity
     */
    public static Intent newIntent(Context context, File file) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(Constants.EXTRA_IMAGE_FILE, file);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageFile = (File) getIntent().getSerializableExtra(Constants.EXTRA_IMAGE_FILE);

        setUpUI();
    }

    /**
     * Set up user interface
     */
    private void setUpUI() {
        setContentView(R.layout.image_activity);

        hideStatusBar();

        mImageView = (ImageView) findViewById(R.id.ia_iv_image);

        // Load the image
        Glide.with(getApplicationContext())
                .load(mImageFile)
                .placeholder(R.drawable.place_holder_gray)
                .error(R.drawable.image_error_gray)
                .into(mImageView);
    }

    /**
     * Hides status bar. Call it before setContentView(), otherwise it does not work.
     */
    private void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}

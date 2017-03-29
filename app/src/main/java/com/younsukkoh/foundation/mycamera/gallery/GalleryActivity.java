package com.younsukkoh.foundation.mycamera.gallery;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.younsukkoh.foundation.mycamera.R;
import com.younsukkoh.foundation.mycamera.camera.CameraActivity;
import com.younsukkoh.foundation.mycamera.util.Constants;
import com.younsukkoh.foundation.mycamera.util.RuntimePermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Younsuk Koh on 1/1/2017.
 * Updated by Younsuk Koh on 3/28/2017.
 */

public class GalleryActivity extends AppCompatActivity {

    private final static String TAG = GalleryActivity.class.getSimpleName();

    // Butter Knife utility for binding views
    private Unbinder mUnbinder;

    // Action mode activated upon long click
    private ActionMode mActionMode;
    private List<View> mActivatedViews;
    private List<File> mActivatedImages;

    @BindView(R.id.ga_rv) RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request users for dangerous permissions
        RuntimePermissions.checkAndRequestPermissions(getApplicationContext(), GalleryActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);

        setUpUI();
        setUpRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Use camera to take more pictures
            case R.id.gam_camera:
                Intent intent = new Intent(GalleryActivity.this, CameraActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.

        if (requestCode == RuntimePermissions.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(GalleryActivity.this, CameraActivity.class);
                startActivity(intent);
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                RuntimePermissions.showPermissionDialog(getApplicationContext(), GalleryActivity.this, RuntimePermissions.CAMERA_PERMISSIONS);
            }
        }

    }

    private void setUpUI() {
        setContentView(R.layout.gallery_activity);

        mUnbinder = ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    /**
     * @return Get directory that holds image files in my app folder
     */
    private File[] getImageFiles() {
        File galleryDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), getString(R.string.app_name));

        return galleryDirectory.listFiles();
    }

    private void setUpRecyclerView() {
        // If initialized for the first time
        if (mImageAdapter == null) {
            mImageAdapter = new ImageAdapter();
            mRecyclerView.setAdapter(mImageAdapter);
        }
        // Otherwise notify just the changes
        else {
            mImageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unbind butter knife
        mUnbinder.unbind();
    }

    /**
     * Adapter for Images
     */
    public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.gallery_activity_image_holder, parent, false);

            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            holder.bindImage(getImageFiles()[position]);
        }

        @Override
        public int getItemCount() {
            return getImageFiles().length;
        }

    } // close Image Adapter

    /**
     * Image Holder for each images
     */
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView mImageView;
        File mImageFile;

        public ImageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mImageView = ButterKnife.findById(itemView, R.id.gaih_iv_image);
        }

        /**
         * Display the image to view
         */
        public void bindImage(File imageFile) {
            mImageFile = imageFile;

            Glide.with(getApplicationContext())
                    .load(imageFile)
                    .thumbnail(0.1f) // Reduce the image to 10%
                    .placeholder(R.drawable.place_holder_gray)
                    .error(R.drawable.image_error_gray)
                    .into(mImageView);
        }

        @Override
        public void onClick(View view) {
            // If window is not in action mode, see image
            if (mActionMode == null) {
                Intent intent = ImageActivity.newIntent(getApplicationContext(), mImageFile);
                startActivity(intent);
            }
            // If window is in action mode, toggle activation of the view
            else {
                if (mImageView.isActivated())
                    deactivateView(mImageView);
                else
                    activateView(mImageView);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mActionMode == null) {
                mActionMode = startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.gallery_activity_menu_activated, menu);

                        mActivatedViews = new ArrayList<>();
                        mActivatedImages = new ArrayList<>();

                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            // Delete items
                            case R.id.gam_delete:
                                // Display Dialog
                                new AlertDialog.Builder(GalleryActivity.this)
                                        .setTitle("Warning")
                                        .setMessage("Are you sure you want to delete " + mActivatedViews.size() + " images?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int x) {
                                                for (int i = 0; i < mActivatedImages.size(); i ++)
                                                    deleteImage(mActivatedImages.get(i));
                                                // Close action mode
                                                mActionMode.finish();
                                                // Update recycler view
                                                setUpRecyclerView();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                                return true;

                            // Upload items
                            case R.id.gam_upload:
                                // Allow only 1 image to be uploaded
                                if (mActivatedViews.size() == 1) {
                                    // Display Dialog
                                    new AlertDialog.Builder(GalleryActivity.this)
                                            .setTitle("Image Selected for Upload")
                                            .setMessage("Are you sure you want to upload this image?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    new ImageUpload(getApplicationContext()).execute(mActivatedImages.get(0));
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null)
                                            .show();
                                }
                                else {
                                    // Display Dialog
                                    new AlertDialog.Builder(GalleryActivity.this)
                                            .setTitle("Alert")
                                            .setMessage("Only 1 image can be uploaded at a time.")
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // clear all choices
                        clearActivatedView();

                        mActionMode = null;
                        mActivatedViews = null;
                        mActivatedImages = null;

                    }
                });
                // activate the view that was long clicked
                activateView(mImageView);
            }
            return true;
        }

        /**
         * Activate view, including highlight and adding to list of activated views.
         * @param view View that will be highlighted and activated
         */
        private void activateView(View view) {
            view.setActivated(true);

            // Set rectangle around view
            view.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.image_holder_activated_shape));

            // Change the padding
            int highlightedPadding = (int) getApplicationContext().getResources().getDimension(R.dimen.four_dp);
            view.setPadding(highlightedPadding, highlightedPadding, highlightedPadding, highlightedPadding);

            // Make the view transparent
            view.setAlpha(0.7f); // alpha is 1.0 originally

            // Add selected file
            mActivatedImages.add(mImageFile);

            // Add selected view
            mActivatedViews.add(view);

            // Show number of items selected
            mActionMode.setTitle(mActivatedImages.size() + " Selected");
        }

        /**
         * Deactivate view, including highlight and removing from list of activated views.
         * @param view View that will be rid of highlight and activation
         */
        private void deactivateView(View view) {
            view.setActivated(false);

            view.setBackground(null);

            // Restore padding
            int originalPadding = (int) getApplicationContext().getResources().getDimension(R.dimen.one_dp);
            view.setPadding(originalPadding, originalPadding, originalPadding, originalPadding);

            // Restore transparency
            view.setAlpha(1.0f);

            // Remove file that is unselected
            mActivatedImages.remove(mImageFile);

            // Remove selected view
            mActivatedViews.remove(view);

            // Show number of items selected
            mActionMode.setTitle(mActivatedImages.size() + " Selected");
        }

        /**
         * When trying to clear activation using deactivate, the number of array list fluctuates, causing errors or leaving some view still activated.
         */
        private void clearActivatedView() {
            // clear activation
            View view;
            for (int i = 0; i < mActivatedViews.size(); i ++) {
                view = mActivatedViews.get(i);

                view.setActivated(false);

                view.setBackground(null);

                // Restore padding
                int originalPadding = (int) getApplicationContext().getResources().getDimension(R.dimen.one_dp);
                view.setPadding(originalPadding, originalPadding, originalPadding, originalPadding);

                // Restore transparency
                view.setAlpha(1.0f);
            }
        }

        /**
         * Delete from the app and default Gallery
         * @param image file that will be deleted
         */
        private void deleteImage(File image) {
            // uri: scheme for the content to retrieve
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            // projection: a list of which columns to return. We need only ID
            String[] projection = {MediaStore.Images.Media._ID };
            // selection: a filter declaring which rows to return
            String selection = MediaStore.Images.Media.DATA + " = ?";
            // selection args: replacing ? (above) for search
            String[] selectionArgs = new String[] { image.getAbsolutePath() };
            // sort order: we don't care
            String sortOrder = null;

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor.moveToFirst()) {
                // Return zero-based index for given column
                // OrThrow part makes error more clear
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                // Utility for working with Uri. Append id at the end of uri
                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                // DELETE!
                contentResolver.delete(deleteUri, null, null);
            }
            else {
                Log.e(TAG, "The cursor is empty!");
            }
            cursor.close();
        }

    } // Closer Image Holder

}

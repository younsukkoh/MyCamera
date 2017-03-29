package com.younsukkoh.foundation.mycamera.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for uploading image through MySQL server.
 *
 * Created by Younsuk on 3/28/2017.
 */

public class ImageUpload extends AsyncTask<File, Void, String>{

    private static final String TAG = ImageUpload.class.getSimpleName();
    private Context mContext;
    private String mImageName;

    public ImageUpload(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(File... image) {
        File file = image[0];
        mImageName = file.getName() + ".jpg";

        // Encode image file
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        bitmap.recycle();

        byte[] array = outputStream.toByteArray();
        return Base64.encodeToString(array, 0);
    }

    @Override
    protected void onPostExecute(String encodedImage) {
        makeRequest(encodedImage);
    }

    /**
     * Request the image to be uploaded
     * @param encodedImage encoded string of the image that will be uploaded
     */
    private void makeRequest(final String encodedImage) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://146.113.67.160/mycamera/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "Response: " + response);

                        if (!response.equals(""))
                            Toast.makeText(mContext, "Uploaded!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(mContext, "Oops!", Toast.LENGTH_SHORT).show();

                        mContext = null;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error while uploading: " + error.getMessage());
                        Toast.makeText(mContext, "Oops!", Toast.LENGTH_SHORT).show();

                        mContext = null;
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("encoded_string", encodedImage);
                map.put("image_name", mImageName);

                return map;
            }
        };
        requestQueue.add(request);
    }
}

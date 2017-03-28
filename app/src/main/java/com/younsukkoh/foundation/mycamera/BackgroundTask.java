package com.younsukkoh.foundation.mycamera;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Younsuk on 2/10/2017.
 */

public class BackgroundTask extends AsyncTask<String, Void, String> {

    Context mContext;

    public BackgroundTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
//        String upload_info_url = "http://146.113.65.69/test/upload_info.php";
//        String upload_info_url = "http://192.168.56.1/test/upload_info.php";
        String upload_info_url = "http://10.0.2.2/test/upload_info.php";
//        String upload_info_url = "http://127.0.0.1/test/upload_info.php";
//        String upload_info_url = "http://localhost/test/upload_info.php";

        String firstName = params[0];
        String lastName = params[1];
        String userName = params[2];
        String password = params[3];
        try {
            URL url = new URL(upload_info_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String data = URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") + "&" +
                    URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&" +
                    URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                    URLEncoder.encode("user_password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

            InputStream is = httpURLConnection.getInputStream();
            is.close();

            return "Success";
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == "Success")
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
    }
}

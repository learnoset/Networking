package com.learnoset.networking;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LearnosetNetRequest {

    private static String url = "";
    private final Context context;
    private final Map<String, String> params = new HashMap<>();
    private Dialog progressDialog;
    private int requestMethod = 0;
    private String getParameters = "";

    public LearnosetNetRequest(Context context) {
        this.context = context;
        this.progressDialog = null;
    }

    public LearnosetNetRequest(Context context, String url) {
        this.context = context;
        this.progressDialog = null;
        LearnosetNetRequest.url = url;
    }

    public LearnosetNetRequest(Context context, String url, int requestMethod) {
        this.context = context;
        this.progressDialog = null;
        LearnosetNetRequest.url = url;
        this.requestMethod = requestMethod;
    }

    public LearnosetNetRequest(Context context, int requestType) {
        this.context = context;
        this.progressDialog = null;
        this.requestMethod = requestType;
    }

    public static void init(String url, Context context) {

        LearnosetNetRequest.url = url;

        try {
            FileOutputStream fileOutputStream = context.openFileOutput("df.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(url.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUrl(Context context) {
        String data = "";
        try {
            FileInputStream fileInputStream = context.openFileInput("df.txt");
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void addParam(String key, String value) {

        if (requestMethod == 0) {
            if (getParameters.isEmpty()) {
                getParameters = getParameters + "?" + key + "=" + value;
            } else {
                getParameters = getParameters + "&" + key + "=" + value;
            }
        } else {
            params.put(key, value);
        }

    }

    public void setCustomDialog(Dialog customDialog) {
        this.progressDialog = customDialog;
    }

    public void execute(boolean showProgressDialog, int resultCode, NetResponseListener networkResponse) {

        if (progressDialog == null) {
            progressDialog = new LearnosetProgressDialog(context);
            progressDialog.setCancelable(false);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (showProgressDialog) {
            progressDialog.show();
        }

        if (url.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                final String getUrl = getUrl(context);

                if (!getUrl.isEmpty()) {
                    url = getUrl;
                    break;
                }
            }

            if (url.isEmpty()) {
                networkResponse.onRequestFailed("URL not found or URL is empty. Please cal LearnosetNetRequest.init(url) function and pass your URL as an argument", context, resultCode);
            } else {
                makeRequest(networkResponse, resultCode);
            }
        } else {
            makeRequest(networkResponse, resultCode);
        }

    }

    private void makeRequest(NetResponseListener networkResponse, int resultCode) {

        StringRequest stringRequest = new StringRequest(requestMethod, url + getParameters, response -> {
            networkResponse.onRequestSuccess(response, context, resultCode);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }, error -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            networkResponse.onRequestFailed(error.getMessage(), context, resultCode);
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        LearnosetSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
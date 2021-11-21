package com.learnoset.networking;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

@SuppressLint("StaticFieldLeak")
final class LearnosetSingleton {
    private static LearnosetSingleton instance;
    private static Context ctx;
    private RequestQueue requestQueue;


    private LearnosetSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    static synchronized LearnosetSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new LearnosetSingleton(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

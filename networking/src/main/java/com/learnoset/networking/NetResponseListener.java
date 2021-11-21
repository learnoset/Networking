package com.learnoset.networking;

import android.content.Context;

public interface NetResponseListener {

    void onRequestSuccess(String response, Context context, int resultCode);

    void onRequestFailed(String errorMessage, Context context, int resultCode);
}

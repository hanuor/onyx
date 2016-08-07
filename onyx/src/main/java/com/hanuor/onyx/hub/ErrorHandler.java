package com.hanuor.onyx.hub;

import android.util.Log;

/**
 * Created by Shantanu Johri on 07-08-2016.
 */
public class ErrorHandler {

    public static void writeMessage(String message) {
        Log.d("Onyx-Message", message);
    }

    public static void writeError(String error) {
        Log.e("Onyx-Error", error);
    }
}

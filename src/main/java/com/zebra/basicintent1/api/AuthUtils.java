package com.zebra.basicintent1.api;

import android.util.Log;

import java.util.Base64;

public class AuthUtils {
    public static String getBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        // Log.d("AUTH_TAG", Base64.getEncoder().encodeToString(credentials.getBytes()));
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}

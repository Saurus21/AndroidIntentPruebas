package com.zebra.basicintent1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;

public class CookieManager {

    private static final String PREF_NAME = "Cookies de Aplicacion";
    private static final String KEY_COOKIES = "cookies";

    private final SharedPreferences sharedPreferences;

    public CookieManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // guardar una cookie con un nombre especifico
    public void saveCookie(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        logCookies();
    }

    // obtener las cookies
    public Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof String) {
                cookies.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return cookies;
    }

    // obtener una cookie por su nombre
    public String getCookie(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void logCookies() {
        Map<String, String> cookies = getCookies();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if(entry.getValue() instanceof String) {
                Log.d("COOKIE_MANAGER", "Cookie: " + entry.getKey() + " = " + entry.getValue());
            }
        }
    }

    // eliminar todas las cookies (al implementar un logout)
    public void clearCookies() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Log.d("COOKIES", "Cookies borradas");
    }
}
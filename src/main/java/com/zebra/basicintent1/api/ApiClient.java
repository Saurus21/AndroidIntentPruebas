package com.zebra.basicintent1.api;

import com.zebra.basicintent1.utils.AppCookieJar;
import com.zebra.basicintent1.utils.CookieManager;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://1.1.1.1:1000/"; // ip y puerto a conectar
    private static String username = "ADMIN"; // credenciales por defecto
    private static String password = "ADMIN10";
    private static Retrofit retrofit = null;

    public static void setCredentials(String username, String password) {
        ApiClient.username = username;
        ApiClient.password = password;
    }

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor(context));
            httpClient.cookieJar(new AppCookieJar());

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        // .header("Content-Type", "application/json")
                        // .header("Accept", "application/json")
                        .method(original.method(), original.body());

                // agregar Basic Auth solo si es el endpoint /user
                if (original.url().toString().contains("/user")) {
                    String authHeader = AuthUtils.getBasicAuthHeader(username, password);
                    requestBuilder.header("Authorization", authHeader);
                }

                /*

                // agregar cookies
                CookieManager cookieManager = new CookieManager(context);
                String xsrfToken = cookieManager.getCookie("X-XSRF-TOKEN");
                String sessionCookie = cookieManager.getCookie("SESSION");

                if (xsrfToken != null && !xsrfToken.isEmpty()) {
                    requestBuilder.header("X-XSRF-TOKEN", xsrfToken);
                    Log.d("API_CLIENT", "Agregando X-XSRF-TOKEN: " + xsrfToken);
                } else {
                    Log.w("API_CLIENT", "No se encontró el token CSRF en las cookies");
                }

                if (sessionCookie != null && !sessionCookie.isEmpty()) {
                    requestBuilder.header("Cookie", "XSRF-TOKEN=" + xsrfToken + "; SESSION=" + sessionCookie);
                    Log.d("API_CLIENT", "Agregando Cookie: " + sessionCookie);
                }
                */


                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

package com.zebra.basicintent1.api;

import android.content.Context;
import android.util.Log;

import com.zebra.basicintent1.utils.CookieManager;

import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class Interceptor implements okhttp3.Interceptor {
    private final Context context;

    public Interceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .method(original.method(), original.body());

        // agregar el token CSRF si está disponible
        CookieManager cookieManager = new CookieManager(context);
        String xsrfToken = cookieManager.getCookie("X-XSRF-TOKEN");
        String sessionCookie = cookieManager.getCookie("SESSION");

        if (xsrfToken != null && !xsrfToken.isEmpty()) {
            requestBuilder.header("X-XSRF-TOKEN", xsrfToken);
            Log.d("API_CLIENT", "Agregando X-XSRF-TOKEN: " + xsrfToken);
        }

        if (sessionCookie != null && !sessionCookie.isEmpty()) {
            requestBuilder.header("Cookie", "XSRF-TOKEN=" + xsrfToken + "; SESSION=" + sessionCookie);
            Log.d("API_CLIENT", "Agregando Cookie: " + sessionCookie);
        }

        // otros headers de ser necesario
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Accept", "application/json");

        Request request = requestBuilder.build();

        // Registrar la solicitud (opcional)
        Log.d("API_CLIENT", "Solicitud: " + request.toString());

        // Continuar con la solicitud
        Response response = chain.proceed(request);

        // Registrar la respuesta (opcional)
        Log.d("API_CLIENT", "Respuesta: " + response.toString());

        return response;
    }
}

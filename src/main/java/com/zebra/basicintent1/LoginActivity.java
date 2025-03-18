package com.zebra.basicintent1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.basicintent1.api.ApiClient;
import com.zebra.basicintent1.api.InventarioApi;
import com.zebra.basicintent1.ui.DashboardActivity;
import com.zebra.basicintent1.utils.CookieManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CookieManager cookieManager = new CookieManager(this);
        cookieManager.clearCookies();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            } else {
                authenticate(username, password);
            }
        });
    }

    private void authenticate(String username, String password) {
        InventarioApi api = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);

        // usar las credenciales ingresadas por el usuario
        ApiClient.setCredentials(username, password);

        api.authenticate().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {

                    // Obtener todas las cookies del encabezado "Set-Cookie"
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");

                    String xsrfToken = null;
                    for (String cookie : cookies) {
                        if (cookie.startsWith("XSRF-TOKEN")) {
                            xsrfToken = cookie.split(";")[0].split("=")[1]; // valor despues de "XSRF-TOKEN="
                            //Log.d("API_LOGIN", "Token CSRF encontrado: " + xsrfToken);
                            break;
                        }
                    }

                    String sessionCookie = null;
                    for (String cookie : cookies) {
                        if (cookie.startsWith("SESSION")) {
                            sessionCookie = cookie.split(";")[0].split("=")[1]; // valor despues de "SESSION="
                            break;
                        }
                    }

                    CookieManager cookieManager = new CookieManager(LoginActivity.this);

                    if (xsrfToken != null) {
                        // guardar el token CSRF
                        cookieManager.saveCookie("X-XSRF-TOKEN", xsrfToken);
                        cookieManager.saveCookie("XSRF-TOKEN", xsrfToken);

                        // Log.d("API_LOGIN", "Token CSRF encontrado y guardado: " + xsrfToken);
                    } else {
                        Log.d("API_LOGIN", "No se encontró la cookie XSRF-TOKEN.");
                    }

                    if (sessionCookie != null) {
                        // guardar la cookie de sesión

                        cookieManager.saveCookie("SESSION", sessionCookie);
                        // Log.d("API_LOGIN", "Cookie de sesión encontrada y guardada: " + sessionCookie);
                    }

                    Log.d("API_LOGIN", "Autenticación exitosa");
                    Log.d("LOG_COOKIES", "Cookies guardadas:");
                    cookieManager.logCookies();

                    // redirigir a DashboardActivity
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish(); // cerrar LoginActivity para que no se pueda volver atrás
                } else {
                    Toast.makeText(LoginActivity.this, "Error en la autenticación", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // borrar cookies al cerrar la aplicación
        CookieManager cookieManager = new CookieManager(this);
        cookieManager.clearCookies();
        Log.d("COOKIES", "Cookies borradas al cerrar la aplicación");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // borrar cookies al detener la actividad
        CookieManager cookieManager = new CookieManager(this);
        cookieManager.clearCookies();
        Log.d("COOKIES", "Cookies borradas al detener la actividad");
    }
*/
}
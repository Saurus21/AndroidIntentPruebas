package com.zebra.basicintent1.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.api.ApiClient;
import com.zebra.basicintent1.api.Inventario;
import com.zebra.basicintent1.api.InventarioApi;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.database.ScannedData;
import com.zebra.basicintent1.modelosDatos.LecturaPendiente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class SincronizarActivity extends AppCompatActivity {

    private static final String TAG = "SincronizarActivity";
    private ProgressBar progressBar;
    private AppDatabase db;
    private InventarioApi inventarioApi;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private AguaRuralApi aguaRuralApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferir_datos);

        Button buttonTransferir = findViewById(R.id.buttonTransferir);
        progressBar = findViewById(R.id.progressBar);

        db = AppDatabase.getInstance(this);
        aguaRuralApi = ApiClient.getRetrofitInstance(this).create(AguaRuralApi.class);

        buttonTransferir.setOnClickListener(v ->
            transferirDatos()
        );
    }

    private void transferirDatos() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            List<LecturaPendiente> lecturasPendientes = AppDatabase.getInstance(this)
                    .lecturaPendienteDao().getLecturasNoSincronizadas();

            if (lecturasPendientes.isEmpty()) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "No hay lecturas pendientes", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            procesarTransferencia(lecturasPendientes);
        }).start();
    }

    private void procesarTransferencia(List<LecturaPendiente> lecturas) {
        int transferidos = 0;
        int errores = 0;

        for (LecturaPendiente lecturaLocal : lecturas) {
            try {
                Lectura lecturaParaApi = new Lectura(
                        lecturaLocal.medidorId,
                        lecturaLocal.serialMedidor,
                        lecturaLocal.valor,
                        lecturaLocal.observacion,
                        lecturaLocal.timestamp
                );

                // llamada sincrona
                Response<Void> response = aguaRuralApi.enviarLectura(lecturaParaApi).execute();

                if (response.isSuccessful()) {
                    lecturaLocal.sincronizado = true;
                    AppDatabase.getInstance(this).lecturaPendienteDao().update(lecturaLocal);
                    transferidos++;
                } else {
                    errores++;
                }
            } catch (Exception e) {
                errores++;
                Log.e(TAG, "Error al enviar lectura: " + e.getMessage(), e);
            }
        }

        // Mostrar resultados en el hilo principal
        int finalTransferidos = transferidos;
        int finalErrores = errores;

        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            
            @SuppressLint("DefaultLocale")
            String mensaje = String.format(
                    "Transferencia completada:\nNuevos: %d\nErrores: %d",
                    finalTransferidos, finalErrores
            );
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        });
    }
}
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class TransferirDatosActivity extends AppCompatActivity {

    private static final String TAG = "TransferirDatosActivity";
    private ProgressBar progressBar;
    private AppDatabase db;
    private InventarioApi inventarioApi;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferir_datos);

        Button buttonTransferir = findViewById(R.id.buttonTransferir);
        progressBar = findViewById(R.id.progressBar);

        db = AppDatabase.getInstance(this);
        inventarioApi = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);

        buttonTransferir.setOnClickListener(v -> {
            Log.d(TAG, "Iniciando proceso de transferencia");
            transferirDatos();
        });
    }

    private void transferirDatos() {
        progressBar.setVisibility(View.VISIBLE);

        AppDatabase db = AppDatabase.getInstance(this);

        // Obtener los datos de forma asíncrona
        db.scannedDataDao().getAllScannedData().observe(this, scannedDataList -> {
            if (scannedDataList != null && !scannedDataList.isEmpty()) {
                new Thread(() -> {
                    // Procesar la lista en un hilo secundario
                    procesarTransferencia(scannedDataList);
                }).start();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "No hay datos para transferir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarTransferencia(List<ScannedData> datosLocales) {
        int transferidos = 0;
        int existentes = 0;
        int errores = 0;

        for (ScannedData datoLocal : datosLocales) {
            try {
                // Verificar existencia
                Call<Boolean> callVerificar = inventarioApi.existeCodigoBarra(datoLocal.getData());
                Response<Boolean> responseVerificar = callVerificar.execute();

                if (responseVerificar.isSuccessful() && Boolean.TRUE.equals(responseVerificar.body())) {
                    existentes++;
                    continue;
                }

                // Preparar y enviar datos
                Inventario nuevoInventario = crearInventarioDesdeScannedData(datoLocal);
                Call<Inventario> callTransferir = inventarioApi.addInventario(nuevoInventario);
                Response<Inventario> responseTransferir = callTransferir.execute();

                if (responseTransferir.isSuccessful()) {
                    transferidos++;
                } else {
                    errores++;
                }
            } catch (Exception e) {
                errores++;
                Log.e(TAG, "Error procesando: " + datoLocal.getData(), e);
            }
        }

        // Mostrar resultados en el hilo principal
        int finalTransferidos = transferidos;
        int finalExistentes = existentes;
        int finalErrores = errores;

        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            
            @SuppressLint("DefaultLocale")
            String mensaje = String.format(
                    "Transferencia completada:\nNuevos: %d\nExistentes: %d\nErrores: %d",
                    finalTransferidos, finalExistentes, finalErrores
            );
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        });
    }

    private Inventario crearInventarioDesdeScannedData(ScannedData scannedData) {
        Inventario inventario = new Inventario();
        inventario.setCodigoBarra(scannedData.getData());

        // Parsear timestamp si es necesario
        String[] fechaHora = parsearTimestamp(scannedData.getTimestamp());
        inventario.setFecha(fechaHora[0]);
        inventario.setHh(Integer.parseInt(fechaHora[1]));
        inventario.setMm(Integer.parseInt(fechaHora[2]));

        return inventario;
    }

    private String[] parsearTimestamp(String timestamp) {
        String[] resultado = {"0000-00-00", "0", "0"}; // [fecha, hora, minuto]

        if (timestamp == null || timestamp.isEmpty()) {
            return resultado;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            resultado[0] = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            resultado[1] = String.valueOf(dateTime.getHour());
            resultado[2] = String.valueOf(dateTime.getMinute());
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Error parseando timestamp: " + timestamp, e);
        }

        return resultado;
    }
}
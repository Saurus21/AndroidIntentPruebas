package com.zebra.basicintent1.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.zebra.basicintent1.R;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.notaVenta.ProductoEscaneado;
import com.zebra.basicintent1.database.ProductoEscaneadoPendiente;
import com.zebra.basicintent1.notaVenta.ProductosEscaneadosAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EscanearProductosActivity extends AppCompatActivity {

    private static final String LOG_TAG = "EscanearProductosActivity";
    private List<ProductoEscaneado> productosEscaneados = new ArrayList<>();
    private ProductosEscaneadosAdapter adapter;
    private TextView tvResumen;
    private AlertDialog confirmDialog;
    private String numeroNotaVenta;
    private boolean procesandoEscaneo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_productos);

        // Recibir el número de nota de venta del Intent
        numeroNotaVenta = getIntent().getStringExtra("numero_nota");

        if (numeroNotaVenta == null || numeroNotaVenta.isEmpty()) {
            Toast.makeText(this, "Error: No se especificó nota de venta", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(LOG_TAG, "numeroNotaVenta: " + numeroNotaVenta);

        // configurar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductosEscaneados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductosEscaneadosAdapter(productosEscaneados);
        recyclerView.setAdapter(adapter);

        // textview para mostrar el resumen
        tvResumen = findViewById(R.id.tvResumenEscaneo);
        actualizarResumen();

        // configurar boton de escaneo
        Button btnActivateScanner = findViewById(R.id.btnActivateScanner);
        btnActivateScanner.setOnClickListener(v -> {
            Log.d(LOG_TAG, "btnActivateScanner clicked");
            toggleScanner(btnActivateScanner);
        });

        // configurar boton de finalizar
        setupFinalizarButton();

        // registrar el BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, filter);

        // cargar productos ya escaneados en esta nota
        new Thread(() -> {
            List<ProductoEscaneadoPendiente> pendientes = AppDatabase.getInstance(this)
                    .productoEscaneadoDao()
                    .getPendientesPorNota(numeroNotaVenta);

            runOnUiThread(() -> {
                for (ProductoEscaneadoPendiente p : pendientes) {
                    productosEscaneados.add(new ProductoEscaneado(p.codigo));
                }
                adapter.notifyDataSetChanged();
                actualizarResumen();
            });
        }).start();
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        private long lastScanTime = 0;
        private String lastScanCode = "";

        @Override
        public void onReceive(Context context, Intent intent) {
            String decodedData = intent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
            long currentTime = System.currentTimeMillis();

            // Evitar duplicados del mismo código en un corto período
            if (decodedData != null &&
                    (!decodedData.equals(lastScanCode) || (currentTime - lastScanTime) > 1000)) {

                lastScanCode = decodedData;
                lastScanTime = currentTime;
                guardarEscaneo(decodedData);
            }
        }
    };

    private void toggleScanner(Button button) {
        Intent intent = new Intent();
        intent.setAction("com.symbol.datawedge.api.ACTION");
        intent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "TOGGLE_SCANNING");
        button.setText("Toggle Scanner");
        Snackbar.make(button, "Toggling Scanner", Snackbar.LENGTH_SHORT).show();
        sendBroadcast(intent);
    }

    private void setupFinalizarButton() {
        Button btnFinalizar = findViewById(R.id.btnFinalizar);
        btnFinalizar.setOnClickListener(v -> {
            if (productosEscaneados.isEmpty()) {
                Toast.makeText(this, "No se han escaneado productos", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            } else {
                mostrarDialogoConfirmacion();
            }
        });
    }

    private void actualizarResumen() {
        tvResumen.setText(String.format(Locale.getDefault(),
                "Productos escaneados: %d", productosEscaneados.size()));
    }

    private void mostrarDialogoConfirmacion() {
        confirmDialog = new AlertDialog.Builder(this)
                .setTitle("Confirmar escaneo")
                .setMessage(String.format(Locale.getDefault(),
                        "¿Desea finalizar con %d productos escaneados?", productosEscaneados.size()))
                .setPositiveButton("Sí", (dialog, which) -> {
                    runOnUiThread(() -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("refresh_required", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void guardarEscaneo(String codigo) {

        Log.d("DB_DEBUG", "Intentado guardar codigo: " + codigo + " para nota: " + numeroNotaVenta);

        // verificar si existe
        new Thread(() -> {
            try {
                ProductoEscaneadoPendiente nuevo = new ProductoEscaneadoPendiente(
                        codigo,
                        numeroNotaVenta
                );

                // insertar en la base de datos
                long id = AppDatabase.getInstance(this)
                        .productoEscaneadoDao()
                        .insert(nuevo);
                Log.d("DB_DEBUG", "Producto insertado con ID: " + id);

                runOnUiThread(() -> {
                    productosEscaneados.add(new ProductoEscaneado(codigo));
                    adapter.notifyItemInserted(productosEscaneados.size() - 1);
                    actualizarResumen();

                    Log.d("UI_DEBUG", "Producto agregado a UI: " + codigo);

                    Snackbar.make(findViewById(android.R.id.content),
                            "Producto guardado: " + codigo,
                            Snackbar.LENGTH_SHORT).show();
                });
            } catch (SQLiteConstraintException e) {
                runOnUiThread(() ->
                    Toast.makeText(this, "Producto ya escaneado: " + codigo, Toast.LENGTH_SHORT).show());
                    Log.d("UI_DEBUG", "Intento de duplicar producto: " + codigo);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
        confirmDialog = null;
        super.onDestroy();
    }
}
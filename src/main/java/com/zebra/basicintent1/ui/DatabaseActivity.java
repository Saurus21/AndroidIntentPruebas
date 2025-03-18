package com.zebra.basicintent1.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.api.ApiClient;
import com.zebra.basicintent1.api.Inventario;
import com.zebra.basicintent1.api.InventarioApi;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.database.DatabaseAdapter;
import com.zebra.basicintent1.database.ScannedData;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        recyclerView = findViewById(R.id.recyclerViewDatabase);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Cargar datos desde la base de datos
        cargarDatosDesdeBaseDeDatos();

        // Configurar el botón para agregar datos
        Button btnAgregarDatos = findViewById(R.id.btnAgregarDatos);
        btnAgregarDatos.setOnClickListener(v -> mostrarDialogoAgregarInventario());
    }

    private void cargarDatosDesdeBaseDeDatos() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<ScannedData> scannedDataList = db.scannedDataDao().getAllScannedData();

            runOnUiThread(() -> {
                DatabaseAdapter adapter = new DatabaseAdapter(scannedDataList);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    private void mostrarDialogoAgregarInventario() {
        // inflar el layout del dialogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_scanneddata, null);

        EditText editTextData = dialogView.findViewById(R.id.editTextData);
        EditText editTextLabelType = dialogView.findViewById(R.id.editTextLabelType);
        EditText editTextTimestamp = dialogView.findViewById(R.id.editTextTimestamp);

        // crear el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Agregar Nuevo Registro")
                .setPositiveButton("Agregar", (dialog, which) -> {

                    // obtener los datos del formulario
                    String data = editTextData.getText().toString();
                    String labelType = editTextLabelType.getText().toString();
                    String timestamp = editTextTimestamp.getText().toString();

                    // validacion de datos
                    if (data.isEmpty() || labelType.isEmpty() || timestamp.isEmpty()) {
                        Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // crear un nuevo objeto Inventario
                    ScannedData nuevoDato = new ScannedData();
                    nuevoDato.setSource("Agregado Manualmente");
                    nuevoDato.setData(data);
                    nuevoDato.setLabelType(labelType);
                    nuevoDato.setTimestamp(timestamp);
                    //Log.d("API_DEBUG", "Nuevo Inventario creado: " + nuevoDato.toString());

                    agregarNuevoRegistroLocal(nuevoDato);
                })
                .setNegativeButton("Cancelar", null);

        // mostrar dialogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void agregarNuevoRegistroLocal(ScannedData scannedData) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                db.scannedDataDao().insert(scannedData);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registro agregado a la base de datos local", Toast.LENGTH_SHORT).show();
                    cargarDatosDesdeBaseDeDatos(); // Actualizar la lista de datos
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al agregar el registro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
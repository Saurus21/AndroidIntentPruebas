package com.zebra.basicintent1.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.zebra.basicintent1.database_oracle.InventarioRemotoAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class LeerDatosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private InventarioRemotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_datos);

        recyclerView = findViewById(R.id.recyclerViewInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAgregarDatos = findViewById(R.id.btnAgregarDatos);

        // obtener datos de la api
        obtenerDatosDeInventario();

        Inventario nuevoInventario = new Inventario();
        nuevoInventario.setCodigoBarra("codigoBarra");
        nuevoInventario.setFecha("fecha");
        nuevoInventario.setHh(12);
        nuevoInventario.setMm(30);

        // configuración de botón para los nuevos datos
        btnAgregarDatos.setOnClickListener(v -> agregarNuevoRegistro(nuevoInventario)); // antes tenia mostrarDialogoAgregarRegistro()
    }

    private void obtenerDatosDeInventario() {
        InventarioApi apiService = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);

        Call<List<Inventario>> call = apiService.getInventario();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Inventario>> call, @NonNull Response<List<Inventario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Inventario> inventarioList = response.body();
                    adapter = new InventarioRemotoAdapter(inventarioList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Inventario>> call, @NonNull Throwable t) {
                // manejar error
                t.printStackTrace();
            }
        });
    }

    private void mostrarDialogoAgregarInventario() {
        // inflar el layout del dialogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_inventario, null);

        EditText editTextCodigoBarra = dialogView.findViewById(R.id.editTextCodigoBarra);
        EditText editTextFecha = dialogView.findViewById(R.id.editTextFecha);
        EditText editTextHora = dialogView.findViewById(R.id.editTextHora);
        EditText editTextMinutos = dialogView.findViewById(R.id.editTextMinutos);

        // crear el dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Agregar Nuevo Registro")
                .setPositiveButton("Agregar", (dialog, which) -> {

                    // obtener los datos del formulario
                    String codigoBarra = editTextCodigoBarra.getText().toString();
                    String fecha = editTextFecha.getText().toString();
                    String horaStr = editTextHora.getText().toString();
                    String minutosStr = editTextMinutos.getText().toString();

                    // validacion de datos
                    if (codigoBarra.isEmpty() || fecha.isEmpty() || horaStr.isEmpty() || minutosStr.isEmpty()) {
                        Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /*
                    int hora, minutos;
                    try {
                        hora = Integer.parseInt(horaStr);
                        minutos = Integer.parseInt(minutosStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Hora y minutos deben ser números válidos", Toast.LENGTH_SHORT).show();
                        return;
                    } */

                    // crear un nuevo objeto Inventario
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setCodigoBarra(codigoBarra);
                    nuevoInventario.setFecha(fecha);
                    nuevoInventario.setHh(Integer.parseInt(horaStr));
                    nuevoInventario.setMm(Integer.parseInt(minutosStr));
                    Log.d("API_DEBUG", "Nuevo Inventario creado: " + nuevoInventario.toString());

                    agregarNuevoRegistro(nuevoInventario);
                })
                .setNegativeButton("Cancelar", null);

        // mostrar dialogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void agregarNuevoRegistro(Inventario inventario) {
        InventarioApi apiService = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);
        Call<Inventario> call = apiService.addInventario(inventario);

        call.enqueue(new Callback<Inventario>() {
            @Override
            public void onResponse(@NonNull Call<Inventario> call, @NonNull Response<Inventario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LeerDatosActivity.this, "Registro agregado", Toast.LENGTH_SHORT).show();
                    obtenerDatosDeInventario();
                } else {
                    // Log del error del servidor
                    Log.e("API_ERROR", "Código de error: " + response.code());
                    Log.e("API_ERROR", "Mensaje: " + response.message());

                    // intentar leer el cuerpo del error
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Cuerpo del error no disponible";
                        Log.e("API_ERROR", "Cuerpo de la respuesta: " + errorBody);
                    } catch (IOException e) {
                        Log.e("API_ERROR", "Error al leer el cuerpo del error: " + e.getMessage());
                    }

                    Toast.makeText(LeerDatosActivity.this, "Error en el servidor: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Inventario> call, @NonNull Throwable t) {
                Toast.makeText(LeerDatosActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
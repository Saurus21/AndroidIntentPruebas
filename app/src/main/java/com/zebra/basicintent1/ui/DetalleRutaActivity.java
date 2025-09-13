package com.zebra.basicintent1.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.zebra.basicintent1.R;
import com.zebra.basicintent1.api.ApiClient;
import com.zebra.basicintent1.api.AguaRuralApi;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.database.LecturaPendiente;
import com.zebra.basicintent1.database.LecturaPendienteAdapter;
import com.zebra.basicintent1.modelosDatos.Medidor;
import com.zebra.basicintent1.modelosDatos.MedidorAdapter;
import com.zebra.basicintent1.modelosDatos.Ruta;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleRutaActivity extends AppCompatActivity {
    private List<Medidor> medidoresEnRuta = new ArrayList<>();
    private List<LecturaPendiente> lecturasPendientes = new ArrayList<>();

    // adapters
    private MedidorAdapter medidorAdapter;
    private LecturaPendienteAdapter pendientesAdapter;

    private String idRuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta); // usar un nuevo layout

        // obtener la ruta del intent
        Ruta ruta = (Ruta) getIntent().getSerializableExtra("rutaSeleccionada");
        if (ruta == null) {
            Toast.makeText(this, "Error al cargar la ruta", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        idRuta = "R-" + ruta.getId(); // formatear el ID de la ruta
        setTitle("Medidores - " + ruta.getNombreRuta());

        // configurar RecyclerView para los medidores de la ruta
        RecyclerView rvMedidores = findViewById(R.id.recyclerViewMedidores);
        rvMedidores.setLayoutManager(new LinearLayoutManager(this));

        // adapter es MedidorAdapter
        medidorAdapter = new MedidorAdapter(ruta.getMedidores(), medidor -> {
            // logica para abrir la pantalla de registro de una lectura
            Intent intent = new Intent(DetalleRutaActivity.this, RegistrarLecturaActivity.class);
            intent.putExtra("medidor_id", medidor.getId());
            intent.putExtra("serial_medidor", medidor.getSerial());
            startActivity(intent);
        });
        rvMedidores.setAdapter(medidorAdapter);

        // configurar RecyclerView para las lecturas ya realizadas en esta ruta
        RecyclerView rvLecturasPendientes = findViewById(R.id.recyclerViewLecturasPendientes);
        rvLecturasPendientes.setLayoutManager(new LinearLayoutManager(this));

        pendientesAdapter = new LecturaPendienteAdapter(lecturasPendientes, position -> {
            // logica para eliminar una lectura pendiente
            eliminarLectura(position);
        });
        rvLecturasPendientes.setAdapter(pendientesAdapter);

        cargarLecturasGuardadas(); // cargar lecturas desde la BD local

        // iniciar la actividad para registrar una nueva lectura
        FloatingActionButton fabRegistrar = findViewById(R.id.fabRegistrarLectura);
        fabRegistrar.setOnClickListener(v -> {
            // NOTA: Aquí podrías pasar a una pantalla que muestre la lista de medidores
            // y al seleccionar uno, se abre la pantalla de registro.
            // Por simplicidad, aquí iniciamos directamente el registro.
            Intent intent = new Intent(DetalleRutaActivity.this, RegistrarLecturaActivity.class);
            intent.putExtra("id_ruta", idRuta);
            startActivityForResult(intent, 1); // '1' es un requestCode
        });
    }

    // se ejecuta cuando regresas de RegistrarLecturaActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // si el registro fue exitoso, recargamos la lista de lecturas pendientes
            cargarLecturasGuardadas();
        }
    }

    private void cargarLecturasGuardadas() {
        new Thread(() -> {
            List<LecturaPendiente> pendientes = AppDatabase.getInstance(this)
                    .lecturaPendienteDao()
                    .getLecturasPorRuta(idRuta);

            runOnUiThread(() -> {
                lecturasPendientes.clear();
                lecturasPendientes.addAll(pendientes);
                pendientesAdapter.notifyDataSetChanged();

                // logica para mostrar u ocultar la seccion de lecturas pendientes
                findViewById(R.id.tvLecturasPendientes).setVisibility(
                        pendientes.isEmpty() ? View.GONE : View.VISIBLE
                );
            });
        }).start();
    }

    private void eliminarLectura(int position) {
        // verificar posicion valida
        if (position == RecyclerView.NO_POSITION) return;

        new Thread(() -> {
            LecturaPendiente lectura = lecturasPendientes.get(position);
            int deletedRows = AppDatabase.getInstance(this).lecturaPendienteDao().delete(lectura);

            runOnUiThread(() -> {
                if (deletedRows > 0) {
                    // Usar notifyDataSetChanged como fallback seguro
                    cargarLecturasGuardadas();
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }


//    private void eliminarProducto(int position) {
//
//        Log.d("DELETE_DEBUG", "Intentando eliminar posición: " + position +
//                " - Tamaño lista: " + lecturasPendientes.size());
//
//        // verificar posicion valida
//        if (position == RecyclerView.NO_POSITION) return;
//
//        new Thread(() -> {
//            ProductoEscaneadoPendiente producto = productosPendientes.get(position);
//            int deletedRows = AppDatabase.getInstance(this).productoEscaneadoDao().delete(producto);
//
//            runOnUiThread(() -> {
//                if (deletedRows > 0) {
//                    // Usar notifyDataSetChanged como fallback seguro
//                    cargarPendientes();
//                } else {
//                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }).start();
//    }

    
    @Override
    public void onBackPressed() {
        // Enviar señal de actualización a NotasVentaActivity si es necesario
        super.onBackPressed();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("should_refresh", true);
        setResult(RESULT_OK, resultIntent);
        finish(); // Esto regresará naturalmente a NotasVentaActivity
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
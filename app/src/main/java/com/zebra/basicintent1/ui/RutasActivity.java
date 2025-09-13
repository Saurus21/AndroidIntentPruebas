package com.zebra.basicintent1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.api.ApiClient;
import com.zebra.basicintent1.api.AguaRuralApi;
import com.zebra.basicintent1.modelosDatos.Ruta;
import com.zebra.basicintent1.modelosDatos.RutaAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RutasActivity extends AppCompatActivity implements RutaAdapter.OnRutaClickListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RutaAdapter adapter;
    private List<Ruta> listaRutas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_venta);

        recyclerView = findViewById(R.id.recyclerViewNotasVenta);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RutaAdapter(listaRutas, this);
        recyclerView.setAdapter(adapter);

        if (listaRutas.isEmpty()) {
            cargarRutasAsignadas();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void cargarRutasAsignadas() {
        progressBar.setVisibility(View.VISIBLE);

        String estado = "A"; // cambiar estos datos para filtrar las notas por estado y fechas dto
        String fechaDesde = "2025-08-01";
        String fechaHasta = "2025-08-31";

//        AguaRuralApi apiService = ApiClient.getRetrofitInstance(this).create(AguaRuralApi.class);
//        Call<List<Ruta>> call = apiService.getRutasAsignadas("pensar que puede ir aqui");
//
//        call.enqueue(new Callback<List<Ruta>>() {
//            @Override
//            public void onResponse(Call<List<Ruta>> call, Response<List<Ruta>> response) {
//                progressBar.setVisibility(View.GONE);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    listaRutas.clear();
//                    listaRutas.addAll(response.body());
//
//                    adapter.notifyDataSetChanged();
//
//                } else {
//                    Toast.makeText(RutasActivity.this,
//                            "No se encontraron notas de venta",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Ruta>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(RutasActivity.this,
//                        "Error al cargar notas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.e("NotasVentaActivity", "Error al cargar notas: " + t.getMessage(), t);
//            }
//        });
    }

    @Override
    public void onRutaClick(Ruta ruta) {

        Intent intent = new Intent(this, DetalleRutaActivity.class);
        intent.putExtra("rutaSeleccionada", ruta);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("should_refresh", false)) {
            cargarRutasAsignadas(); // Recargar las notas si es necesario
        }
    }

}
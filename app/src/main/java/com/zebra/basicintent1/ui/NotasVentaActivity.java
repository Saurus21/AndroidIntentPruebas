package com.zebra.basicintent1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.api.ApiClient;
import com.zebra.basicintent1.api.InventarioApi;
import com.zebra.basicintent1.notaVenta.NotaVenta;
import com.zebra.basicintent1.notaVenta.NotaVentaAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotasVentaActivity extends AppCompatActivity implements NotaVentaAdapter.OnNotaVentaClickListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NotaVentaAdapter adapter;
    private List<NotaVenta> notasVenta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_venta);

        recyclerView = findViewById(R.id.recyclerViewNotasVenta);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotaVentaAdapter(notasVenta, this);
        recyclerView.setAdapter(adapter);

        if (notasVenta.isEmpty()) {
            cargarNotasVenta();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void cargarNotasVenta() {
        progressBar.setVisibility(View.VISIBLE);

        String estado = "A"; // cambiar estos datos para filtrar las notas por estado y fechas dto
        String fechaDesde = "2025-08-01";
        String fechaHasta = "2025-08-31";

        InventarioApi apiService = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);
        Call<List<NotaVenta>> call = apiService.getNotasVentaPendiente(estado, fechaDesde, fechaHasta);

        call.enqueue(new Callback<List<NotaVenta>>() {
            @Override
            public void onResponse(Call<List<NotaVenta>> call, Response<List<NotaVenta>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    notasVenta.clear();
                    notasVenta.addAll(response.body());

                    Collections.sort(notasVenta, (n1, n2) -> n2.getFecha().compareTo(n1.getFecha()));

                    adapter.notifyDataSetChanged();

                    // Log para verificar datos
                    for (NotaVenta nota : notasVenta) {
                        Log.d("NOTA_VENTA", "Nota #" + nota.getNumero() +
                                " - Cliente: " + nota.getUsuarioNombre() +
                                " - Detalles: " + nota.getDetalles().size());
                    }
                } else {
                    Toast.makeText(NotasVentaActivity.this,
                            "No se encontraron notas de venta",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotaVenta>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotasVentaActivity.this,
                        "Error al cargar notas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NotasVentaActivity", "Error al cargar notas: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onNotaVentaClick(NotaVenta notaVenta) {
        if (notaVenta.getDetalles() != null && !notaVenta.getDetalles().isEmpty()) {
            Intent intent = new Intent(this, ProductosNotaVentaActivity.class);
            intent.putExtra("notaVenta", notaVenta);

            if (notaVenta.getRutEmpresa() != null) {
                intent.putExtra("rutEmpresa", notaVenta.getRutEmpresa());
            }

            startActivity(intent);
        } else {
            Toast.makeText(this, "No se encontraron productos para esta nota", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("should_refresh", false)) {
            cargarNotasVenta(); // Recargar las notas si es necesario
        }
    }

}
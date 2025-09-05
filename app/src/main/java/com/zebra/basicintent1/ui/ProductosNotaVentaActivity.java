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
import com.zebra.basicintent1.api.InventarioApi;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.notaVenta.StockProducto;
import com.zebra.basicintent1.notaVenta.NotaVenta;
import com.zebra.basicintent1.notaVenta.ProductoAdapter;
import com.zebra.basicintent1.notaVenta.ProductoEscaneado;
import com.zebra.basicintent1.database.ProductoEscaneadoPendiente;
import com.zebra.basicintent1.notaVenta.ProductosEscaneadosAdapter;
import com.zebra.basicintent1.notaVenta.ProductosPendientesAdapter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosNotaVentaActivity extends AppCompatActivity {
    private List<NotaVenta.Detalle> detallesConProductos = new ArrayList<>();
    private List<ProductoEscaneado> productosEscaneados = new ArrayList<>();
    private ProductoAdapter productoAdapter;
    private ProductosEscaneadosAdapter escaneadosAdapter;

    private List<ProductoEscaneadoPendiente> productosPendientes = new ArrayList<>();
    private ProductosPendientesAdapter pendientesAdapter;
    private String numeroNotaVenta;
    private BigInteger rutEmpresa;
    private String p1;
    private String p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_nota_venta);


        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar boton de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Obtener la nota de venta del intent
        NotaVenta notaVenta = (NotaVenta) getIntent().getSerializableExtra("notaVenta");
        if (notaVenta == null) {
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (notaVenta != null && notaVenta.getRutEmpresa() != null) {
            rutEmpresa = notaVenta.getRutEmpresa();
        }

        numeroNotaVenta = "NV-" + notaVenta.getNumero();
        // formato ej: NV-12345
        Log.d("NOTA_VENTA", "Numero de nota: " + numeroNotaVenta);

        setTitle("Productos - NV-" + notaVenta.getNumero());

        // Configurar RecyclerView para productos de la nota
        RecyclerView rvProductos = findViewById(R.id.recyclerViewProductos);
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapter(detallesConProductos);
        rvProductos.setAdapter(productoAdapter);

        // Configurar RecyclerView para productos escaneados
        RecyclerView rvPendientes = findViewById(R.id.recyclerViewEscaneados);
        rvPendientes.setLayoutManager(new LinearLayoutManager(this));
        pendientesAdapter = new ProductosPendientesAdapter(productosPendientes, new ProductosPendientesAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                mostrarDialogoConfirmacionEliminar(position);
            }
        });
        rvPendientes.setAdapter(pendientesAdapter);

        escaneadosAdapter = new ProductosEscaneadosAdapter(productosEscaneados);

        cargarPendientes();


        // Preparar lista de productos
        this.detallesConProductos.clear();

        //List<NotaVenta.Detalle> detallesConProductos = new ArrayList<>();
        for (NotaVenta.Detalle detalle : notaVenta.getDetalles()) {
            if (detalle.getProductos() != null) {
                this.detallesConProductos.add(detalle);
            }
        }

        // Mostrar resumen en subtítulo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(this.detallesConProductos.size() + " productos");
        }

        // Configurar adaptador
        ProductoAdapter adapter = new ProductoAdapter(this.detallesConProductos);
        rvProductos.setAdapter(adapter);

        // Configurar botón flotante para escanear
        FloatingActionButton fabEscanear = findViewById(R.id.fabEscanear);
        fabEscanear.setOnClickListener(v -> {
            // Iniciar actividad de escaneo
            Intent intent = new Intent(ProductosNotaVentaActivity.this, EscanearProductosActivity.class);
            intent.putExtra("numero_nota", numeroNotaVenta);
            startActivityForResult(intent, 1);
        });

        // Configurar botón de sincronización
        configurarSincronizacion();

        // margen inferior para ambos rv
        addBottomMarginDecoration(rvProductos);
        addBottomMarginDecoration(rvPendientes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            cargarPendientes();
        }
    }

    private void mostrarDialogoConfirmacionEliminar(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que desea eliminar este producto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // verificar que la posicion sigue siendo valida
                    if (position >= 0 && position < productosPendientes.size()) {
                        eliminarProducto(position);
                    } else {
                        Log.e("DELETE_ERROR", "Posición inválida después de diálogo: " + position);
                        // Recargar datos si hay inconsistencia
                        cargarPendientes();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarProducto(int position) {

        Log.d("DELETE_DEBUG", "Intentando eliminar posición: " + position +
                " - Tamaño lista: " + productosPendientes.size());

        // verificar posicion valida
        if (position == RecyclerView.NO_POSITION) return;

        new Thread(() -> {
            ProductoEscaneadoPendiente producto = productosPendientes.get(position);
            int deletedRows = AppDatabase.getInstance(this).productoEscaneadoDao().delete(producto);

            runOnUiThread(() -> {
                if (deletedRows > 0) {
                    // Usar notifyDataSetChanged como fallback seguro
                    cargarPendientes();
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void procesarProductosEscaneados(List<ProductoEscaneado> productosEscaneados) {
        // logica, hecha funcion
    }

    private void cargarPendientes() {
        Log.d("DB_DEBUG", "Cargando pendientes para nota: " + numeroNotaVenta);

        new Thread(() -> {
            List<ProductoEscaneadoPendiente> pendientes = AppDatabase.getInstance(this)
                    .productoEscaneadoDao()
                    .getPendientesPorNota(numeroNotaVenta);

            Log.d("DB_DEBUG", "Productos obtenidos de DB: " + pendientes.size());

            runOnUiThread(() -> {
                // limpiar listas completamente
                productosPendientes.clear();
                productosEscaneados.clear();


                productosPendientes.addAll(pendientes);

                Log.d("UI_DEBUG", "Productos pendientes asignados: " + productosPendientes.size());

                for (ProductoEscaneadoPendiente p : pendientes) {
                    productosEscaneados.add(new ProductoEscaneado(p.codigo));
                }
                Log.d("UI_DEBUG", "Productos escaneados asignados: " + productosEscaneados.size());

                pendientesAdapter.notifyDataSetChanged();
                // escaneadosAdapter.notifyDataSetChanged();

                // mostrar / ocultar seccion segun datos
                int visibility = pendientes.isEmpty() ? View.GONE : View.VISIBLE;
                findViewById(R.id.tvProductosEscaneados).setVisibility(visibility);
                findViewById(R.id.recyclerViewEscaneados).setVisibility(visibility);

                Log.d("UI_DEBUG", "Actualización de UI completada");
            });
        }).start();
    }

    private void configurarSincronizacion() {
        Button btnSincronizar = findViewById(R.id.btnSincronizar);
        btnSincronizar.setOnClickListener(v -> sincronizarEscaneos());

    }

    private void sincronizarEscaneos() {
        if (productosPendientes.isEmpty()) {
            Toast.makeText(this, "No hay productos escaneados", Toast.LENGTH_SHORT).show();
            return;
        }

        for (ProductoEscaneadoPendiente producto : productosPendientes) {
            verificarProductoSimple(producto.codigo);
        }
    }

    private void verificarProductoSimple(String codigo) {

        if (rutEmpresa == null) {
            Toast.makeText(this, "No se pudo obtener RUT de empresa", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verificando producto...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        InventarioApi service = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);
        Call<List<StockProducto>> call = service.verificarProducto(BigInteger.valueOf(77383986), codigo); //TODO, cambiar rutEmpresa
        // Call<List<Detalle>> call = service.verificarProducto(rutEmpresa, codigo);

        call.enqueue(new Callback<List<StockProducto>>() {
            @Override
            public void onResponse(Call<List<StockProducto>> call, Response<List<StockProducto>> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    // Log de depuración
                    try {
                        String rawResponse = response.body() != null ?
                                new Gson().toJson(response.body()) : "null body";
                        // Log.d("API_RESPONSE", "Respuesta cruda: " + rawResponse);
                    } catch (Exception e) {
                        Log.e("API_RESPONSE", "Error al loguear respuesta", e);
                    }

                    if (response.body() != null) {
                        procesarRespuestaProducto(codigo, response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<StockProducto>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProductosNotaVentaActivity.this,
                        "Error al verificar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Error al verificar producto", t);
            }
        });
    }

    private void procesarRespuestaProducto(String codigo, List<StockProducto> stockProductos) {

        if (stockProductos == null || stockProductos.isEmpty()) {
            Toast.makeText(this, "❌ Producto " + codigo + " no encontrado",
                    Toast.LENGTH_SHORT).show();
            Log.i("API_RESPONSE", "❌ Producto " + codigo + " no encontrado");
            return;
        } else {
            Toast.makeText(this, "✅ Producto " + codigo + " encontrado",
                    Toast.LENGTH_SHORT).show();
            Log.i("API_RESPONSE", "✅ Producto " + codigo + " encontrado");
        }

        // tomar el primer resultado
        StockProducto stockProducto = stockProductos.get(0);

        // Verificar que los campos esenciales no sean nulos
        if (stockProducto.getNroUm() == null || stockProducto.getCdgItem() == null) {
            Toast.makeText(this, "⚠ Datos incompletos para producto " + codigo,
                    Toast.LENGTH_SHORT).show();
            // return;
        }

        // loggear
        Log.d("STOCK_ENCONTRADO", "Stock completo: " + stockProducto.toString());


        // logica de comparacion de stock

        String codigoItemStock = stockProducto.getCdgItem();
        Log.i("API_RESPONSE", "Código de item stock: " + codigoItemStock);

        String stockP1 = stockProducto.getP1();
        String stockP2 = stockProducto.getP2();

        boolean productoEncontrado = false;
        boolean parametrosCoinciden = false;

        for (NotaVenta.Detalle detalle : detallesConProductos) {

            if (detalle.getProductos() != null && !detalle.getProductos().isEmpty()) {

                for (NotaVenta.Producto productoNota : detalle.getProductos()) {

                    String codigoProductoNota = productoNota.getCodigo();
                    Log.i("API_RESPONSE", "Código de producto nota: " + codigoProductoNota);

                    if (java.util.Objects.equals(codigoItemStock, codigoProductoNota)) {
                        productoEncontrado = true;

                        String notaP1 = detalle.getP1();
                        String notaP2 = detalle.getP2();
                        Log.i("COMPARACION", "Stock -> p1: " + stockP1 + ", p2: " + stockP2);
                        Log.i("COMPARACION", "Nota Venta -> p1: " + notaP1 + ", p2: " + notaP2);

                        if (java.util.Objects.equals(stockP1, notaP1) &&
                            java.util.Objects.equals(stockP2, notaP2)) {
                            parametrosCoinciden = true;
                        }

                        break;
                    }
                }
            }

            if (productoEncontrado) {
                break;
            }
        }

        // resultados
        if (productoEncontrado) {
            if (parametrosCoinciden) {
                // producto y los parametros coinciden
                Toast.makeText(this, "✅ Producto y parámetros OK", Toast.LENGTH_LONG).show();
                Log.i("API_RESPONSE", "✅ ÉXITO: Producto " + codigoItemStock + " y parámetros coinciden.");
            } else {
                // producto es correcto, pero los parametros (p1, p2) no
                Toast.makeText(this, "⚠️ Parámetros no coinciden", Toast.LENGTH_LONG).show();
                Log.w("API_RESPONSE", "⚠️ ALERTA: Producto " + codigoItemStock + " encontrado, pero P1/P2 no coinciden.");
            }
        } else {
            // producto escaneado no existe en la nota de venta
            Toast.makeText(this, "❌ Producto no está en la nota", Toast.LENGTH_LONG).show();
            Log.e("API_RESPONSE", "❌ ERROR: Producto " + codigoItemStock + " no figura en la nota de venta.");
        }
    }

    private boolean enviarAServidor(String codigoProducto) {
        // Implementa tu lógica de API aquí
        // Retorna true si fue exitoso
        return true; // simulación
    }

    private void addBottomMarginDecoration(RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 32;
                }
            }
        });
    }
    
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
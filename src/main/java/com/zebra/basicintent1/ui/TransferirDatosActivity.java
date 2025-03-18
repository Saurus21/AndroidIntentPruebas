package com.zebra.basicintent1.ui;

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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class TransferirDatosActivity extends AppCompatActivity {

    private static final String TAG = "TransferirDatosActivity"; // etiqueta para los logs
    private ProgressBar progressBar;
    private AppDatabase db; // db local (room)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferir_datos);

        Button buttonTransferir = findViewById(R.id.buttonTransferir);
        progressBar = findViewById(R.id.progressBar);

        // inicializar la base de datos local (room)
        db = AppDatabase.getInstance(this);

        buttonTransferir.setOnClickListener(v -> {
            Log.d(TAG, "Botón Transferir presionado"); // log para rastrear el clic del botón
            transferirDatos();
        });
    }

    private void transferirDatos() {
        Log.d(TAG, "Iniciando transferencia de datos"); // log para rastrear el inicio de la transferencia

        // mostrar el ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // obtener los datos de la bd en hilo secundario
        new Thread(() -> {
            // obtener los datos de la bd en hilo secundario
            List<ScannedData> datosLocales = db.scannedDataDao().getAllScannedData();
            Log.d(TAG, "Datos locales obtenidos: " + datosLocales.size()); // log para rastrear la cantidad de datos locales

            // transferir los datos a la bd externa
            for (ScannedData datoLocal : datosLocales) {
                Log.d(TAG, "Procesando dato local: " + datoLocal.getData()); // log para rastrear cada dato local
                transferirDato(datoLocal);
            }

            // ocultar el ProgressBar
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransferirDatosActivity.this, "Transferencia completa", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Transferencia de datos completada");
            });
        }).start();
    }

    private void transferirDato(ScannedData datoLocal) {
        try {
            Log.d(TAG, "Iniciando transferencia de dato: " + datoLocal.getData()); // log para rastrear el inicio de la transferencia de un dato

            // parsear el timestamp para obtener fecha, hora y minuto
            String fecha = TimestampParser.obtenerFecha(datoLocal.getTimestamp());
            int hora = TimestampParser.obtenerHora(datoLocal.getTimestamp());
            int minuto = TimestampParser.obtenerMinuto(datoLocal.getTimestamp());

            // verificar si fecha es nula
            if (fecha == null) {
                fecha = "0000-00-00";
                Log.w(TAG, "Fecha nula o inválida, usando valor por defecto: 0000-00-00"); // log para rastrear fechas nulas
            }

            if (hora == -1) {
                hora = 0;
                Log.w(TAG, "Hora inválida, usando valor por defecto: 0"); // log para rastrear horas inválidas
            }

            if (minuto == -1) {
                minuto = 0;
                Log.w(TAG, "Minuto inválido, usando valor por defecto: 0"); // log para rastrear minutos inválidos
            }

            // verificar si el dato ya existe en la base de datos externa
            InventarioApi inventarioApi = ApiClient.getRetrofitInstance(this).create(InventarioApi.class);
            Call<List<Inventario>> call = inventarioApi.getScannedDataPorCodigo(datoLocal.getData()); // creo que aca falla en la verificacion, ya que siempre se va al else
            // TODO, aca falla ya que en vez de buscar el codigo de barra busca la id, arreglar

            Response<List<Inventario>> response = call.execute();
            if (response.isSuccessful() && response.body() != null && response.body().isEmpty()) {
                Log.d(TAG, "Dato no existe en la base de datos externa, procediendo a transferir"); // log para rastrear la transferencia de un dato nuevo

                // el dato no existe en la base de datos externa, transferirlo
                Inventario nuevoInventario = new Inventario();
                nuevoInventario.setCodigoBarra(datoLocal.getData());  // usar el campo "data" como código de barras
                nuevoInventario.setFecha(fecha);
                nuevoInventario.setHh(hora);  // hora y minutos
                nuevoInventario.setMm(minuto);

                Call<Inventario> callAgregar = inventarioApi.addInventario(nuevoInventario);
                callAgregar.execute();

                Log.d(TAG, "Dato transferido exitosamente: " + datoLocal.getData()); // log para rastrear la transferencia exitosa

                // eliminar el dato de la base de datos local (opcional)
                //db.scannedDataDao().delete(datoLocal); // falta implementar
            } else {
                Log.d(TAG, "Dato ya existe en la base de datos externa, omitiendo: " + datoLocal.getData()); // log para rastrear datos duplicados
            }
        } catch (IOException e) {
            Log.e(TAG, "Error de red al transferir dato: " + datoLocal.getData(), e); // log para rastrear errores de red
            runOnUiThread(() -> Toast.makeText(TransferirDatosActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
        }
    }

    public static class TimestampParser {
        public static String obtenerFecha(String timestamp) {
            if (timestamp == null || timestamp.isEmpty()) {
                Log.w("TimestampParser", "Timestamp nulo o vacío, retornando null"); // log para rastrear timestamps nulos
                return null;  // retorna null si el timestamp es null o vacío
            }

            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = formatoEntrada.parse(timestamp);
                return formatoFecha.format(date);
            } catch (ParseException e) {
                Log.e("TimestampParser", "Error al parsear fecha: " + timestamp, e); // log para rastrear errores de parsing
                return null;  // retorna null si hay un error al parsear
            }
        }

        public static int obtenerHora(String timestamp) {
            if (timestamp == null || timestamp.isEmpty()) {
                Log.w("TimestampParser", "Timestamp nulo o vacío, retornando -1"); // log para rastrear timestamps nulos
                return -1; // retorna -1 si el timestamp es null o vacío
            }

            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoHora = new SimpleDateFormat("HH", Locale.getDefault());
            try {
                Date date = formatoEntrada.parse(timestamp);
                return Integer.parseInt(formatoHora.format(date));
            } catch (ParseException e) {
                Log.e("TimestampParser", "Error al parsear hora: " + timestamp, e); // log para rastrear errores de parsing
                return -1;
            }
        }

        public static int obtenerMinuto(String timestamp) {
            if (timestamp == null || timestamp.isEmpty()) {
                Log.w("TimestampParser", "Timestamp nulo o vacío, retornando -1"); // log para rastrear timestamps nulos
                return -1; // retorna -1 si el timestamp es null o vacío
            }

            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoMinuto = new SimpleDateFormat("mm", Locale.getDefault());
            try {
                Date date = formatoEntrada.parse(timestamp);
                return Integer.parseInt(formatoMinuto.format(date));
            } catch (ParseException e) {
                Log.e("TimestampParser", "Error al parsear minuto: " + timestamp, e); // log para rastrear errores de parsing
                return -1;
            }
        }
    }
}
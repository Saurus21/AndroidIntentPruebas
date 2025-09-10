package com.zebra.basicintent1.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.modelosDatos.LecturaPendiente;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarLecturaActivity extends AppCompatActivity {

    private EditText etValorLectura;
    private EditText etObservacion;
    private Button btnGuardarLectura;
    private int medidorId;         // recibido del intent
    private String serialMedidor;  // recibido del intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_lectura);

        // obtener los datos del intent
        medidorId = getIntent().getIntExtra("medidor_id", -1);
        serialMedidor = getIntent().getStringExtra("serial_medidor");

        etValorLectura = findViewById(R.id.etValorLectura);
        etObservacion = findViewById(R.id.etObservacion);
        btnGuardarLectura = findViewById(R.id.btnGuardarLectura);

        btnGuardarLectura.setOnClickListener(v -> {
            String valorStr = etValorLectura.getText().toString();
            if (valorStr.isEmpty()) {
                Toast.makeText(this, "El valor no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            double valor = Double.parseDouble(valorStr);
            String observacion = etObservacion.getText().toString();

            guardarLecturaLocal(valor, observacion);
        });
    }

    private void guardarLecturaLocal(double valor, String observacion) {
        // fecha y hora actual
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        LecturaPendiente nuevaLectura = new LecturaPendiente(
                medidorId,
                serialMedidor,
                valor,
                observacion,
                timestamp
        );

        // guardar en la base de datos
        new Thread(() -> {
            try {
                AppDatabase.getInstance(this).lecturaPendienteDao().insert(nuevaLectura);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lectura guardada localmente", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar la lectura: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
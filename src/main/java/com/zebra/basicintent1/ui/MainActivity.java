// **********************************************************************************************
// *                                                                                            *
// *    This application is intended for demonstration purposes only. It is provided as-is      *
// *    without guarantee or warranty and may be modified to suit individual needs.             *
// *                                                                                            *
// **********************************************************************************************

package com.zebra.basicintent1.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.zebra.basicintent1.database.AppDatabase;
import com.zebra.basicintent1.R;
import com.zebra.basicintent1.database.ScannedData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate: Activity started");

        IntentFilter filter = new IntentFilter();

        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(myBroadcastReceiver, filter);

        // activar el scanner
        Button btnActivateScanner = findViewById(R.id.btnActivateScanner);
        btnActivateScanner.setOnClickListener(v -> {
            Log.d(LOG_TAG, "btnActivateScanner clicked");
            toggleScanner(btnActivateScanner);
        });

        // mostrar los datos guardados en la base de datos
        Button btnViewDatabase = findViewById(R.id.btnViewDatabase);
        btnViewDatabase.setOnClickListener(v -> {
            Log.d(LOG_TAG, "btnViewDatabase clicked");
            Intent intent = new Intent(MainActivity.this, DatabaseActivity.class);
            startActivity(intent);
        });
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();

            Log.d(LOG_TAG, "onRecieve: Received intent with action: " + action);

            // logear los tags que pasan al intent

            assert action != null;
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                Log.d("BROADCAST", "Broadcast received!");

                if (extras != null) {
                    Log.d("Scanned Data", "onRecieve: Intent Extras:");
                    for (String key : extras.keySet()) {
                        Log.d("Scanned Data", "Key: " + key + ", Value: " + extras.getString(key));
                    }
                }
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    Log.e("Scanned Data", "Error al mostrar los resultados: " + e.getMessage());
                }
            }
        }
    };

    private void toggleScanner(Button button) {
        Intent intent = new Intent();
        intent.setAction("com.symbol.datawedge.api.ACTION");

        Log.d(LOG_TAG, "toggleScanner: Sending intent");

        intent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "TOGGLE_SCANNING");
        button.setText("Toggle Scanner");
        Snackbar.make(button, "Toggling Scanner", Snackbar.LENGTH_SHORT).show();

        sendBroadcast(intent);
    }

    private void displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        Log.d(LOG_TAG, "displayScanResult: Processing intent");

        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        //String decodedData = initiatingIntent.getStringExtra("com.symbol.datawedge.decode_data");
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

        Log.d(LOG_TAG, "displayScanResult: Decoded Source: " + decodedSource);
        Log.d(LOG_TAG, "displayScanResult: Decoded Data: " + decodedData);
        Log.d(LOG_TAG, "displayScanResult: Decoded Label Type: " + decodedLabelType);

        final TextView lblScanSource = findViewById(R.id.lblScanSource);
        final TextView lblScanData = findViewById(R.id.lblScanData);
        final TextView lblScanLabelType = findViewById(R.id.lblScanDecoder);

        lblScanSource.setText(decodedSource != null ? decodedSource + " " + howDataReceived : "No source");
        lblScanData.setText(decodedData != null ? decodedData : "No data");
        lblScanLabelType.setText(decodedLabelType != null ? decodedLabelType : "No label type");

        //obtener fecha y hora del dispositivo
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // guardar los datos en la base de datos
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            ScannedData scannedData = new ScannedData();

            scannedData.setSource(decodedSource);
            scannedData.setData(decodedData);
            scannedData.setLabelType(decodedLabelType);
            scannedData.setTimestamp(timestamp);

            db.scannedDataDao().insert(scannedData);
            Log.d("Database", "Data inserted: " + scannedData.getData());
        }).start();
    }
}

// **********************************************************************************************
// *                                                                                            *
// *    This application is intended for demonstration purposes only. It is provided as-is      *
// *    without guarantee or warranty and may be modified to suit individual needs.             *
// *                                                                                            *
// **********************************************************************************************

package com.zebra.basicintent1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //
    // The section snippet below registers to receive the data broadcast from the
    // DataWedge intent output. In the example, a dynamic broadcast receiver is
    // registered in the onCreate() call of the target app. Notice that the filtered action
    // matches the "Intent action" specified in the DataWedge Intent Output configuration.
    //
    // For a production app, a more efficient way to the register and unregister the receiver
    // might be to use the onResume() and onPause() calls.

    // Note: If DataWedge had been configured to start an activity (instead of a broadcast),
    // the intent could be handled in the app's manifest by calling getIntent() in onCreate().
    // If configured as startService, then a service must be created to receive the intent.
    //

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

        // Mostrar los datos guardados en la base de datos
        displayStoredData();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy: Activity destroyed");
        unregisterReceiver(myBroadcastReceiver);
    }

    //
    // After registering the broadcast receiver, the next step (below) is to define it.
    // Here it's done in the MainActivity.java, but also can be handled by a separate class.
    // The logic of extracting the scanned data and displaying it on the screen
    // is executed in its own method (later in the code). Note the use of the
    // extra keys defined in the strings.xml file.
    //
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();

            Log.d(LOG_TAG, "onRecieve: Received intent with action: " + action);

            // logear los tags que pasan al intent

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

        sendBroadcast(intent);
    }

    //
    // The section below assumes that a UI exists in which to place the data. A production
    // application would be driving much of the behavior following a scan.
    //

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

        // Guardar los datos en la base de datos
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            ScannedData scannedData = new ScannedData();

            scannedData.setSource(decodedSource);
            scannedData.setData(decodedData);
            scannedData.setLabelType(decodedLabelType);

            db.scannedDataDao().insert(scannedData);
            Log.d("Database", "Data inserted: " + scannedData.getData());
        }).start();
    }

    private void displayStoredData() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<ScannedData> dataList = db.scannedDataDao().getAllScannedData();

            for (ScannedData data : dataList) {
                Log.d("Database", "ID: " + data.getId() + ", Source: " + data.getSource() +
                        ", Data: " + data.getData() + ", Label Type: " + data.getLabelType());
            }
        }).start();
    }
}


package com.zebra.basicintent1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.basicintent1.LoginActivity;
import com.zebra.basicintent1.R;
import com.zebra.basicintent1.utils.CookieManager;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // botón para ir a MainActivity (escaneo de datos)
        // aca se cambia los valores de los botones, ya sea imagen o texto

        View scannerButton = findViewById(R.id.btnScannerActivity);
        ((TextView) scannerButton.findViewById(R.id.dashboardButtonText)).setText("Scanner");
        ((ImageView) scannerButton.findViewById(R.id.dashboardButtonIcon)).setImageResource(R.drawable.barcode_scanner);

        scannerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // botón para ir a DatabaseActivity

        View databaseButton = findViewById(R.id.btnDatabaseActivity);
        ((TextView) databaseButton.findViewById(R.id.dashboardButtonText)).setText("Lecturas\nRealizadas");
        ((ImageView) databaseButton.findViewById(R.id.dashboardButtonIcon)).setImageResource(R.drawable.server);

        databaseButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        });

        // botón para ir a LeerDatosActivity

        View cargarDatosButton = findViewById(R.id.btnCargarDatos);
        ((TextView) cargarDatosButton.findViewById(R.id.dashboardButtonText)).setText("Leer Datos\nBase Central");
        ((ImageView) cargarDatosButton.findViewById(R.id.dashboardButtonIcon)).setImageResource(R.drawable.down_arrow);

        cargarDatosButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LeerDatosActivity.class);
            startActivity(intent);
        });

        // botón para ir a TransferirDatosActivity

        View transferirDatosButton = findViewById(R.id.btnTransferirDatos);
        ((TextView) transferirDatosButton.findViewById(R.id.dashboardButtonText)).setText("Transferir Datos\na Base Central");
        ((ImageView) transferirDatosButton.findViewById(R.id.dashboardButtonIcon)).setImageResource(R.drawable.up_arrow);

        transferirDatosButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransferirDatosActivity.class);
            startActivity(intent);
        });

        // botón para cerrar sesión

        View logoutButton = findViewById(R.id.btnLogout);
        ((TextView) logoutButton.findViewById(R.id.dashboardButtonText)).setText("Cerrar Sesión");
        ((ImageView) logoutButton.findViewById(R.id.dashboardButtonIcon)).setImageResource(R.drawable.logout);

        logoutButton.setOnClickListener(v -> {
            // eliminar las cookies
            CookieManager cookieManager = new CookieManager(DashboardActivity.this);
            cookieManager.logCookies();
            cookieManager.clearCookies();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // cerrar DashboardActivity para que no se pueda volver atrás
        });

    }
}

package com.zebra.basicintent1.notaVenta;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.ui.ProductosNotaVentaActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotaVentaAdapter extends RecyclerView.Adapter<NotaVentaAdapter.NotaVentaViewHolder> {

    private List<NotaVenta> notasVenta;
    private Context context;

    public interface OnNotaVentaClickListener {
        void onNotaVentaClick(NotaVenta notaVenta);
    }

    public NotaVentaAdapter(List<NotaVenta> notasVenta, Context context) {
        this.notasVenta = notasVenta;
        this.context = context;
    }

    @NonNull
    @Override
    public NotaVentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nota_venta, parent, false);
        return new NotaVentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaVentaViewHolder holder, int position) {
        NotaVenta nota = notasVenta.get(position);

        holder.tvNumero.setText("NV-" + nota.getNumero());

        // Formatear fecha
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(nota.getFecha());
            holder.tvFecha.setText("Fecha: " + outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvFecha.setText("Fecha: " + nota.getFecha());
        }

        holder.tvUsuario.setText("Cliente: " + (nota.getClienteNombre() != null ? nota.getClienteNombre() : "Sin cliente"));
        holder.tvEstado.setText("Estado: " + (nota.getEstado().equals("A") ? "Activo" : "Inactivo"));

        // calcular total sumando los montos netos de los detalles
        double total = 0;
        for (NotaVenta.Detalle detalle : nota.getDetalles()) {
            total += detalle.getMontoNeto();
        }
        holder.tvTotal.setText(String.format("Total: $%,.0f", total));

        holder.itemView.setOnClickListener(v -> {
            if (nota.getDetalles() != null && !nota.getDetalles().isEmpty()) {
                Intent intent = new Intent(context, ProductosNotaVentaActivity.class);
                intent.putExtra("notaVenta", nota);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No hay productos asociados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notasVenta.size();
    }

    public static class NotaVentaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero, tvFecha, tvUsuario, tvEstado, tvTotal;

        public NotaVentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumero = itemView.findViewById(R.id.tvNumeroNota);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}

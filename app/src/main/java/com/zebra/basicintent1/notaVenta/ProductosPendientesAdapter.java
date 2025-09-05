package com.zebra.basicintent1.notaVenta;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.database.ProductoEscaneadoPendiente;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductosPendientesAdapter extends RecyclerView.Adapter<ProductosPendientesAdapter.ViewHolder> {
    private final List<ProductoEscaneadoPendiente> productos;
    private OnItemClickListener listener;

    public ProductosPendientesAdapter(List<ProductoEscaneadoPendiente> productos, OnItemClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_pendiente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductoEscaneadoPendiente producto = productos.get(position);
        holder.tvCodigo.setText(producto.codigo);

        // Formatear fecha
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvHora.setText(sdf.format(new Date(producto.fechaEscaneo)));

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < productos.size()) {
            productos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, productos.size());
        }
    }

    /*
    public void removeItem(int position) {
        if (position >= 0 && position < productos.size()) {
            datos.remove(position);
            notifyItemRemoved(position);
        }
    }
    */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvHora;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvCodigoPendiente);
            tvHora = itemView.findViewById(R.id.tvHoraEscaneo);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
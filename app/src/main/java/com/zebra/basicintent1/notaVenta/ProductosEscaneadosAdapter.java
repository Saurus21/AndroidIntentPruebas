package com.zebra.basicintent1.notaVenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;

import java.util.List;

public class ProductosEscaneadosAdapter extends RecyclerView.Adapter<ProductosEscaneadosAdapter.ViewHolder> {
    private final List<ProductoEscaneado> productos;

    public ProductosEscaneadosAdapter(List<ProductoEscaneado> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_escaneado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvCodigo.setText(productos.get(position).getCodigo());
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo;
        TextView tvInfoAdicional;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvCodigo);
            tvInfoAdicional = itemView.findViewById(R.id.tvInfoAdicional);
        }
    }
}
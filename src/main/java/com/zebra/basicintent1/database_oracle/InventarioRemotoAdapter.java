package com.zebra.basicintent1.database_oracle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;
import com.zebra.basicintent1.api.Inventario;

import java.util.List;

public class InventarioRemotoAdapter extends RecyclerView.Adapter<InventarioRemotoAdapter.InventarioViewHolder> {
    private final List<Inventario> inventarioList;

    public InventarioRemotoAdapter(List<Inventario> inventarioList) {
        this.inventarioList = inventarioList;
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventario_remoto, parent, false);
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Inventario inventario = inventarioList.get(position);
        holder.textViewId.setText("ID: " + inventario.getId());
        holder.textViewCodigoBarra.setText("Código de Barra: " + inventario.getCodigoBarra());
        holder.textViewFecha.setText("Fecha: " + inventario.getFecha());
        holder.textViewHora.setText("Hora: " + inventario.getHh() + ":" + inventario.getMm());
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }

    public static class InventarioViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, textViewCodigoBarra, textViewFecha, textViewHora;

        public InventarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.textViewId);
            textViewCodigoBarra = itemView.findViewById(R.id.textViewCodigoBarra);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewHora = itemView.findViewById(R.id.textViewHora);
        }
    }
}

package com.zebra.basicintent1.modelosDatos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;

import java.util.List;

public class MedidorAdapter extends RecyclerView.Adapter<MedidorAdapter.MedidorViewHolder> {

    private final List<Medidor> listaMedidores;
    private OnMedidorClickListener clickListener;

    public MedidorAdapter(List<Medidor> listaMedidores, OnMedidorClickListener listener) {
        this.listaMedidores = listaMedidores;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public MedidorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medidor, parent, false);
        return new MedidorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedidorViewHolder holder, int position) {
        Medidor medidor = listaMedidores.get(position);
        holder.bind(medidor, clickListener);
    }

    @Override
    public int getItemCount() {
        return listaMedidores.size();
    }

    public interface OnMedidorClickListener {
        void onMedidorClick(Medidor medidor);
    }

    static class MedidorViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerialMedidor;
        TextView tvDireccion;

        public MedidorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerialMedidor = itemView.findViewById(R.id.tvSerialMedidor);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
        }

        public void bind(final Medidor medidor, final OnMedidorClickListener listener) {
            tvSerialMedidor.setText("Serial: " + medidor.getSerial());
            tvDireccion.setText(medidor.getDireccion());
            itemView.setOnClickListener(v -> listener.onMedidorClick(medidor));
        }
    }
}
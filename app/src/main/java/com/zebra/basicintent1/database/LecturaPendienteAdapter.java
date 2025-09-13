package com.zebra.basicintent1.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;

import java.util.List;

public class LecturaPendienteAdapter extends RecyclerView.Adapter<LecturaPendienteAdapter.LecturaViewHolder> {

    private final List<LecturaPendiente> listaLecturas;
    private final OnItemClickListener listener;

    public LecturaPendienteAdapter(List<LecturaPendiente> listaLecturas, OnItemClickListener listener) {
        this.listaLecturas = listaLecturas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LecturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lectura_pendiente, parent, false);
        return new LecturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LecturaViewHolder holder, int position) {
        LecturaPendiente lectura = listaLecturas.get(position);
        holder.bind(lectura, listener);
    }

    @Override
    public int getItemCount() {
        return listaLecturas.size();
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    static class LecturaViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerialMedidor;
        TextView tvValorLectura;
        TextView tvTimestamp;
        ImageButton btnDelete;

        public LecturaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerialMedidor = itemView.findViewById(R.id.tvSerialMedidorPendiente);
            tvValorLectura = itemView.findViewById(R.id.tvValorLectura);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDeleteLectura);
        }

        public void bind(final LecturaPendiente lectura, final OnItemClickListener listener) {
            tvSerialMedidor.setText("Serial: " + lectura.serialMedidor);
            tvValorLectura.setText(String.format("Valor: %.2f", lectura.valor));
            tvTimestamp.setText(lectura.timestamp);
            btnDelete.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}

package com.zebra.basicintent1.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;

import java.util.List;

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.ViewHolder> {

    private List<LecturaPendiente> lecturasPendientes;

    public DatabaseAdapter(List<LecturaPendiente> LecturaPendiente) {
        this.lecturasPendientes = lecturasPendientes;
    }

    public void actualizarDatos(List<LecturaPendiente> nuevosDatos) {
        this.lecturasPendientes = nuevosDatos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scanned_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ScannedData data = lecturasPendientes.get(position);
//
//        holder.tvSource.setText("Source: " + data.getSource());
//        holder.tvData.setText("Data: " + data.getData());
//        holder.tvLabelType.setText("Label Type: " + data.getLabelType());
//        holder.tvTimestamp.setText("Timestamp: " + data.getTimestamp());
//
//        String syncStatus = data.getSyncStatus() == 1 ? "Sincronizado" : "No sincronizado";
//        holder.tvSyncStatus.setText("Sync Status: " + syncStatus);
    }

    @Override
    public int getItemCount() {
        return lecturasPendientes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource, tvData, tvLabelType, tvTimestamp, tvSyncStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvData = itemView.findViewById(R.id.tvData);
            tvLabelType = itemView.findViewById(R.id.tvLabelType);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvSyncStatus = itemView.findViewById(R.id.tvSyncStatus);
        }
    }
}

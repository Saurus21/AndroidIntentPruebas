package com.zebra.basicintent1.modelosDatos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;

import java.util.List;

public class RutaAdapter extends RecyclerView.Adapter<RutaAdapter.RutaViewHolder> {

    private final List<Ruta> listaRutas;
    private final OnRutaClickListener clickListener;

    public RutaAdapter(List<Ruta> listaRutas, OnRutaClickListener clickListener) {
        this.listaRutas = listaRutas;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruta, parent, false); // Usar un nuevo layout 'item_ruta.xml'
        return new RutaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        Ruta rutaActual = listaRutas.get(position);
        holder.bind(rutaActual, clickListener);
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    // Interfaz para manejar los clics en cada item de la lista.
    public interface OnRutaClickListener {
        void onRutaClick(Ruta ruta);
    }

    // ViewHolder que contiene los elementos de la UI para cada ruta.
    static class RutaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreRuta;
        TextView tvFecha;
        TextView tvCantidadMedidores;

        public RutaViewHolder(@NonNull View itemView) {
            super(itemView);

            // vincular vistas del layout 'item_ruta.xml'
            tvNombreRuta = itemView.findViewById(R.id.tvNombreRuta);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvCantidadMedidores = itemView.findViewById(R.id.tvCantidadMedidores);
        }

        public void bind(final Ruta ruta, final OnRutaClickListener listener) {
            tvNombreRuta.setText(ruta.getNombreRuta());
            tvFecha.setText(ruta.getFechaAsignada());
            tvCantidadMedidores.setText(String.format("%d medidores", ruta.getMedidores().size()));
            itemView.setOnClickListener(v -> listener.onRutaClick(ruta));
        }
    }
}
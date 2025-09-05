package com.zebra.basicintent1.notaVenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.basicintent1.R;

import java.text.BreakIterator;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private List<NotaVenta.Detalle> detalles;

    public ProductoAdapter(List<NotaVenta.Detalle> detalles) {
        this.detalles = detalles;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        NotaVenta.Detalle detalle = detalles.get(position);
        NotaVenta.Producto producto = detalle.getProductos().get(0);

        holder.tvCodigo.setText("Código: " + producto.getCodigo());
        holder.tvNombre.setText(producto.getNombre());

        holder.tvCantidad.setText(String.format("Cant: %.2f %s",
                detalle.getCantidad(),
                detalle.getUnidadMedida()));

        holder.tvEstado.setText("Estado: " + (producto.getVigente().equalsIgnoreCase("s") ? "Activo" : "Inactivo"));
        holder.tvTipo.setText("Tipo: " + getTipoProducto(producto.getTipo()));
    }

    private String getTipoProducto(int tipo) {
        switch (tipo) {
            case 1: return "Producto";
            case 2: return "Insumo";
            case 3: return "Servicio";
            default: return "Desconocido";
        }
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvNombre, tvCantidad, tvEstado, tvTipo;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvCodigo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvTipo = itemView.findViewById(R.id.tvTipo);
        }
    }

}

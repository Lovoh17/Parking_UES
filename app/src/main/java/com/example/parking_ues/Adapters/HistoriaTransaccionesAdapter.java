package com.example.parking_ues.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking_ues.R;
import com.google.android.material.chip.Chip;

public class HistoriaTransaccionesAdapter extends RecyclerView.Adapter<HistoriaTransaccionesAdapter.ViewHolderHistoriaTransaccionesAdapter> {
    public Context context;
    private FragmentManager fragmentManager;

    public HistoriaTransaccionesAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public HistoriaTransaccionesAdapter.ViewHolderHistoriaTransaccionesAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historial_transacciones, parent, false);
        return new ViewHolderHistoriaTransaccionesAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriaTransaccionesAdapter.ViewHolderHistoriaTransaccionesAdapter holder, int position) {
        // Datos fijos de prueba
        holder.lblDescripcionTransaccion.setText("Ingreso a parqueo COM-1");
        holder.lblMontoTransaccion.setText("$1.50");
        holder.lblNumeroReferencia.setText("Ref #0001");
        holder.lblFechaTransaccion.setText("08/06/2025");
        holder.lblMetodoPago.setText("Efectivo");
        holder.lblTipoTransaccion.setText("Entrada");
        holder.chipEstadoTransaccion.setText("Completado");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolderHistoriaTransaccionesAdapter extends RecyclerView.ViewHolder {
        TextView lblDescripcionTransaccion, lblMontoTransaccion, lblNumeroReferencia, lblFechaTransaccion, lblMetodoPago, lblTipoTransaccion;
        ImageView imgTipoTransaccion;
        Chip chipEstadoTransaccion;
        public ViewHolderHistoriaTransaccionesAdapter(@NonNull View itemView) {
            super(itemView);
            lblDescripcionTransaccion = itemView.findViewById(R.id.lblDescripcionTransaccion);
            lblMontoTransaccion = itemView.findViewById(R.id.lblMontoTransaccion);
            lblNumeroReferencia = itemView.findViewById(R.id.lblNumeroReferencia);
            lblFechaTransaccion = itemView.findViewById(R.id.lblFechaTransaccion);
            lblMetodoPago = itemView.findViewById(R.id.lblMetodoPago);
            lblTipoTransaccion = itemView.findViewById(R.id.lblTipoTransaccion);
            imgTipoTransaccion = itemView.findViewById(R.id.imgTipoTransaccion);
            chipEstadoTransaccion = itemView.findViewById(R.id.chipEstadoTransaccion);

        }

    }
}

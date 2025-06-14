package com.example.parking_ues.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking_ues.R;

public class HistorialPorEstacionamientoAdapter extends RecyclerView.Adapter<HistorialPorEstacionamientoAdapter.ViewHolderHistorialPorEstacionamientoAdapter> {
    public Context context;
    private FragmentManager fragmentManager;

    public HistorialPorEstacionamientoAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public HistorialPorEstacionamientoAdapter.ViewHolderHistorialPorEstacionamientoAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historial_por_estacionamiento, parent, false);
        return new ViewHolderHistorialPorEstacionamientoAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialPorEstacionamientoAdapter.ViewHolderHistorialPorEstacionamientoAdapter holder, int position) {
        // Datos fijos de prueba
        holder.lblNombreCliente.setText("Lino  lovo");
        holder.lblPlaca.setText("Placa: ABC-123");
        holder.lblHoraEntrada.setText("Entrada: 08:30 AM");
        holder.lblHoraSalida.setText("Salida: 12:30 PM");
        holder.lblFecha.setText("Fecha: 08/06/2025");
        holder.lblMonto.setText("$5.00");

        holder.lblNombreCliente.setText("Lino lovo");
        holder.lblPlaca.setText("Placa: ABC-123");
        holder.lblHoraEntrada.setText("Entrada: 08:30 AM");
        holder.lblHoraSalida.setText("Salida: 12:30 PM");
        holder.lblFecha.setText("Fecha: 08/06/2025");
        holder.lblMonto.setText("$5.00");

        holder.lblNombreCliente.setText("Kevin Apellido");
        holder.lblPlaca.setText("Placa: ABC-123");
        holder.lblHoraEntrada.setText("Entrada: 08:30 AM");
        holder.lblHoraSalida.setText("Salida: 12:30 PM");
        holder.lblFecha.setText("Fecha: 08/06/2025");
        holder.lblMonto.setText("$5.00");
    }

    @Override
    public int getItemCount() {
        return 3;

    }

    public class ViewHolderHistorialPorEstacionamientoAdapter extends RecyclerView.ViewHolder {
        TextView lblNombreCliente, lblPlaca, lblHoraEntrada, lblHoraSalida, lblFecha, lblMonto;
        public ViewHolderHistorialPorEstacionamientoAdapter(@NonNull View itemView) {
            super(itemView);
            lblNombreCliente = itemView.findViewById(R.id.lblNombreCliente);
            lblPlaca         = itemView.findViewById(R.id.lblPlaca);
            lblHoraEntrada   = itemView.findViewById(R.id.lblHoraEntrada);
            lblHoraSalida    = itemView.findViewById(R.id.lblHoraSalida);
            lblFecha         = itemView.findViewById(R.id.lblFecha);
            lblMonto         = itemView.findViewById(R.id.lblMonto);
        }
    }
}

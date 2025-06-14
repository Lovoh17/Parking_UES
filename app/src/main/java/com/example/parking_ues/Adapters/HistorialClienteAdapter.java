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

public class HistorialClienteAdapter extends RecyclerView.Adapter<HistorialClienteAdapter.ViewHolderHistorialClienteAdapter> {
    private Context context;
    private FragmentManager fragmentManager;

    public HistorialClienteAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public HistorialClienteAdapter.ViewHolderHistorialClienteAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hostorial_cliente, parent, false);
        return new ViewHolderHistorialClienteAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialClienteAdapter.ViewHolderHistorialClienteAdapter holder, int position) {
        // Datos de prueba
        holder.lblPlacaVehiculo.setText("Placa: ABC-123");
        holder.lblEstacionaminetoHistorial.setText("Estacionamiento: VIP-1");
        holder.lblFechaEntradaHistorial.setText("Fecha Entrada: 08/06/2025");
        holder.lblHoraEntradaHistorial.setText("Hora Entrada: 08:30 AM");
        holder.lblFechaSalidaHistorial.setText("Fecha Salida: 08/06/2025");
        holder.lblHoraSalidaHistorial.setText("Hora Salida: 12:30 PM");
        holder.lblDuracion.setText("Duración: 4h");
        holder.lblMontoPagado.setText("$5.00");
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolderHistorialClienteAdapter extends RecyclerView.ViewHolder {
        private TextView lblPlacaVehiculo, lblEstacionaminetoHistorial, lblFechaEntradaHistorial,
                lblHoraEntradaHistorial, lblFechaSalidaHistorial, lblHoraSalidaHistorial, lblDuracion, lblMontoPagado;
        public ViewHolderHistorialClienteAdapter(@NonNull View itemView) {
            super(itemView);
            AociarElemnetosXML();
        }
        public void AociarElemnetosXML(){
            lblPlacaVehiculo = itemView.findViewById(R.id.lblPlacaVehiculo);
            lblEstacionaminetoHistorial = itemView.findViewById(R.id.lblEstacionaminetoHistorial);
            lblFechaEntradaHistorial = itemView.findViewById(R.id.lblFechaEntradaHistorial);
            lblHoraEntradaHistorial = itemView.findViewById(R.id.lblHoraEntradaHistorial);
            lblFechaSalidaHistorial = itemView.findViewById(R.id.lblFechaSalidaHistorial);
            lblHoraSalidaHistorial = itemView.findViewById(R.id.lblHoraSalidaHistorial);
            lblHoraSalidaHistorial = itemView.findViewById(R.id.lblHoraSalidaHistorial);
            lblDuracion = itemView.findViewById(R.id.lblDuracion);
            lblMontoPagado = itemView.findViewById(R.id.lblMontoPagado);
        }
    }
}

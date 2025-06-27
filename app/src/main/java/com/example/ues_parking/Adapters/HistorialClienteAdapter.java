package com.example.ues_parking.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ues_parking.R;

import java.util.ArrayList;
import java.util.List;

public class HistorialClienteAdapter extends RecyclerView.Adapter<HistorialClienteAdapter.ViewHolder> {

    private Context context;
    private FragmentManager fragmentManager;
    private List<String> codigos = new ArrayList<>();
    private List<String> estados = new ArrayList<>();
    private List<String> fechas = new ArrayList<>();

    public HistorialClienteAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void limpiarDatos() {
        codigos.clear();
        estados.clear();
        fechas.clear();
        notifyDataSetChanged();
    }

    public void agregarCodigoAccess(String codigo, String estado, String fecha) {
        codigos.add(codigo);
        estados.add(estado);
        fechas.add(fecha);
        notifyItemInserted(codigos.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hostorial_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvCodigo.setText("Código: " + codigos.get(position));
        holder.tvEstado.setText("Estado: " + estados.get(position));
        holder.tvFecha.setText("Fecha: " + fechas.get(position));
    }

    @Override
    public int getItemCount() {
        return codigos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvEstado, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvCodigo);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
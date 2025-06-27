package com.example.ues_parking.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ues_parking.Models.Membership;
import com.example.ues_parking.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MembresiaAdapter extends RecyclerView.Adapter<MembresiaAdapter.MembresiaViewHolder> {

    private List<Membership> membresias;
    private Context context;
    private OnMembresiaClickListener listener;

    public interface OnMembresiaClickListener {
        void onMembresiaClick(Membership membresia);
    }

    public MembresiaAdapter(List<Membership> membresias, Context context, OnMembresiaClickListener listener) {
        this.membresias = membresias;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MembresiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_membresia, parent, false);
        return new MembresiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembresiaViewHolder holder, int position) {
        Membership membresia = membresias.get(position);

        // Configurar colores según el tipo
        int colorTinte = membresia.getName().equalsIgnoreCase("gold") ?
                context.getResources().getColor(R.color.dorado) :
                context.getResources().getColor(R.color.plateado);

        holder.imgTipoMembresia.setColorFilter(colorTinte);
        holder.tvPrecioMembresia.setTextColor(colorTinte);

        // Configurar datos
        holder.tvNombreMembresia.setText("Compra de membresía " + membresia.getName());
        holder.tvDescripcionMembresia.setText(membresia.getDescription());
        holder.tvPrecioMembresia.setText(String.format("$%.2f", membresia.getPrice()));
        holder.btnPagarMembresia.setText("PAGAR " + membresia.getName().toUpperCase());

        // Limpiar beneficios anteriores
        holder.llBeneficios.removeAllViews();

        // Agregar beneficios dinámicamente
        for (String beneficio : membresia.getBenefits()) {
            View beneficioView = LayoutInflater.from(context)
                    .inflate(R.layout.item_beneficio, holder.llBeneficios, false);

            ImageView ivCheck = beneficioView.findViewById(R.id.ivCheckBeneficio);
            TextView tvBeneficio = beneficioView.findViewById(R.id.tvBeneficio);

            ivCheck.setColorFilter(colorTinte);
            tvBeneficio.setText(beneficio);

            holder.llBeneficios.addView(beneficioView);
        }

        // Configurar click listener
        holder.btnPagarMembresia.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMembresiaClick(membresia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return membresias.size();
    }

    public static class MembresiaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTipoMembresia;
        TextView tvNombreMembresia, tvDescripcionMembresia, tvPrecioMembresia;
        LinearLayout llBeneficios;
        MaterialButton btnPagarMembresia;

        public MembresiaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTipoMembresia = itemView.findViewById(R.id.imgTipoMembresia);
            tvNombreMembresia = itemView.findViewById(R.id.tvNombreMembresia);
            tvDescripcionMembresia = itemView.findViewById(R.id.tvDescripcionMembresia);
            tvPrecioMembresia = itemView.findViewById(R.id.tvPrecioMembresia);
            llBeneficios = itemView.findViewById(R.id.llBeneficios);
            btnPagarMembresia = itemView.findViewById(R.id.btnPagarMembresia);
        }
    }
}
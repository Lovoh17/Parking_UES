package com.example.ues_parking.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ues_parking.Models.ParkingSpace;
import com.example.ues_parking.R;

import java.util.ArrayList;
import java.util.List;

public class ParkingSpaceAdapter extends RecyclerView.Adapter<ParkingSpaceAdapter.ParkingSpaceViewHolder> {

    private List<ParkingSpace> parkingSpaces;
    private OnParkingSpaceClickListener listener;
    private Context context;

    public interface OnParkingSpaceClickListener {
        void onParkingSpaceClick(ParkingSpace parkingSpace);
    }

    public ParkingSpaceAdapter(Context context, List<ParkingSpace> parkingSpaces,
                               OnParkingSpaceClickListener listener) {
        this.context = context;
        // Crear una copia defensiva para evitar modificaciones externas
        this.parkingSpaces = new ArrayList<>(parkingSpaces);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParkingSpaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estacionamiento, parent, false);
        return new ParkingSpaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingSpaceViewHolder holder, int position) {
        ParkingSpace space = parkingSpaces.get(position);
        holder.bind(space, listener);
    }

    @Override
    public int getItemCount() {
        return parkingSpaces != null ? parkingSpaces.size() : 0;
    }

    public void updateData(List<ParkingSpace> newSpaces) {
        if (newSpaces != null) {
            parkingSpaces.clear();
            parkingSpaces.addAll(newSpaces);
            notifyDataSetChanged();
        }
    }

    // Método adicional para actualizar un elemento específico
    public void updateSingleSpace(ParkingSpace updatedSpace) {
        for (int i = 0; i < parkingSpaces.size(); i++) {
            if (parkingSpaces.get(i).getSpaceId().equals(updatedSpace.getSpaceId())) {
                parkingSpaces.set(i, updatedSpace);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class ParkingSpaceViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvSpaceNumber;
        private final TextView tvStatus;
        private final TextView tvSection;

        public ParkingSpaceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardParkingSpace);
            tvSpaceNumber = itemView.findViewById(R.id.tvSpaceNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSection = itemView.findViewById(R.id.tvSection);
        }

        public void bind(ParkingSpace space, OnParkingSpaceClickListener listener) {
            if (space == null) return;

            // Mostrar número de espacio
            tvSpaceNumber.setText(String.valueOf(space.getSpaceNumber()));

            // Mostrar sección (normal/vip)
            boolean isVip = "vip".equalsIgnoreCase(space.getSection());
            tvSection.setText(isVip ? "VIP" : "Normal");
            tvSection.setTextColor(isVip ?
                    ContextCompat.getColor(itemView.getContext(), R.color.azul_noche) :
                    ContextCompat.getColor(itemView.getContext(), R.color.rojo_terroso));

            // Configurar estado con mejor legibilidad
            configureSpaceStatus(space);

            // Configurar click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onParkingSpaceClick(space);
                }
            });
        }

        private void configureSpaceStatus(ParkingSpace space) {
            int backgroundColor;
            String statusText;

            if (space.isOccupied()) {
                backgroundColor = R.color.rojo;
                statusText = "OCUPADO";
            } else if (space.isReserved()) {
                backgroundColor = R.color.amarillo;
                statusText = "RESERVADO";
            } else {
                backgroundColor = R.color.verde;
                statusText = "DISPONIBLE";
            }

            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), backgroundColor));
            tvStatus.setText(statusText);
        }
    }
}
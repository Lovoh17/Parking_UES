package com.example.ues_parking.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ues_parking.Adapters.ParkingSpaceAdapter;
import com.example.ues_parking.Models.ParkingSpace;
import com.example.ues_parking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerEstacionamientosFragment extends Fragment implements ParkingSpaceAdapter.OnParkingSpaceClickListener {

    private RecyclerView rvComercial, rvVIP;
    private ParkingSpaceAdapter comercialAdapter, vipAdapter;
    private List<ParkingSpace> espaciosNormales = new ArrayList<>();
    private List<ParkingSpace> espaciosVIP = new ArrayList<>();
    private DatabaseReference parkingRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_estacionamientos, container, false);

        // Inicializar Firebase
        parkingRef = FirebaseDatabase.getInstance().getReference("parking_spaces");

        // Configurar vistas
        rvComercial = view.findViewById(R.id.rvComercial);
        rvVIP = view.findViewById(R.id.rvVIP);

        // Configurar adaptadores
        comercialAdapter = new ParkingSpaceAdapter(requireContext(), espaciosNormales, this);
        vipAdapter = new ParkingSpaceAdapter(requireContext(), espaciosVIP, this);

        rvComercial.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVIP.setLayoutManager(new LinearLayoutManager(getContext()));

        rvComercial.setAdapter(comercialAdapter);
        rvVIP.setAdapter(vipAdapter);

        // Cargar datos
        loadParkingSpaces();

        return view;
    }

    private void loadParkingSpaces() {
        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                espaciosNormales.clear();
                espaciosVIP.clear();

                for (DataSnapshot spaceSnapshot : snapshot.getChildren()) {
                    ParkingSpace space = spaceSnapshot.getValue(ParkingSpace.class);
                    if (space != null) {
                        if ("vip".equalsIgnoreCase(space.getSection())) {
                            espaciosVIP.add(space);
                        } else {
                            espaciosNormales.add(space);
                        }
                    }
                }

                comercialAdapter.updateData(espaciosNormales);
                vipAdapter.updateData(espaciosVIP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar espacios: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onParkingSpaceClick(ParkingSpace parkingSpace) {
        if (parkingSpace.isOccupied()) {
            showSpaceDialog(parkingSpace, "OCUPADO", "Este espacio está actualmente en uso.");
        } else if (parkingSpace.isReserved()) {
            showSpaceDialog(parkingSpace, "RESERVADO", "Este espacio VIP ha sido reservado.");
        } else {
            showReservationOptions(parkingSpace);
        }
    }

    private void showSpaceDialog(ParkingSpace space, String status, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Espacio " + space.getSpaceNumber() + " - " + status)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void showReservationOptions(ParkingSpace parkingSpace) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Espacio " + parkingSpace.getSpaceNumber())
                .setMessage("¿Desea reservar este espacio?")
                .setPositiveButton("Reservar", (dialog, which) -> updateSpaceStatus(parkingSpace, true))
                .setNegativeButton("Ocupar", (dialog, which) -> updateSpaceStatus(parkingSpace, false))
                .setNeutralButton("Cancelar", null)
                .show();
    }

    private void updateSpaceStatus(ParkingSpace parkingSpace, boolean isReservation) {
        DatabaseReference spaceRef = parkingRef.child(parkingSpace.getSpaceId());

        if (isReservation && "vip".equals(parkingSpace.getSection())) {
            spaceRef.child("reserved").setValue(true);
        } else {
            spaceRef.child("occupied").setValue(true);
        }

        spaceRef.child("lastUpdated").setValue(System.currentTimeMillis());
    }
}
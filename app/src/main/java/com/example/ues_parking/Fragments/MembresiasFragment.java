package com.example.ues_parking.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ues_parking.Adapters.MembresiaAdapter;
import com.example.ues_parking.Dialogs.ConfirmarPagoMenbresiaDialog;
import com.example.ues_parking.Models.Membership;
import com.example.ues_parking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MembresiasFragment extends Fragment implements
        MembresiaAdapter.OnMembresiaClickListener,
        ConfirmarPagoMenbresiaDialog.OnPagoConfirmadoListener {

    private RecyclerView rvMembresias;
    private MembresiaAdapter adapter;
    private List<Membership> listaMembresias = new ArrayList<>();

    public MembresiasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menbresias, container, false);


        // Configurar RecyclerView
        rvMembresias = view.findViewById(R.id.rvMembresias);
        rvMembresias.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MembresiaAdapter(listaMembresias, getContext(), this);
        rvMembresias.setAdapter(adapter);

        // Cargar datos de Firebase
        cargarMembresias();

        return view;
    }

    private void cargarMembresias() {
        DatabaseReference membresiasRef = FirebaseDatabase.getInstance()
                .getReference("membership_plans");

        membresiasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMembresias.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Membership membresia = dataSnapshot.getValue(Membership.class);
                    if (membresia != null && membresia.isActive()) {
                        listaMembresias.add(membresia);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar membresías: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMembresiaClick(Membership membresia) {
        mostrarDialogoConfirmacion(membresia);
    }

    private void mostrarDialogoConfirmacion(Membership membresia) {
        ConfirmarPagoMenbresiaDialog dialogo = ConfirmarPagoMenbresiaDialog.newInstance(membresia);
        dialogo.setOnPagoConfirmadoListener(this);
        dialogo.show(getParentFragmentManager(), "confirmarPagoMembresia");
    }

    @Override
    public void onPagoConfirmado(Membership membresia) {
        procesarPagoMembresia(membresia);
    }

    private void procesarPagoMembresia(Membership membresia) {
        Toast.makeText(getContext(), "Pago confirmado para: " + membresia.getName(),Toast.LENGTH_SHORT).show();
    }
}
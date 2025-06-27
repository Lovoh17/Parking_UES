package com.example.ues_parking.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ues_parking.Adapters.HistorialClienteAdapter;
import com.example.ues_parking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistorialClienteFragment extends Fragment {

    private RecyclerView rvcHistorialClientes;
    private ImageButton lblVolver;
    private HistorialClienteAdapter adapter;
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    public HistorialClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_cliente, container, false);
        asociarElementosXML(view);
        configurarFirebase();
        configurarRecyclerView();
        configurarBotonVolver();
        return view;
    }

    private void asociarElementosXML(View view) {
        rvcHistorialClientes = view.findViewById(R.id.rvcHistorialClientes);
        lblVolver = view.findViewById(R.id.lblVolver);
    }

    private void configurarFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void configurarRecyclerView() {
        rvcHistorialClientes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistorialClienteAdapter(requireContext(), getParentFragmentManager());
        rvcHistorialClientes.setAdapter(adapter);

        if (currentUser != null) {
            cargarHistorialUsuario(currentUser.getUid());
        } else {
            Toast.makeText(getContext(), "No se pudo identificar al usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarHistorialUsuario(String userId) {
        databaseRef.child("codigo_acceso")
                .orderByChild("user_id")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adapter.limpiarDatos();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String codigo = dataSnapshot.child("codigo").getValue(String.class);
                            String status = dataSnapshot.child("status").getValue(String.class);
                            String timestamp = dataSnapshot.child("timestamp").getValue(String.class);

                            // Agregar solo códigos permitidos
                            if ("Permitido".equals(status)) {
                                adapter.agregarCodigoAccess(codigo, status, timestamp);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error al cargar historial: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void configurarBotonVolver() {
        lblVolver.setOnClickListener(v -> {
            Fragment fragment = new PerfilClienteFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
package com.example.parking_ues.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.parking_ues.Adapters.HistorialPorEstacionamientoAdapter;
import com.example.parking_ues.Dialogs.NombreDialogo;
import com.example.parking_ues.R;


public class GestionarPorEstacionamientoFragment extends Fragment {
    public RecyclerView rvcHistorialPorEstacionamiento;
    public TextView lblTituloEstacionamiento;
    private ImageButton lblVolver, btnEditarEstacionamiento;

    public GestionarPorEstacionamientoFragment() {
        // Required empty public constructor
    }


    public static GestionarPorEstacionamientoFragment newInstance(String param1, String param2) {
        GestionarPorEstacionamientoFragment fragment = new GestionarPorEstacionamientoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gestionar_por_estacionamiento, container, false);
        AsociarElemnetosXML(view);

        rvcHistorialPorEstacionamiento.setLayoutManager(new LinearLayoutManager(getContext()));
        HistorialPorEstacionamientoAdapter adapter = new HistorialPorEstacionamientoAdapter(requireContext(), getParentFragmentManager());
        rvcHistorialPorEstacionamiento.setAdapter(adapter);

        if (getArguments() != null) {
            String nombreEspacio = getArguments().getString("nombreEspacio", "Sin nombre");
            lblTituloEstacionamiento.setText(nombreEspacio);
        }

        //EVENETO PARA VOLVER LA FRAMNETO GESTION DE ESTACIOMANIENTO
        lblVolver.setOnClickListener(v -> {
            Fragment fragment = new GestionarEstacionamientoFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        //EVENETO PARA ABARIDIALOGO DE EDITAR LA FRAMNETO GESTION DE ESTACIOMANIENTO
        btnEditarEstacionamiento.setOnClickListener(v -> {
            NombreDialogo dialogo = new NombreDialogo();
            dialogo.show(getParentFragmentManager(), "NombreDialogo");
        });

        return view;
    }
    public void AsociarElemnetosXML(View view){
        rvcHistorialPorEstacionamiento = view.findViewById(R.id.rvcHistorialPorEstacionamiento);
        lblTituloEstacionamiento = view.findViewById(R.id.lblTituloEstacionamiento);
        lblVolver = view.findViewById(R.id.lblVolver);
        btnEditarEstacionamiento = view.findViewById(R.id.btnEditarEstacionamiento);
    }
}
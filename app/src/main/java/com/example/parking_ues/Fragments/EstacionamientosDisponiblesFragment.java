package com.example.parking_ues.Fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.parking_ues.Dialogs.ReservarAhoraDialogo;
import com.example.parking_ues.Dialogs.UsuariosDialog;
import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;


public class EstacionamientosDisponiblesFragment extends Fragment  {

    private MaterialButton btnReservarEstacionamineto, btnParquearme;
    private ImageButton lblVolver;
    public EstacionamientosDisponiblesFragment() {
        // Required empty public constructor
    }
    public static EstacionamientosDisponiblesFragment newInstance(String param1, String param2) {
        EstacionamientosDisponiblesFragment fragment = new EstacionamientosDisponiblesFragment();
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
        View view = inflater.inflate(R.layout.fragment_estacionamientos_disponibles, container, false);
        AsociarElememtosXML(view);

        //ABRIR EL DIALOGO PARA RESERVAR ESTACINAMIENTO
        btnReservarEstacionamineto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReservarAhoraDialogo dialog = new ReservarAhoraDialogo();
                dialog.show(getParentFragmentManager(), "reservarAhoraDialogo");
            }
        });
        //EVENETO PARA VOLVER LA FRAMNETO GESTION DE ESTACIOMANIENTO
        lblVolver.setOnClickListener(v -> {
            Fragment fragment = new VerEstacionamientosFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
    public void AsociarElememtosXML(View view){
        btnParquearme = view.findViewById(R.id.btnParquearme);
        btnReservarEstacionamineto = view.findViewById(R.id.btnReservarEstacionamineto);
        lblVolver = view.findViewById(R.id.lblVolver);
    }
}
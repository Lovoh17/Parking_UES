package com.example.parking_ues.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parking_ues.Dialogs.PagoConSaldoDialogo;
import com.example.parking_ues.Dialogs.PagoSinSaldoDialog;
import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;


public class TiempoEstacionadoFragment extends Fragment {

    private MaterialButton btnPagarEstacionamiento;
    public TiempoEstacionadoFragment() {
        // Required empty public constructor
    }

    public static TiempoEstacionadoFragment newInstance(String param1, String param2) {
        TiempoEstacionadoFragment fragment = new TiempoEstacionadoFragment();
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
        View view = inflater.inflate(R.layout.fragment_tiempo_estacionado, container, false);
        AsociarElementosXML(view);
        btnPagarEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagoConSaldoDialogo dialogo = new PagoConSaldoDialogo();
                dialogo.show(getParentFragmentManager(), "pagoConSaldo");
            }
        });
        return  view;
    }
    private void AsociarElementosXML(View view){
        btnPagarEstacionamiento = view.findViewById(R.id.btnPagarEstacionamiento);
    }
}
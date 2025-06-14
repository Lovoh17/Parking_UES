package com.example.parking_ues.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parking_ues.Dialogs.ConfirmarPagoMenbresiaDialog;
import com.example.parking_ues.Dialogs.PagoConSaldoDialogo;
import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;


public class MenbresiasFragment extends Fragment {
    private TextView lblPrecioMembresiaGold, lblPresioMenbresiaSilver;
    private MaterialButton btnPagarMembresiaSilver, btnPagarMembresiaGold;
    public MenbresiasFragment() {
        // Required empty public constructor
    }

    public static MenbresiasFragment newInstance(String param1, String param2) {
        MenbresiasFragment fragment = new MenbresiasFragment();
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
        View view = inflater.inflate(R.layout.fragment_menbresias, container, false);
        AsociarElementosXML(view);

        //EVENTO DEL BOTON PAGAR MENBRESIA SILVER
        btnPagarMembresiaSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmarPagoMenbresiaDialog dialogo = new ConfirmarPagoMenbresiaDialog();
                dialogo.show(getParentFragmentManager(), "confirmarPagoMnebresia");
            }
        });

        //EVENTO DEL BOTON PAGAR MENBRESIA GOLD
        btnPagarMembresiaGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmarPagoMenbresiaDialog dialogo = new ConfirmarPagoMenbresiaDialog();
                dialogo.show(getParentFragmentManager(), "confirmarPagoMnebresia");
            }
        });
        return view;
    }
    public void   AsociarElementosXML(View view){
        lblPresioMenbresiaSilver = view.findViewById(R.id.lblPresioMenbresiaSilver);
        lblPrecioMembresiaGold = view.findViewById(R.id.lblPrecioMembresiaGold);

        btnPagarMembresiaSilver = view.findViewById(R.id.btnPagarMembresiaSilver);
        btnPagarMembresiaGold = view.findViewById(R.id.btnPagarMembresiaGold);


    }
}
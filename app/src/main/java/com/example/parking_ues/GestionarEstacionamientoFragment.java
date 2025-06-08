package com.example.parking_ues;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;


public class GestionarEstacionamientoFragment extends Fragment implements View.OnClickListener {

    // Elementos de la zona comercial
    private CardView cardComercial1, cardComercial2, cardComercial3, cardComercial4, cardComercial5, cardComercial6;

    // Elementos de la zona VIP
    private CardView cardVIP1, cardVIP2, cardVIP3, cardVIP4, cardVIP5, cardVIP6;

    // Botones
    private MaterialButton btnTarifas, btnTarifasVIP;

    public GestionarEstacionamientoFragment() {
        // Required empty public constructor
    }


    public static GestionarEstacionamientoFragment newInstance(String param1, String param2) {
        GestionarEstacionamientoFragment fragment = new GestionarEstacionamientoFragment();
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
        View view = inflater.inflate(R.layout.fragment_gestionar_estacionamineto, container, false);
        AsociarELementosXML(view);
        return view;
    }
    public void AsociarELementosXML(View view){
        // Asociar estacionamientos comerciales
        cardComercial1 = view.findViewById(R.id.cardComercial1);
        cardComercial1.setOnClickListener(this);

        cardComercial2 = view.findViewById(R.id.cardComercial2);
        cardComercial2.setOnClickListener(this);

        cardComercial3 = view.findViewById(R.id.cardComercial3);
        cardComercial3.setOnClickListener(this);

        cardComercial4 = view.findViewById(R.id.cardComercial4);
        cardComercial4.setOnClickListener(this);

        cardComercial5 = view.findViewById(R.id.cardComercial5);
        cardComercial5.setOnClickListener(this);

        cardComercial6 = view.findViewById(R.id.cardComercial6);
        cardComercial6.setOnClickListener(this);

        // Asociar estacionamientos VIP
        cardVIP1 = view.findViewById(R.id.cardVIP1);
        cardVIP1.setOnClickListener(this);

        cardVIP2 = view.findViewById(R.id.cardVIP2);
        cardVIP2.setOnClickListener(this);

        cardVIP3 = view.findViewById(R.id.cardVIP3);
        cardVIP3.setOnClickListener(this);

        cardVIP4 = view.findViewById(R.id.cardVIP4);
        cardVIP4.setOnClickListener(this);

        cardVIP5 = view.findViewById(R.id.cardVIP5);
        cardVIP5.setOnClickListener(this);

        cardVIP6 = view.findViewById(R.id.cardVIP6);
        cardVIP6.setOnClickListener(this);

        // Asociar btns (botones)
        btnTarifas = view.findViewById(R.id.btnTarifas);
        btnTarifasVIP = view.findViewById(R.id.btnTarifasVIP);
    }


    //EVENTOS DE LAS CARD DE LOS ESTACIOMANIETOS
    @Override
    public void onClick(View view) {
        String nombreTitulo = "";

        switch (view.getId()) {
            case R.id.cardComercial1:
                nombreTitulo = "Estacionamineto COM-1";
                break;
            case R.id.cardComercial2:
                nombreTitulo = "Estacionamineto COM-2";
                break;
            case R.id.cardComercial3:
                nombreTitulo = "Estacionamineto COM-3";
                break;
            case R.id.cardComercial4:
                nombreTitulo = "Estacionamineto COM-4";
                break;
            case R.id.cardComercial5:
                nombreTitulo = "Estacionamineto COM-5";
                break;
            case R.id.cardComercial6:
                nombreTitulo = "Estacionamineto Com-6";
                break;
            case R.id.cardVIP1:
                nombreTitulo = "Estacionamineto VIP-1";
                break;
            case R.id.cardVIP2:
                nombreTitulo = "Estacionamineto VIP-2";
                break;
            case R.id.cardVIP3:
                nombreTitulo = "Estacionamineto  VIP-3";
                break;
            case R.id.cardVIP4:
                nombreTitulo = "Estacionamineto  VIP-4";
                break;
            case R.id.cardVIP5:
                nombreTitulo = "Estacionamineto VIP-5";
                break;
            case R.id.cardVIP6:
                nombreTitulo = "Estacionamineto  VIP-6";

                break;
        }
        abrirFragmentoDestino(nombreTitulo);
    }

    private void abrirFragmentoDestino(String nombreEspacio) {
        Fragment fragment = new GestionarPorEstacionamientoFragment();

        Bundle bundle = new Bundle();
        bundle.putString("nombreEspacio", nombreEspacio);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment) // tu contenedor de fragmentos
                .addToBackStack(null)
                .commit();
    }


}
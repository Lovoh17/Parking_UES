package com.example.parking_ues.Fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parking_ues.R;


public class DashboardFragment extends Fragment {
    private CardView cardTitulo, cardLeyendaDeEstados;
    private TextView lblTitle, lblContadorUsuarios, lblContadorComerciales, lblContadorVIP;
    private TextView lblUltimoParqueo, lblUltimaCompra;
    private TextView lblContadorLibres, lblContadorOcupados, lblContadorReservados;
    private CardView btnGestionUsuarios, btnConfiguracion;
    public DashboardFragment() {
        // Required empty public constructor
    }


    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        AsocialrElmentoXML(view);

        // ACCION PARA EL BTN GETIONAR USUARIOS
        btnGestionUsuarios.setOnClickListener(v -> {
            Fragment fragment = new GestionUsuariosFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
    public void AsocialrElmentoXML(View view){

        // Elementos de estadist
        lblTitle = view.findViewById(R.id.lblTitle);
        lblContadorUsuarios = view.findViewById(R.id.lblContadorUsuarios);
        lblContadorComerciales = view.findViewById(R.id.lblContadorComerciales);
        lblContadorVIP = view.findViewById(R.id.lblContadorVIP);

        //Botones de acciones
        btnGestionUsuarios = view.findViewById(R.id.btnGestionUsuarios);
        btnConfiguracion = view.findViewById(R.id.btnConfiguracion);

        // Actividades recientes
        lblUltimoParqueo = view.findViewById(R.id.lblUltimoParqueo);
        lblUltimaCompra = view.findViewById(R.id.lblUltimaCompra);

        //  Leyenda de estados
        lblContadorLibres = view.findViewById(R.id.lblContadorLibres);
        lblContadorOcupados = view.findViewById(R.id.lblContadorOcupados);
        lblContadorReservados = view.findViewById(R.id.lblContadorReservados);


    }
}
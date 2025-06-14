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

import com.example.parking_ues.Adapters.GestionarUsuariosAdapter;
import com.example.parking_ues.Adapters.HistorialClienteAdapter;
import com.example.parking_ues.Adapters.HistorialPorEstacionamientoAdapter;
import com.example.parking_ues.R;


public class HistorialClienteFragment extends Fragment {

    private RecyclerView rvcHistorialClientes;
    private ImageButton lblVolver ;


    public HistorialClienteFragment() {
        // Required empty public constructor
    }


    public static HistorialClienteFragment newInstance(String param1, String param2) {
        HistorialClienteFragment fragment = new HistorialClienteFragment();
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
        View view = inflater.inflate(R.layout.fragment_historial_cliente, container, false);
        AsociarElementosXML(view);

        rvcHistorialClientes.setLayoutManager(new LinearLayoutManager(getContext()));
        HistorialClienteAdapter adapter = new HistorialClienteAdapter(requireContext(), getParentFragmentManager());
        rvcHistorialClientes.setAdapter(adapter);

        lblVolver.setOnClickListener(v ->{
            Fragment fragment = new PerfilClienteFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
    public void AsociarElementosXML(View view){
        rvcHistorialClientes = view.findViewById(R.id.rvcHistorialClientes);
        lblVolver = view.findViewById(R.id.lblVolver);

    }

}
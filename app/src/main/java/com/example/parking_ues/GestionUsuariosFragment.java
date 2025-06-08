package com.example.parking_ues;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parking_ues.Adapters.GestionarUsuariosAdapter;
import com.example.parking_ues.Dialogs.UsuariosDialog;
import com.google.android.material.button.MaterialButton;


public class GestionUsuariosFragment extends Fragment {
    public TextView lblContadorUsuarios;
    public RecyclerView rvcUsuarios;
    public MaterialButton btnAbrirDialogoUsuarios;
    public GestionUsuariosFragment() {
        // Required empty public constructor
    }

    public static GestionUsuariosFragment newInstance(String param1, String param2) {
        GestionUsuariosFragment fragment = new GestionUsuariosFragment();
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
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);

        AsociarElemnetosXML(view);

        rvcUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        GestionarUsuariosAdapter adapter = new GestionarUsuariosAdapter(getContext(), getParentFragmentManager());
        rvcUsuarios.setAdapter(adapter);
        lblContadorUsuarios.setText("Total de usuarios: " + adapter.getItemCount());

        //ABRIR EL DIALOGO PARA INSERTAR USUARIOS
        btnAbrirDialogoUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsuariosDialog dialog = new UsuariosDialog();
                dialog.show(getParentFragmentManager(), "usuarioDialogo");
            }
        });

        return view;
    }

    public void AsociarElemnetosXML(View view){
        lblContadorUsuarios = view.findViewById(R.id.lblContadorUsuarios);
        rvcUsuarios = view.findViewById(R.id.rvcUsuarios);
        btnAbrirDialogoUsuarios = view.findViewById(R.id.btnAbrirDialogoUsuarios);
    }
}
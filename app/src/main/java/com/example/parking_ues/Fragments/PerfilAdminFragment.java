package com.example.parking_ues.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parking_ues.EditarContraseniaActivity;
import com.example.parking_ues.EditarPerfilActivity;
import com.example.parking_ues.R;
import com.example.parking_ues.RegisterActivity;


public class PerfilAdminFragment extends Fragment {
    private TextView lblNombreAdminPefil, lblCorreoAdminPerfil, lblRol;
    private TextView lblEditarPerfilAdmin, lblCambiarContraseniaAdmin, lblCerrarSecionAdmin;

    public PerfilAdminFragment() {
        // Required empty public constructor
    }

    public static PerfilAdminFragment newInstance(String param1, String param2) {
        PerfilAdminFragment fragment = new PerfilAdminFragment();
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
        View view =  inflater.inflate(R.layout.fragment_perfil_admin, container, false);
        AsociarElementosXML(view);

        //EVENTO DEL BOTON PARA IR A EDITAR PERFIL
        lblEditarPerfilAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });

        //EVENTO DEL BOTON CAMBIO DE CONTRSASENIA
        lblCambiarContraseniaAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarContraseniaActivity.class);
            startActivity(intent);
        });

        //EVENTO DEL BOTON CERRA SECION
        lblCerrarSecionAdmin.setOnClickListener(v ->{
            mostrarDialogoCerrarSesion();
        });
        return view;
    }
    private void AsociarElementosXML(View view){
        lblNombreAdminPefil = view.findViewById(R.id.lblNombreAdminPefil);
        lblCorreoAdminPerfil = view.findViewById(R.id.lblCorreoAdminPerfil);
        lblRol = view.findViewById(R.id.lblRol);

        lblEditarPerfilAdmin = view.findViewById(R.id.lblEditarPerfilAdmin);
        lblCambiarContraseniaAdmin = view.findViewById(R.id.lblCambiarContraseniaAdmin);
        lblCerrarSecionAdmin = view.findViewById(R.id.lblCerrarSecionAdmin);
    }
    //METODO MOSTAR DIALOGO PARA SERRAR SECION
    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(requireContext()) // requireContext() da el Contexto desde el Fragment
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesion(); //METODO CERRAR SECION
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    //LOGICA PARA SERRar AECCION
    private void cerrarSesion() {
        try {
            Log.d("TAG", "Iniciando proceso de cierre de sesión");
            Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show(); // También aquí

        } catch (Exception e) {
            Log.e("TAG", "Error cerrando sesión", e);
            Toast.makeText(requireContext(), "Error cerrando sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
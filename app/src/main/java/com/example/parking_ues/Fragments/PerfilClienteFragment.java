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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parking_ues.EditarContraseniaActivity;
import com.example.parking_ues.EditarPerfilActivity;
import com.example.parking_ues.R;


public class PerfilClienteFragment extends Fragment {
    private ImageView imgCliente;
    private TextView lblNombreClientePeril, lblCorreoClientePerfil, lblSaldoClientePerfil;
    private TextView lblMenbresias, lblHistorialCliente;
    private TextView lblEditarPerfil, lblCambiarContrasenia, lblReportarPerfil, lblCerrarSecion;

    public PerfilClienteFragment() {
        // Required empty public constructor
    }


    public static PerfilClienteFragment newInstance(String param1, String param2) {
        PerfilClienteFragment fragment = new PerfilClienteFragment();
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

        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);
        AsociarElementosXML(view);

        //EVENTO DEL BOTON PARA IR A MEBRESIAS
        lblMenbresias.setOnClickListener(v -> {
            Fragment fragment = new MenbresiasFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        //EVENTO DEL BOTON PARA IR A HOTORIAL
        lblHistorialCliente.setOnClickListener(v -> {
            Fragment fragment = new HistorialClienteFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        //EVENTO DEL BOTON PARA IR A EDITAR PERFIL
        lblEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });

        //EVENTO DEL BOTON CAMBIO DE CONTRSASENIA
        lblCambiarContrasenia.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarContraseniaActivity.class);
            startActivity(intent);
        });

        //EVENTO DEL BOTON CERRA SECION
        lblCerrarSecion.setOnClickListener(v ->{
            mostrarDialogoCerrarSesion();
        });


        return view;
    }
    public void AsociarElementosXML(View view){
        lblMenbresias = view.findViewById(R.id.lblMenbresias);
        lblHistorialCliente = view.findViewById(R.id.lblHistorialCliente);

        lblNombreClientePeril = view.findViewById(R.id.lblNombreAdminPefil);
        lblCorreoClientePerfil = view.findViewById(R.id.lblCorreoAdminPerfil);
        lblSaldoClientePerfil = view.findViewById(R.id.lblRol);

        lblEditarPerfil = view.findViewById(R.id.lblEditarPerfilAdmin);
        lblCambiarContrasenia = view.findViewById(R.id.lblCambiarContraseniaAdmin);
        lblReportarPerfil = view.findViewById(R.id.lblReportarPerfil);
        lblCerrarSecion = view.findViewById(R.id.lblCerrarSecionAdmin);
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
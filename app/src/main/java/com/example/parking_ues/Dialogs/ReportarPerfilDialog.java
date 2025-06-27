package com.example.parking_ues.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;

public class ReportarPerfilDialog extends DialogFragment {
    private MaterialButton btnConfirmarBorrarCuenta, btnCancelarBorrarCuenta;

    //METODO ONSTAR (se ejecuta cunado eldialogo se va mostrar en pantalla)
    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() != null){
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    //METODO ONCREATEVIEW (CREAA LA VISTA DEL DIALOGO APARTIR DEL XML)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_reportar_perfil, container, false);
        AsociarElementosXML(view);

        //ACCION DE CALCELAR CIAERRAR EL DIALOGO
        btnCancelarBorrarCuenta.setOnClickListener(v -> dismiss()); //SALIR

        //ACCION DE ELIMINAR CUENTA


        return view;
    }

    //METOD PARA ASOCIAR ELEMENTOS
    private void AsociarElementosXML(View view){
        btnCancelarBorrarCuenta = view.findViewById(R.id.btnCancelarBorrarCuenta);
        btnConfirmarBorrarCuenta = view.findViewById(R.id.btnConfirmarBorrarCuenta);
    }

}

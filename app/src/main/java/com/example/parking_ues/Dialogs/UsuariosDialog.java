package com.example.parking_ues.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UsuariosDialog extends DialogFragment {
    // Elementos del layout
    private TextInputEditText txtNombreUsuario, txtEmailUsuario, txtTelefonoUsuario, txtPasswordUsuario, txtConfirmarPasswordUsuario;
    private MaterialButton btnInsertarUsuario;
    private TextView btnSalir;


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_usuario, container, false);

        AsociarElementosXML(view);
        btnSalir.setOnClickListener(v -> dismiss()); //SALIR
        return view;
    }

    public void AsociarElementosXML(View view){
        txtNombreUsuario = view.findViewById(R.id.txtNombreUsuario);
        txtEmailUsuario = view.findViewById(R.id.txtEmailUsuario);
        txtTelefonoUsuario = view.findViewById(R.id.txtTelefonoUsuario);
        txtPasswordUsuario = view.findViewById(R.id.txtPasswordUsuario);
        txtConfirmarPasswordUsuario = view.findViewById(R.id.txtConfirmarPasswordUsuario);

        btnInsertarUsuario = view.findViewById(R.id.btnEditarEstacionamientoNombre);
        btnSalir = view.findViewById(R.id.btnSalir);
    }
}

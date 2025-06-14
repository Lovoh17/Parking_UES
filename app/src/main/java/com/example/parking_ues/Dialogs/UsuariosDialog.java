package com.example.parking_ues.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class UsuariosDialog extends DialogFragment {
    // Elementos del layout
    private TextInputEditText txtNombreUsuario, txtEmailUsuario, txtTelefonoUsuario, txtPasswordUsuario, txtConfirmarPasswordUsuario;
    private MaterialButton btnInsertarUsuario;
    private TextView btnSalir;
    private Spinner spRol;


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
        configurarSpinners();
        btnSalir.setOnClickListener(v -> dismiss()); //SALIR
        return view;
    }

    public void AsociarElementosXML(View view){
        txtNombreUsuario = view.findViewById(R.id.lblNombreAdminPefil);
        txtEmailUsuario = view.findViewById(R.id.txtEmailUsuario);
        txtTelefonoUsuario = view.findViewById(R.id.txtTelefonoUsuario);
        txtTelefonoUsuario = view.findViewById(R.id.txtTelefonoUsuario);
        spRol = view.findViewById(R.id.spRol);
        txtConfirmarPasswordUsuario = view.findViewById(R.id.txtConfirmarPasswordUsuario);

        btnInsertarUsuario = view.findViewById(R.id.btnEditarEstacionamientoNombre);
        btnSalir = view.findViewById(R.id.btnSalir);
    }
    private void configurarSpinners() {
        List<String> rol = Arrays.asList("Admin", "Cliente");
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_personalizado, rol);
        rolAdapter.setDropDownViewResource(R.layout.spinner_personalizado);
        spRol.setAdapter(rolAdapter);
    }
}

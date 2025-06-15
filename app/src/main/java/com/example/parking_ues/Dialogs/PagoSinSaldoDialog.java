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
import com.google.android.material.textfield.TextInputEditText;

public class PagoSinSaldoDialog extends DialogFragment {
    private MaterialButton btnCancelar, btnConfirmarPago;
    private TextInputEditText txtFechaVencimiento, txtMonto, txtCVV, txtNumeroTarjeta, txtNombreCliente;
    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() != null){
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_pago_sin_saldo, container, false);
        AsociarElementosXML(view);
        btnCancelar.setOnClickListener(v -> dismiss()); //SALIR
        return view;
    }
    private void AsociarElementosXML(View view){
        txtNombreCliente = view.findViewById(R.id.txtNombreCliente);
        txtFechaVencimiento = view.findViewById(R.id.txtFechaVencimiento);
        txtNumeroTarjeta = view.findViewById(R.id.txtNumeroTarjeta);
        txtCVV = view.findViewById(R.id.txtCVV);
        txtMonto = view.findViewById(R.id.txtMonto);
        btnConfirmarPago = view.findViewById(R.id.btnConfirmarPagoMenbresia);
        btnCancelar = view.findViewById(R.id.btnCancelar);
    }
}

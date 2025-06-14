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

public class PagoConSaldoDialogo extends DialogFragment {

    private MaterialButton btnPagarAhora, btnCancelar;
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
        //ACA SE INFLA LA VISTA DEL DOAOGO
        View view = inflater.inflate(R.layout.dialogo_pago_con_saldo, container, false);
        AsociarlElementosXML(view);
        btnCancelar.setOnClickListener(v ->{dismiss();});
        return view;
    }
    private void AsociarlElementosXML(View view){
        btnPagarAhora = view.findViewById(R.id.btnPagarAhora);
        btnCancelar = view.findViewById(R.id.btnCancelar);
    }
}

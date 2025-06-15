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

public class ConfirmarPagoMenbresiaDialog extends DialogFragment {
    private MaterialButton btnConfirmarPagoMenbresia, btnCancelarPagoMenbresia;
    private TextView lblPrecioMnebresia, lblDuracionMnebresia;

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
        View view = inflater.inflate(R.layout.dialogo_pagar_menbresia, container, false);
        AsociarlElementosXML(view);
        btnCancelarPagoMenbresia.setOnClickListener(v ->{dismiss();});
        return view;
    }
    private  void AsociarlElementosXML(View view){
        lblPrecioMnebresia = view.findViewById(R.id.lblDuracionEstmada);
        lblDuracionMnebresia = view.findViewById(R.id.lblDuracionMnebresia);
        btnCancelarPagoMenbresia = view.findViewById(R.id.btnCancelarPagoMenbresia);
        btnConfirmarPagoMenbresia = view.findViewById(R.id.btnConfirmarPagoMenbresia);
    }
}

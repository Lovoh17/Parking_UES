package com.example.ues_parking.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ues_parking.Models.Membership;
import com.example.ues_parking.R;
import com.google.android.material.button.MaterialButton;

public class ConfirmarPagoMenbresiaDialog extends DialogFragment {
    private static final String ARG_MEMBRESIA = "membresia";

    private MaterialButton btnConfirmarPagoMenbresia, btnCancelarPagoMenbresia;
    private TextView lblPrecioMembresia, lblDuracionMembresia, lblTitulo;
    private Membership membresia;
    private OnPagoConfirmadoListener listener;

    public interface OnPagoConfirmadoListener {
        void onPagoConfirmado(Membership membresia);
    }

    public static ConfirmarPagoMenbresiaDialog newInstance(Membership membresia) {
        ConfirmarPagoMenbresiaDialog fragment = new ConfirmarPagoMenbresiaDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEMBRESIA, membresia);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPagoConfirmadoListener(OnPagoConfirmadoListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            membresia = (Membership) getArguments().getSerializable(ARG_MEMBRESIA);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogo_pagar_menbresia, container, false);
        asociarElementosXML(view);
        configurarVista();
        configurarBotones();
        return view;
    }

    private void asociarElementosXML(View view) {
        lblTitulo = view.findViewById(R.id.textView2);
        lblPrecioMembresia = view.findViewById(R.id.lblDuracionEstmada);
        lblDuracionMembresia = view.findViewById(R.id.lblDuracionMnebresia);
        btnCancelarPagoMenbresia = view.findViewById(R.id.btnCancelarPagoMenbresia);
        btnConfirmarPagoMenbresia = view.findViewById(R.id.btnConfirmarPagoMenbresia);
    }

    private void configurarVista() {
        if (membresia != null) {
            lblTitulo.setText(String.format("Confirmar %s", membresia.getName()));
            lblPrecioMembresia.setText(String.format("$%.2f", membresia.getPrice()));
            lblDuracionMembresia.setText(String.format("Duración de la membresía: %d días",
                    membresia.getDurationDays()));

            // Cambiar color del precio según el tipo de membresía
            int color = getResources().getColor(
                    membresia.getName().equalsIgnoreCase("gold") ?
                            R.color.dorado : R.color.plateado
            );

            lblPrecioMembresia.setTextColor(color);
        }
    }

    private void configurarBotones() {
        btnCancelarPagoMenbresia.setOnClickListener(v -> dismiss());

        btnConfirmarPagoMenbresia.setOnClickListener(v -> {
            if (listener != null && membresia != null) {
                listener.onPagoConfirmado(membresia);
            }
            dismiss();
        });
    }
}
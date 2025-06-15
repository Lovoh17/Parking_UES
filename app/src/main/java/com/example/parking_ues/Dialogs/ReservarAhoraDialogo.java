package com.example.parking_ues.Dialogs;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parking_ues.R;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.Locale;

public class ReservarAhoraDialogo extends DialogFragment {
    private TextView txtHoraIngreso, txtHoraSalida;
    private MaterialButton btnResevarEstaciomaniento, btnPagarEstacionamiento;
    private TextView btnSalir;
    private Calendar calendar;


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
        View view = inflater.inflate(R.layout.dialogo_reservar_ahora, container, false);
        AsociarElementosXML(view);
        ConfigurarHoras();
        btnPagarEstacionamiento.setOnClickListener(v -> {
            PagoConSaldoDialogo dialogo = new PagoConSaldoDialogo();
            dialogo.show(getParentFragmentManager(), "pagarConSaldoDialog");
        });
        btnSalir.setOnClickListener(v -> dismiss()); //SALIR
        return view;
    }
    public void AsociarElementosXML(View view){
        txtHoraIngreso = view.findViewById(R.id.txtHoraIngreso);
        txtHoraSalida = view.findViewById(R.id.txtHoraSalida);

        btnPagarEstacionamiento = view.findViewById(R.id.btnPagarEstacionamiento);
        btnSalir = view.findViewById(R.id.btnSalir);

    }
    //CONFIGUTAR ORAS DE INGRESO Y HORA DE SALIDA
    private void ConfigurarHoras(){
        // Hora ingreso
        calendar = Calendar.getInstance();
        txtHoraIngreso.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    getContext(),
                    (view12, hourOfDay, minute1) -> {
                        String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        txtHoraIngreso.setText(hora);
                    },
                    hour, minute, true
            );
            timePicker.show();
        });

        //Hora salida
        txtHoraSalida.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    getContext(),
                    (view12, hourOfDay, minute1) -> {
                        String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        txtHoraSalida.setText(hora);
                    },
                    hour, minute, true
            );
            timePicker.show();
        });
    }
}

package com.example.ues_parking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistroCode extends Fragment {

    private EditText txtRegistroCode;
    private MaterialButton btnRegistrarCode;
    private TextView lblMensajeInformativo;
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    public RegistroCode() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_code, container, false);
        asociarElementosXML(view);
        inicializarFirebase();
        configurarListeners();
        return view;
    }

    private void asociarElementosXML(View view) {
        txtRegistroCode = view.findViewById(R.id.txtRegistroCode);
        btnRegistrarCode = view.findViewById(R.id.btnRgistrarCode);
        lblMensajeInformativo = view.findViewById(R.id.lblMensajeInformativo);
    }

    private void inicializarFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void configurarListeners() {
        // Validar código mientras se escribe
        txtRegistroCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    verificarCodigoExistente(s.toString());
                } else {
                    btnRegistrarCode.setEnabled(false);
                    lblMensajeInformativo.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Listener para el botón de registro
        btnRegistrarCode.setOnClickListener(v -> registrarCodigo());
    }

    private void verificarCodigoExistente(String codigo) {
        databaseRef.child("codigo_acceso").child(codigo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String status = snapshot.child("status").getValue(String.class);
                            if ("Permitido".equals(status)) {
                                btnRegistrarCode.setEnabled(true);
                                mostrarMensaje("Código válido " + txtRegistroCode.getText(), true);
                            } else {
                                btnRegistrarCode.setEnabled(false);
                                mostrarMensaje("Código ya utilizado", false);
                            }
                        } else {
                            btnRegistrarCode.setEnabled(false);
                            mostrarMensaje("Código no existe " + txtRegistroCode.getText(), false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mostrarMensaje("Error al verificar código", false);
                    }
                });
    }

    private void registrarCodigo() {
        String codigo = txtRegistroCode.getText().toString().trim();

        if (currentUser == null) {
            mostrarMensaje("Error: No hay usuario autenticado", false);
            return;
        }

        // Actualizar el código existente con el usuario
        databaseRef.child("codigo_acceso").child(codigo).child("user_id").setValue(currentUser.getUid());
        // CORREGIDO: Cambié "ststus" por "status"
        databaseRef.child("codigo_acceso").child(codigo).child("status").setValue("Registrado")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mostrarMensaje("Código registrado exitosamente", true);
                        txtRegistroCode.setText("");
                        btnRegistrarCode.setEnabled(false);
                    } else {
                        mostrarMensaje("Error al registrar código", false);
                    }
                });
    }

    private void mostrarMensaje(String mensaje, boolean exito) {
        lblMensajeInformativo.setVisibility(View.VISIBLE);
        lblMensajeInformativo.setText(mensaje);
        lblMensajeInformativo.setTextColor(getResources().getColor(
                exito ? R.color.verde : R.color.rojo_fuerte
        ));

        // Ocultar el mensaje después de 3 segundos
        lblMensajeInformativo.postDelayed(() -> {
            lblMensajeInformativo.setVisibility(View.GONE);
        }, 3000);
    }
}
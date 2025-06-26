package com.example.ues_parking.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.ues_parking.Models.ParkingSession;
import com.example.ues_parking.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PagoConSaldoDialogo extends DialogFragment {

    private static final String TAG = "PagoConSaldoDialogo";
    private static final String ARG_SESSION_ID = "session_id";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_DURATION = "duration";

    // Views
    private TextView tvMontoPagar;
    private TextView tvTiempoTotal;
    private TextView tvDetallesSession;
    private MaterialButton btnConfirmarPago;
    private MaterialButton btnCancelar;

    // Data
    private String sessionId;
    private double amount;
    private long durationMinutes;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    public PagoConSaldoDialogo() {
        // Required empty constructor
    }

    public static PagoConSaldoDialogo newInstance(String sessionId, double amount, long durationMinutes) {
        PagoConSaldoDialogo fragment = new PagoConSaldoDialogo();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        args.putDouble(ARG_AMOUNT, amount);
        args.putLong(ARG_DURATION, durationMinutes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Get arguments
        if (getArguments() != null) {
            sessionId = getArguments().getString(ARG_SESSION_ID);
            amount = getArguments().getDouble(ARG_AMOUNT);
            durationMinutes = getArguments().getLong(ARG_DURATION);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_pago_con_saldo, null);

        initializeViews(view);
        setupData();
        setupClickListeners();

        builder.setView(view);
        return builder.create();
    }

    private void initializeViews(View view) {
        tvMontoPagar = view.findViewById(R.id.tvMontoPagar);
        tvTiempoTotal = view.findViewById(R.id.tvTiempoTotal);
        tvDetallesSession = view.findViewById(R.id.tvDetallesSession);
        btnConfirmarPago = view.findViewById(R.id.btnConfirmarPago);
        btnCancelar = view.findViewById(R.id.btnCancelar);
    }

    private void setupData() {
        // Display amount
        if (tvMontoPagar != null) {
            tvMontoPagar.setText(String.format(Locale.getDefault(), "$%.2f", amount));
        }

        // Display duration
        if (tvTiempoTotal != null) {
            long hours = durationMinutes / 60;
            long minutes = durationMinutes % 60;

            String durationText;
            if (hours > 0) {
                durationText = String.format(Locale.getDefault(),
                        "%d %s y %d %s",
                        hours, hours == 1 ? "hora" : "horas",
                        minutes, minutes == 1 ? "minuto" : "minutos");
            } else {
                durationText = String.format(Locale.getDefault(),
                        "%d %s", minutes, minutes == 1 ? "minuto" : "minutos");
            }
            tvTiempoTotal.setText(durationText);
        }

        // Display session details
        if (tvDetallesSession != null && sessionId != null) {
            tvDetallesSession.setText(String.format("Sesión: %s", sessionId.substring(0, Math.min(8, sessionId.length()))));
        }

        // Show free parking message if amount is 0
        if (amount == 0.0) {
            if (tvMontoPagar != null) {
                tvMontoPagar.setText("GRATIS");
            }
            if (btnConfirmarPago != null) {
                btnConfirmarPago.setText("FINALIZAR ESTACIONAMIENTO");
            }
        }
    }

    private void setupClickListeners() {
        if (btnConfirmarPago != null) {
            btnConfirmarPago.setOnClickListener(v -> processPayment());
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dismiss());
        }
    }

    private void processPayment() {
        if (sessionId == null) {
            showError("Error: Sesión no válida");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showError("Error: Usuario no autenticado");
            return;
        }

        // Disable button to prevent double-clicks
        btnConfirmarPago.setEnabled(false);
        btnConfirmarPago.setText("PROCESANDO...");

        // Update the parking session
        updateParkingSession(currentUser.getUid());
    }

    private void updateParkingSession(String userId) {
        Date exitTime = new Date();

        Map<String, Object> sessionUpdates = new HashMap<>();
        sessionUpdates.put("exitTime", exitTime);
        sessionUpdates.put("duration", durationMinutes);
        sessionUpdates.put("totalCost", amount);
        sessionUpdates.put("status", "completed");
        sessionUpdates.put("paymentStatus", amount > 0 ? "paid" : "free");
        sessionUpdates.put("paymentTime", exitTime);

        databaseRef.child("parking_sessions").child(sessionId)
                .updateChildren(sessionUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Session updated successfully");
                        freeParkingSpace();
                    } else {
                        Log.e(TAG, "Error updating session", task.getException());
                        showError("Error al procesar el pago");
                        resetButton();
                    }
                });
    }

    private void freeParkingSpace() {
        // Get the current session to find the space ID
        databaseRef.child("parking_sessions").child(sessionId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ParkingSession session = snapshot.getValue(ParkingSession.class);
                            if (session != null && session.getSpaceId() != null) {
                                updateParkingSpaceStatus(session.getSpaceId());
                            } else {
                                finishPayment();
                            }
                        } else {
                            finishPayment();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error reading session for space update", error.toException());
                        finishPayment(); // Continue even if space update fails
                    }
                });
    }

    private void updateParkingSpaceStatus(String spaceId) {
        Map<String, Object> spaceUpdates = new HashMap<>();
        spaceUpdates.put("occupied", false);
        spaceUpdates.put("occupiedByUserId", null);
        spaceUpdates.put("occupationStartTime", null);

        databaseRef.child("parking_spaces").child(spaceId)
                .updateChildren(spaceUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Parking space freed successfully");
                    } else {
                        Log.e(TAG, "Error freeing parking space", task.getException());
                    }
                    finishPayment();
                });
    }

    private void finishPayment() {
        if (getContext() != null) {
            String message = amount > 0
                    ? String.format(Locale.getDefault(), "Pago de $%.2f procesado exitosamente", amount)
                    : "Estacionamiento finalizado exitosamente";
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }

        dismiss();

        // Optionally, refresh the parent fragment or activity
        if (getParentFragment() != null) {
            // The parent fragment should reload its data
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        Log.e(TAG, message);
    }

    private void resetButton() {
        if (btnConfirmarPago != null) {
            btnConfirmarPago.setEnabled(true);
            btnConfirmarPago.setText(amount > 0 ? "CONFIRMAR PAGO" : "FINALIZAR ESTACIONAMIENTO");
        }
    }
}
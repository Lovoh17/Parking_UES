package com.example.ues_parking.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ues_parking.Dialogs.PagoConSaldoDialogo;
import com.example.ues_parking.Models.ParkingSession;
import com.example.ues_parking.Models.User;
import com.example.ues_parking.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TiempoEstacionadoFragment extends Fragment {

    private static final String TAG = "TiempoEstacionado";

    // Views
    private MaterialButton btnPagarEstacionamiento;
    private TextView tvTituloPrincipal;
    private TextView lblTiempoEstacionado;
    private TextView lblMontoPagar;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    // Data
    private ParkingSession currentSession;
    private User userData;
    private Handler timerHandler;
    private Runnable timerRunnable;

    // Pricing constants
    private static final double RATE_PER_HOUR_NORMAL = 2.50;
    private static final double RATE_PER_HOUR_VIP = 1.50;
    private static final int MAX_FREE_MINUTES = 15; // 15 minutos gratis

    public TiempoEstacionadoFragment() {
        // Required empty public constructor
    }

    public static TiempoEstacionadoFragment newInstance() {
        return new TiempoEstacionadoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        // Initialize timer
        timerHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tiempo_estacionado, container, false);

        initializeViews(view);
        setupClickListeners();

        if (currentUser != null) {
            loadUserData();
            loadActiveSession();
        } else {
            showError("Usuario no autenticado");
        }

        return view;
    }

    private void initializeViews(View view) {
        btnPagarEstacionamiento = view.findViewById(R.id.btnPagarEstacionamiento);
        tvTituloPrincipal = view.findViewById(R.id.tvTituloPrincipal);
        lblTiempoEstacionado = view.findViewById(R.id.lblTiempoEstacionado);
        lblMontoPagar = view.findViewById(R.id.lblMontoPagar);
    }

    private void setupClickListeners() {
        btnPagarEstacionamiento.setOnClickListener(v -> {
            if (currentSession != null) {
                showPaymentDialog();
            } else {
                Toast.makeText(getContext(), "No hay sesión activa para pagar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        databaseRef.child("users").child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userData = snapshot.getValue(User.class);
                            if (userData != null) {
                                updateWelcomeMessage();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading user data", error.toException());
                    }
                });
    }

    private void loadActiveSession() {
        databaseRef.child("parking_sessions")
                .orderByChild("userId")
                .equalTo(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ParkingSession activeSession = null;

                        for (DataSnapshot sessionSnapshot : snapshot.getChildren()) {
                            ParkingSession session = sessionSnapshot.getValue(ParkingSession.class);
                            if (session != null && "active".equals(session.getStatus())) {
                                activeSession = session;
                                break;
                            }
                        }

                        if (activeSession != null) {
                            currentSession = activeSession;
                            startTimer();
                            loadParkingSpaceInfo();
                            Log.d(TAG, "Active session found: " + activeSession.getSessionId());
                        } else {
                            showNoActiveSession();
                            Log.d(TAG, "No active session found for user");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading active session", error.toException());
                        showError("Error al cargar sesión activa");
                    }
                });
    }

    private void loadParkingSpaceInfo() {
        if (currentSession != null) {
            databaseRef.child("parking_spaces").child(currentSession.getSpaceId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String spaceType = snapshot.child("type").getValue(String.class);
                                int spaceNumber = snapshot.child("spaceNumber").getValue(Integer.class);
                                updateLocationMessage(spaceType, spaceNumber);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error loading parking space info", error.toException());
                        }
                    });
        }
    }

    private void startTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndCost();
                timerHandler.postDelayed(this, 1000); // Update every second
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void updateTimeAndCost() {
        if (currentSession == null || currentSession.getEntryTime() == null) {
            return;
        }

        try {
            Date entryTime = currentSession.getEntryTime();
            Date currentTime = new Date();

            long durationMillis = currentTime.getTime() - entryTime.getTime();
            long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;

            // Update time display
            String timeText = formatDuration(hours, minutes, seconds);
            lblTiempoEstacionado.setText(timeText);

            // Calculate and update cost
            double cost = calculateCost(totalMinutes);
            lblMontoPagar.setText(String.format(Locale.getDefault(), "$%.2f", cost));

        } catch (Exception e) {
            Log.e(TAG, "Error updating time and cost", e);
        }
    }

    private String formatDuration(long hours, long minutes, long seconds) {
        if (hours > 0) {
            return String.format(Locale.getDefault(),
                    "%d %s, %d %s y %d %s",
                    hours, hours == 1 ? "hora" : "horas",
                    minutes, minutes == 1 ? "minuto" : "minutos",
                    seconds, seconds == 1 ? "segundo" : "segundos");
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(),
                    "%d %s y %d %s",
                    minutes, minutes == 1 ? "minuto" : "minutos",
                    seconds, seconds == 1 ? "segundo" : "segundos");
        } else {
            return String.format(Locale.getDefault(),
                    "%d %s", seconds, seconds == 1 ? "segundo" : "segundos");
        }
    }

    private double calculateCost(long totalMinutes) {
        if (totalMinutes <= MAX_FREE_MINUTES) {
            return 0.0; // Free for first 15 minutes
        }

        // Calculate billable minutes (total - free minutes)
        long billableMinutes = totalMinutes - MAX_FREE_MINUTES;
        double billableHours = billableMinutes / 60.0;

        // Determine rate based on plan type
        double hourlyRate = RATE_PER_HOUR_NORMAL;
        if (userData != null && "vip".equalsIgnoreCase(userData.getPlanType())) {
            hourlyRate = RATE_PER_HOUR_VIP;
        }

        // Apply hourly rate with minimum charge of 30 minutes
        double minimumBillableHours = Math.max(billableHours, 0.5); // 30 minutes minimum

        return minimumBillableHours * hourlyRate;
    }

    private void updateWelcomeMessage() {
        if (userData != null && tvTituloPrincipal != null) {
            String name = userData.getName();
            if (name == null || name.trim().isEmpty()) {
                name = userData.getEmail().split("@")[0]; // Use email prefix if no name
            }

            String welcomeText = String.format("Hey %s, estás en el\nestacionamiento", name);
            tvTituloPrincipal.setText(welcomeText);
        }
    }

    private void updateLocationMessage(String spaceType, int spaceNumber) {
        if (userData != null && tvTituloPrincipal != null) {
            String name = userData.getName();
            if (name == null || name.trim().isEmpty()) {
                name = userData.getEmail().split("@")[0];
            }

            String spaceTypeText = "vip".equalsIgnoreCase(spaceType) ? "VIP" : "COM";
            String locationText = String.format("Hey %s, estás en el\nestacionamiento %s - %d",
                    name, spaceTypeText, spaceNumber);
            tvTituloPrincipal.setText(locationText);
        }
    }

    private void showNoActiveSession() {
        if (lblTiempoEstacionado != null) {
            lblTiempoEstacionado.setText("No hay sesión activa");
        }
        if (lblMontoPagar != null) {
            lblMontoPagar.setText("$0.00");
        }
        if (btnPagarEstacionamiento != null) {
            btnPagarEstacionamiento.setEnabled(false);
            btnPagarEstacionamiento.setText("SIN SESIÓN ACTIVA");
        }
        if (tvTituloPrincipal != null) {
            String name = currentUser != null ? currentUser.getEmail().split("@")[0] : "Usuario";
            tvTituloPrincipal.setText(String.format("Hey %s,\nno tienes estacionamiento activo", name));
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        Log.e(TAG, message);
    }

    private void showPaymentDialog() {
        if (currentSession != null) {
            // Calculate current cost for the dialog
            Date entryTime = currentSession.getEntryTime();
            Date currentTime = new Date();
            long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime.getTime() - entryTime.getTime());
            double currentCost = calculateCost(totalMinutes);

            // Create dialog with current session data
            PagoConSaldoDialogo dialogo = PagoConSaldoDialogo.newInstance(
                    currentSession.getSessionId(),
                    currentCost,
                    totalMinutes
            );
            dialogo.show(getParentFragmentManager(), "pagoConSaldo");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentSession != null) {
            startTimer();
        }
    }
}
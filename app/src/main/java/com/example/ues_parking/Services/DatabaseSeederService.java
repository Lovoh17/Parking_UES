package com.example.ues_parking.Services;

import android.util.Log;

import com.example.ues_parking.Models.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DatabaseSeederService {
    private static final String TAG = "DatabaseSeeder";
    private final DatabaseReference parkingSpacesRef;
    private final DatabaseReference parkingSessionsRef;
    private final DatabaseReference usersRef;
    private final DatabaseReference monthlyPlansRef;
    private final DatabaseReference infractionsRef;

    public DatabaseSeederService() {
        try {
            Log.d(TAG, "Initializing DatabaseSeederService...");

            // Use default Firebase Database instance instead of hardcoded URL
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            // Verify database instance
            if (database == null) {
                throw new IllegalStateException("FirebaseDatabase instance is null - check Firebase configuration");
            }

            Log.d(TAG, "Firebase Database instance obtained successfully");

            // Initialize all references with null checks
            DatabaseReference rootRef = database.getReference();
            if (rootRef == null) {
                throw new IllegalStateException("Root database reference is null");
            }

            parkingSpacesRef = rootRef.child("parking_spaces");
            parkingSessionsRef = rootRef.child("parking_sessions");
            usersRef = rootRef.child("users");
            monthlyPlansRef = rootRef.child("monthly_plans");
            infractionsRef = rootRef.child("infractions");

            // Verify that none of the references are null

            Log.d(TAG, "All database references initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing database references", e);
            throw new RuntimeException("Failed to initialize DatabaseSeederService: " + e.getMessage(), e);
        }
    }

    public void seedAllData() {
        try {
            Log.d(TAG, "Starting data seeding...");
            seedUsers();
            seedMonthlyPlans();
            seedParkingSpaces();
            seedParkingSessions();
            seedInfractions();
            Log.d(TAG, "Data seeding completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during data seeding", e);
        }
    }

    /**
     * Inserta 8 espacios de parqueo (4 normales y 4 VIP) con estados variados
     */
    public void seedParkingSpaces() {
        try {
            Log.d(TAG, "Seeding parking spaces...");
            Date now = new Date();

            // Espacios normales (1-4)
            for (int i = 1; i <= 4; i++) {
                ParkingSpace space = new ParkingSpace("normal_" + i, i, "normal");

                // Simular diferentes estados
                if (i == 1) {
                    // Espacio 1: Ocupado por 30 minutos
                    space.setOccupied(true);
                    space.setOccupiedByUserId("user_001");
                    space.setOccupationStartTime(new Date(now.getTime() - TimeUnit.MINUTES.toMillis(30)));
                } else if (i == 3) {
                    // Espacio 3: Reservado
                    space.setReserved(true);
                }

                parkingSpacesRef.child(space.getSpaceId()).setValue(space);
            }

            // Espacios VIP (5-8)
            for (int i = 1; i <= 4; i++) {
                ParkingSpace space = new ParkingSpace("vip_" + i, i + 4, "vip");

                if (i == 2) {
                    // VIP 2: Ocupado por 2 horas
                    space.setOccupied(true);
                    space.setOccupiedByUserId("user_002");
                    space.setOccupationStartTime(new Date(now.getTime() - TimeUnit.HOURS.toMillis(2)));
                } else if (i == 4) {
                    // VIP 4: Reservado
                    space.setReserved(true);
                    space.setOccupiedByUserId("user_002");
                }

                parkingSpacesRef.child(space.getSpaceId()).setValue(space);
            }
            Log.d(TAG, "Parking spaces seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding parking spaces", e);
        }
    }

    /**
     * Inserta usuarios de ejemplo (admin y clientes)
     */
    public void seedUsers() {
        try {
            Log.d(TAG, "Seeding users...");

            // Administrador
            User admin = new User(
                    "admin_001",
                    "admin@parkingues.com",
                    "Administrador Principal",
                    "7777-8888",
                    "admin",
                    "none"
            );
            usersRef.child(admin.getUserId()).setValue(admin);

            // Cliente con plan estándar
            User standardClient = new User(
                    "user_001",
                    "cliente1@email.com",
                    "Juan Pérez",
                    "7777-1234",
                    "cliente",
                    "standard"
            );
            usersRef.child(standardClient.getUserId()).setValue(standardClient);

            // Cliente con plan VIP
            User vipClient = new User(
                    "user_002",
                    "cliente2@email.com",
                    "María López",
                    "7777-5678",
                    "cliente",
                    "vip"
            );
            usersRef.child(vipClient.getUserId()).setValue(vipClient);

            // Cliente sin plan
            User noPlanClient = new User(
                    "user_003",
                    "cliente3@email.com",
                    "Carlos Martínez",
                    "7777-9012",
                    "cliente",
                    "none"
            );
            usersRef.child(noPlanClient.getUserId()).setValue(noPlanClient);

            Log.d(TAG, "Users seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding users", e);
        }
    }

    /**
     * Inserta sesiones de estacionamiento de ejemplo con tiempos realistas
     */
    public void seedParkingSessions() {
        try {
            Log.d(TAG, "Seeding parking sessions...");
            Date now = new Date();

            // 1. Sesión activa en espacio normal (ocupada por 30 minutos)
            ParkingSession activeSession = new ParkingSession(
                    "session_1",
                    "user_001",
                    "normal_1",
                    "ABC-1234", // Placa directamente en lugar de vehicle_001
                    "standard"
            );
            activeSession.setEntryTime(new Date(now.getTime() - TimeUnit.MINUTES.toMillis(30)));
            activeSession.setStatus("active");
            parkingSessionsRef.child(activeSession.getSessionId()).setValue(activeSession);

            // 2. Sesión completada en VIP (ocupada por 2 horas)
            ParkingSession completedSession = new ParkingSession(
                    "session_2",
                    "user_002",
                    "vip_2",
                    "XYZ-5678", // Placa directamente en lugar de vehicle_002
                    "vip"
            );
            completedSession.setEntryTime(new Date(now.getTime() - TimeUnit.HOURS.toMillis(3)));
            completedSession.setExitTime(new Date(now.getTime() - TimeUnit.HOURS.toMillis(1)));
            completedSession.setDuration(120); // 2 horas
            completedSession.setTotalCost(0.00);
            completedSession.setStatus("completed");
            completedSession.setPaymentStatus("paid");
            parkingSessionsRef.child(completedSession.getSessionId()).setValue(completedSession);

            // 3. Sesión con infracción (ocupación prolongada - 8 horas)
            ParkingSession violationSession = new ParkingSession(
                    "session_3",
                    "user_003",
                    "normal_3",
                    "DEF-9012", // Placa directamente en lugar de vehicle_003
                    "none"
            );
            violationSession.setEntryTime(new Date(now.getTime() - TimeUnit.HOURS.toMillis(8)));
            violationSession.setExitTime(now);
            violationSession.setDuration(480); // 8 horas
            violationSession.setTotalCost(30.00);
            violationSession.setStatus("violation");
            violationSession.setPaymentStatus("debt");
            violationSession.setWithinAllowedHours(false);
            parkingSessionsRef.child(violationSession.getSessionId()).setValue(violationSession);

            Log.d(TAG, "Parking sessions seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding parking sessions", e);
        }
    }

    /**
     * Inserta planes mensuales de ejemplo
     */
    public void seedMonthlyPlans() {
        try {
            Log.d(TAG, "Seeding monthly plans...");
            Calendar calendar = Calendar.getInstance();
            Date startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            Date endDate = calendar.getTime();

            // Plan Estándar
            MonthlyPlan standardPlan = new MonthlyPlan(
                    "plan_001",
                    "user_001",
                    "07:00",
                    "19:00",
                    50.00
            );
            standardPlan.setStartDate(startDate);
            standardPlan.setEndDate(endDate);
            monthlyPlansRef.child(standardPlan.getPlanId()).setValue(standardPlan);

            // Plan VIP
            MonthlyPlan vipPlan = new MonthlyPlan(
                    "plan_002",
                    "user_002",
                    "00:00",
                    "23:59",
                    120.00
            );
            vipPlan.setStartDate(startDate);
            vipPlan.setEndDate(endDate);
            monthlyPlansRef.child(vipPlan.getPlanId()).setValue(vipPlan);

            Log.d(TAG, "Monthly plans seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding monthly plans", e);
        }
    }

    /**
     * Inserta infracciones de ejemplo relacionadas con las sesiones
     */
    public void seedInfractions() {
        try {
            Log.d(TAG, "Seeding infractions...");

            // Infracción por horario excedido
            Infraction timeViolation = new Infraction(
                    "infraction_1",
                    "user_003",
                    "session_3",
                    "fuera del rango de horas",
                    "Estacionamiento fuera del horario permitido",
                    25.00
            );
            infractionsRef.child(timeViolation.getInfactionId()).setValue(timeViolation);

            // Infracción por no pago
            Infraction paymentViolation = new Infraction(
                    "infraction_2",
                    "user_003",
                    "session_3",
                    "sin pago",
                    "No se realizó el pago del estacionamiento",
                    30.00
            );
            infractionsRef.child(paymentViolation.getInfactionId()).setValue(paymentViolation);

            // Infracción resuelta (ejemplo histórico)
            Infraction resolvedInfraction = new Infraction(
                    "infraction_3",
                    "user_002",
                    "session_2",
                    "fuera del rango de horas",
                    "Infracción pagada el mismo día",
                    20.00
            );
            resolvedInfraction.setStatus("paid");
            resolvedInfraction.setResolvedAt(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
            infractionsRef.child(resolvedInfraction.getInfactionId()).setValue(resolvedInfraction);

            Log.d(TAG, "Infractions seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding infractions", e);
        }
    }
}
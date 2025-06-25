package com.example.ues_parking.Services;

import android.util.Log;

import com.example.ues_parking.Models.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class DatabaseSeederService {
    private final DatabaseReference parkingSpacesRef;
    private final DatabaseReference parkingSessionsRef;
    private final DatabaseReference usersRef;
    private final DatabaseReference monthlyPlansRef;
    private final DatabaseReference infractionsRef;

    public DatabaseSeederService() {
        // Configuración específica para tu base de datos Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingues-default-rtdb.firebaseio.com/");

        // Referencias a los nodos principales
        parkingSpacesRef = database.getReference("parking_spaces");
        parkingSessionsRef = database.getReference("parking_sessions");
        usersRef = database.getReference("users");
        monthlyPlansRef = database.getReference("monthly_plans");
        infractionsRef = database.getReference("infractions");
    }

    /**
     * Inserta 8 espacios de parqueo (4 normales y 4 VIP)
     */
    public void seedParkingSpaces() {
        // Espacios normales (1-4)
        for (int i = 1; i <= 4; i++) {
            ParkingSpace space = new ParkingSpace(
                    "normal_" + i,
                    i,
                    "normal"
            );
            parkingSpacesRef.child("normal_" + i).setValue(space);
        }

        // Espacios VIP (5-8)
        for (int i = 1; i <= 4; i++) {
            ParkingSpace space = new ParkingSpace(
                    "vip_" + i,
                    i + 4,
                    "vip"
            );
            parkingSpacesRef.child("vip_" + i).setValue(space);
        }
    }

    /**
     * Inserta usuarios de ejemplo (admin y clientes)
     */
    public void seedUsers() {
        // 1. Administrador
        User admin = new User(
                "admin_001",
                "admin@parkingues.com",
                "Administrador Principal",
                "7777-8888",
                "admin",
                "none"
        );
        usersRef.child("admin_001").setValue(admin);

        // 2. Cliente con plan estándar
        User standardClient = new User(
                "user_001",
                "cliente1@email.com",
                "Juan Pérez",
                "7777-1234",
                "cliente",
                "standard"
        );
        usersRef.child("user_001").setValue(standardClient);

        // 3. Cliente con plan VIP
        User vipClient = new User(
                "user_002",
                "cliente2@email.com",
                "María López",
                "7777-5678",
                "cliente",
                "vip"
        );
        usersRef.child("user_002").setValue(vipClient);

        // 4. Cliente sin plan
        User noPlanClient = new User(
                "user_003",
                "cliente3@email.com",
                "Carlos Martínez",
                "7777-9012",
                "cliente",
                "none"
        );
        usersRef.child("user_003").setValue(noPlanClient);
    }

    /**
     * Inserta sesiones de estacionamiento de ejemplo
     */
    public void seedParkingSessions() {
        // 1. Sesión activa en espacio normal (con plan estándar)
        ParkingSession activeSession = new ParkingSession(
                "session_1",
                "user_001",
                "normal_1",
                "standard"
        );
        parkingSessionsRef.child("session_1").setValue(activeSession);

        // 2. Sesión completada en VIP (con plan VIP)
        ParkingSession completedSession = new ParkingSession(
                "session_2",
                "user_002",
                "vip_2",
                "vip"
        );
        completedSession.setExitTime(new Date(System.currentTimeMillis() - 3600000));
        completedSession.setDuration(120);
        completedSession.setTotalCost(0.00);
        completedSession.setStatus("completed");
        completedSession.setPaymentStatus("paid");
        parkingSessionsRef.child("session_2").setValue(completedSession);

        // 3. Sesión con infracción (sin plan)
        ParkingSession violationSession = new ParkingSession(
                "session_3",
                "user_003",
                "normal_3",
                "none"
        );
        violationSession.setExitTime(new Date());
        violationSession.setDuration(360);
        violationSession.setTotalCost(30.00);
        violationSession.setStatus("violation");
        violationSession.setPaymentStatus("debt");
        violationSession.setWithinAllowedHours(false);
        parkingSessionsRef.child("session_3").setValue(violationSession);
    }

    /**
     * Inserta planes mensuales de ejemplo
     */
    public void seedMonthlyPlans() {
        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();

        // 1. Plan Estándar
        MonthlyPlan standardPlan = new MonthlyPlan(
                "plan_001",
                "user_001",
                "07:00",
                "19:00",
                50.00
        );
        standardPlan.setStartDate(startDate);
        standardPlan.setEndDate(endDate);
        monthlyPlansRef.child("plan_001").setValue(standardPlan);

        // 2. Plan VIP
        MonthlyPlan vipPlan = new MonthlyPlan(
                "plan_002",
                "user_002",
                "00:00",
                "23:59",
                120.00
        );
        vipPlan.setStartDate(startDate);
        vipPlan.setEndDate(endDate);
        monthlyPlansRef.child("plan_002").setValue(vipPlan);
    }

    /**
     * Inserta infracciones de ejemplo
     */
    public void seedInfractions() {
        // 1. Infracción por horario excedido
        Infaction timeViolation = new Infaction(
                "infraction_1",
                "user_003",
                "session_3",
                "fuera del rango de horas",
                "Estacionamiento fuera del horario permitido",
                25.00
        );
        infractionsRef.child("infraction_1").setValue(timeViolation);

        // 2. Infracción por no pago
        Infaction paymentViolation = new Infaction(
                "infraction_2",
                "user_003",
                "session_3",
                "sin pago",
                "No se realizó el pago del estacionamiento",
                30.00
        );
        infractionsRef.child("infraction_2").setValue(paymentViolation);

        // 3. Infracción resuelta (ejemplo histórico)
        Infaction resolvedInfraction = new Infaction(
                "infraction_3",
                "user_002",
                "session_2",
                "fuera del rango de horas",
                "Infracción pagada el mismo día",
                20.00
        );
        resolvedInfraction.setStatus("paid");
        resolvedInfraction.setResolvedAt(new Date(System.currentTimeMillis() - 86400000));
        infractionsRef.child("infraction_3").setValue(resolvedInfraction);
    }

    /**
     * Método completo para sembrar todos los datos iniciales
     */
    public void seedAllData() {
        Log.d("DatabaseSeederService", "Sembrando datos iniciales...");
        seedParkingSpaces();
        seedUsers();
        seedMonthlyPlans();
        seedParkingSessions();
        seedInfractions();
    }
}
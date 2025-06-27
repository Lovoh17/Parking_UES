package com.example.ues_parking.Services;

import android.util.Log;

import com.example.ues_parking.Models.Membership;
import com.example.ues_parking.Models.UserMembership;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MembershipSeederService {
    private static final String TAG = "MembershipSeeder";
    private final DatabaseReference membershipPlansRef;
    private final DatabaseReference userMembershipsRef;
    private final DatabaseReference usersRef;

    public MembershipSeederService() {
        try {
            Log.d(TAG, "Initializing MembershipSeederService...");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference rootRef = database.getReference();

            membershipPlansRef = rootRef.child("membership_plans");
            userMembershipsRef = rootRef.child("user_memberships");
            usersRef = rootRef.child("users");

            Log.d(TAG, "Membership database references initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database references", e);
            throw new RuntimeException("Failed to initialize MembershipSeederService: " + e.getMessage(), e);
        }
    }

    public void seedMembershipData() {
        try {
            Log.d(TAG, "Starting membership data seeding...");
            seedMembershipPlans();
            seedUserMemberships();
            Log.d(TAG, "Membership data seeding completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during membership data seeding", e);
        }
    }

    /**
     * Inserta los planes de membresía disponibles (Silver y Gold)
     */
    public void seedMembershipPlans() {
        try {
            Log.d(TAG, "Seeding membership plans...");

            // Plan Silver
            Membership silverPlan = new Membership(
                    "plan_silver",
                    "Silver",
                    "Membresía básica con beneficios esenciales",
                    19.99,
                    Arrays.asList(
                            "Soporte prioritario",
                            "Descuentos especiales del 10%",
                            "Acceso básico a estacionamiento"
                    ),
                    30 // 30 días de duración
            );
            membershipPlansRef.child(silverPlan.getPlanId()).setValue(silverPlan);

            // Plan Gold
            Membership goldPlan = new Membership(
                    "plan_gold",
                    "Gold",
                    "Membresía avanzada con beneficios premium",
                    39.99,
                    Arrays.asList(
                            "Beneficios VIP",
                            "Acceso anticipado a nuevas funciones",
                            "Descuentos especiales del 20%",
                            "Estacionamiento prioritario"
                    ),
                    30 // 30 días de duración
            );
            membershipPlansRef.child(goldPlan.getPlanId()).setValue(goldPlan);

            Log.d(TAG, "Membership plans seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding membership plans", e);
        }
    }

    /**
     * Inserta membresías de ejemplo para usuarios
     */
    public void seedUserMemberships() {
        try {
            Log.d(TAG, "Seeding user memberships...");
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);

            // Membresía Silver activa para user_001
            calendar.add(Calendar.DAY_OF_MONTH, 15); // Expira en 15 días
            UserMembership silverMembership = new UserMembership(
                    "user_001",
                    "plan_silver",
                    30,
                    "credit_card",
                    "txn_silver_001"
            );
            silverMembership.setMembershipId("membership_001");
            silverMembership.setStartDate(now);
            silverMembership.setEndDate(calendar.getTime());
            userMembershipsRef.child(silverMembership.getMembershipId()).setValue(silverMembership);

            // Actualizar tipo de plan del usuario
            updateUserPlanType("user_001", "silver");

            // Membresía Gold activa para user_002
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, 5); // Expira en 5 días
            UserMembership goldMembership = new UserMembership(
                    "user_002",
                    "plan_gold",
                    30,
                    "paypal",
                    "txn_gold_002"
            );
            goldMembership.setMembershipId("membership_002");
            goldMembership.setStartDate(now);
            goldMembership.setEndDate(calendar.getTime());
            userMembershipsRef.child(goldMembership.getMembershipId()).setValue(goldMembership);

            // Actualizar tipo de plan del usuario
            updateUserPlanType("user_002", "gold");

            // Membresía Gold expirada para user_003 (histórico)
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, -10); // Expiró hace 10 días
            UserMembership expiredMembership = new UserMembership(
                    "user_003",
                    "plan_gold",
                    30,
                    "credit_card",
                    "txn_gold_003"
            );
            expiredMembership.setMembershipId("membership_003");
            expiredMembership.setStartDate(new Date(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(30)));
            expiredMembership.setEndDate(calendar.getTime());
            expiredMembership.setActive(false);
            userMembershipsRef.child(expiredMembership.getMembershipId()).setValue(expiredMembership);

            // Actualizar tipo de plan del usuario (revertido a básico)
            updateUserPlanType("user_003", "basic");

            Log.d(TAG, "User memberships seeded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding user memberships", e);
        }
    }

    /**
     * Actualiza el tipo de plan de un usuario
     */
    private void updateUserPlanType(String userId, String planType) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("planType", planType);
        usersRef.child(userId).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Updated plan type for user: " + userId);
                    } else {
                        Log.e(TAG, "Failed to update plan type for user: " + userId);
                    }
                });
    }
}
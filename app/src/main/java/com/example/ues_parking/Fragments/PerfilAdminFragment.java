package com.example.ues_parking.Fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ues_parking.Activities.MainActivity;
import com.example.ues_parking.EditarContraseniaActivity;
import com.example.ues_parking.EditarPerfilActivity;
import com.example.ues_parking.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilAdminFragment extends Fragment {
    private TextView lblNombreAdminPefil, lblCorreoAdminPerfil, lblRol;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private TextView lblEditarPerfilAdmin, lblCambiarContraseniaAdmin, lblCerrarSecionAdmin;

    public PerfilAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Firebase Auth y Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        AsociarElementosXML(view);
        cargarDatosAdmin();
        configurarEventos();

        return view;
    }

    private void AsociarElementosXML(View view) {
        lblNombreAdminPefil = view.findViewById(R.id.lblNombreAdminPefil);
        lblCorreoAdminPerfil = view.findViewById(R.id.lblCorreoAdminPerfil);
        lblRol = view.findViewById(R.id.lblRol);

        lblEditarPerfilAdmin = view.findViewById(R.id.lblEditarPerfilAdmin);
        lblCambiarContraseniaAdmin = view.findViewById(R.id.lblCambiarContraseniaAdmin);
        lblCerrarSecionAdmin = view.findViewById(R.id.lblCerrarSecionAdmin);
    }

    private void cargarDatosAdmin() {
        FirebaseUser admin = mAuth.getCurrentUser();

        if (admin != null) {
            // Mostrar email (siempre disponible)
            lblCorreoAdminPerfil.setText(admin.getEmail());

            // Obtener datos del usuario desde la tabla 'users' filtrando por rol 'admin'
            mDatabase.child("users").child(admin.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Verificar que el usuario tenga rol de admin
                                String rol = dataSnapshot.child("role").getValue(String.class);

                                if (rol != null && rol.equals("admin")) {
                                    // Obtener valores de la base de datos
                                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                                    String apellido = dataSnapshot.child("apellido").getValue(String.class);

                                    // Mostrar nombre completo
                                    if (nombre != null && apellido != null) {
                                        lblNombreAdminPefil.setText(nombre + " " + apellido);
                                    } else if (admin.getDisplayName() != null) {
                                        lblNombreAdminPefil.setText(admin.getDisplayName());
                                    } else {
                                        lblNombreAdminPefil.setText("Administrador UES");
                                    }

                                    // Mostrar rol
                                    lblRol.setText("Rol: " + rol);
                                } else {
                                    // Usuario no tiene rol de admin - cerrar sesión por seguridad
                                    Toast.makeText(getContext(), "Acceso no autorizado", Toast.LENGTH_SHORT).show();
                                    cerrarSesion();
                                }
                            } else {
                                // No existe el nodo del usuario en la base de datos
                                Log.w(TAG, "Datos del administrador no encontrados en Realtime Database");
                                if (admin.getDisplayName() != null) {
                                    lblNombreAdminPefil.setText(admin.getDisplayName());
                                } else {
                                    lblNombreAdminPefil.setText("Administrador UES");
                                }
                                lblRol.setText("Rol: Administrador");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "Error al leer datos del administrador", databaseError.toException());
                            Toast.makeText(getContext(), "Error al cargar datos del administrador", Toast.LENGTH_SHORT).show();

                            // Mostrar datos básicos como fallback
                            if (admin.getDisplayName() != null) {
                                lblNombreAdminPefil.setText(admin.getDisplayName());
                            } else {
                                lblNombreAdminPefil.setText("Administrador UES");
                            }
                            lblRol.setText("Rol: Administrador");
                        }
                    });
        } else {
            // Usuario no autenticado, redirigir al login
            redirigirALogin();
        }
    }

    private void configurarEventos() {
        // EVENTO PARA EDITAR PERFIL
        lblEditarPerfilAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });

        // EVENTO PARA CAMBIAR CONTRASEÑA
        lblCambiarContraseniaAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarContraseniaActivity.class);
            startActivity(intent);
        });

        // EVENTO PARA CERRAR SESIÓN
        lblCerrarSecionAdmin.setOnClickListener(v -> mostrarDialogoCerrarSesion());
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?")
                .setPositiveButton("Sí, cerrar sesión", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        mostrarProgressDialog("Cerrando sesión...");

        try {
            // 1. Cerrar sesión en Firebase
            mAuth.signOut();

            // 2. Cerrar sesión en Google (si aplica)
            mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
                ocultarProgressDialog();
                redirigirALogin();
                Log.d(TAG, "Sesión cerrada exitosamente");
            });
        } catch (Exception e) {
            ocultarProgressDialog();
            Log.e(TAG, "Error al cerrar sesión", e);
            Toast.makeText(requireContext(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
            redirigirALogin();
        }
    }

    private void redirigirALogin() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void mostrarProgressDialog(String mensaje) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(mensaje);
        progressDialog.show();
    }

    private void ocultarProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ocultarProgressDialog();
    }
}
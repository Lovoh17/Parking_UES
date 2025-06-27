package com.example.ues_parking.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class PerfilClienteFragment extends Fragment {
    private static final String TAG = "PerfilClienteFragment";

    private ImageView imgCliente;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;

    private TextView lblNombreClientePeril, lblCorreoClientePerfil, lblSaldoClientePerfil;
    private TextView lblMenbresias, lblHistorialCliente;
    private TextView lblEditarPerfil, lblCambiarContrasenia, lblReportarPerfil, lblCerrarSecionCliente;

    public PerfilClienteFragment() {}

    public static PerfilClienteFragment newInstance(String param1, String param2) {
        PerfilClienteFragment fragment = new PerfilClienteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);

        // Asociar elementos XML
        AsociarElementosXML(view);
        //cargar daros del usuario
        cargarDatosUsuario();

        // Configurar eventos
        configurarEventos();

        return view;
    }

    private void AsociarElementosXML(View view) {
        lblMenbresias = view.findViewById(R.id.lblMenbresias);
        lblHistorialCliente = view.findViewById(R.id.lblHistorialCliente);

        lblNombreClientePeril = view.findViewById(R.id.lblNombreAdminPefil);
        lblCorreoClientePerfil = view.findViewById(R.id.lblCorreoAdminPerfil);
        lblSaldoClientePerfil = view.findViewById(R.id.lblRol);

        lblEditarPerfil = view.findViewById(R.id.lblEditarPerfilAdmin);
        lblCambiarContrasenia = view.findViewById(R.id.lblCambiarContraseniaAdmin);
        lblReportarPerfil = view.findViewById(R.id.lblReportarPerfil);
        lblCerrarSecionCliente = view.findViewById(R.id.lblCerrarSecionCliente);
    }

    private void configurarEventos() {
        // EVENTO PARA IR A MEMBRESÍAS
        lblMenbresias.setOnClickListener(v -> navegarAFragment(new MembresiasFragment()));

        // EVENTO PARA IR A HISTORIAL
        lblHistorialCliente.setOnClickListener(v -> navegarAFragment(new HistorialClienteFragment()));

        // EVENTO PARA EDITAR PERFIL
        lblEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });

        // EVENTO PARA CAMBIAR CONTRASEÑA
        lblCambiarContrasenia.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditarContraseniaActivity.class);
            startActivity(intent);
        });

        // EVENTO PARA CERRAR SESIÓN
        lblCerrarSecionCliente.setOnClickListener(v -> mostrarDialogoCerrarSesion());
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            lblCorreoClientePerfil.setText(user.getEmail());
            mDatabase.child("users").child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String nombre = dataSnapshot.child("nombre").getValue(String.class);
                                String apellido = dataSnapshot.child("apellido").getValue(String.class);
                                Double saldo = dataSnapshot.child("saldo").getValue(Double.class);
                                if (nombre != null && apellido != null) {
                                    lblNombreClientePeril.setText(nombre + " " + apellido);
                                } else if (user.getDisplayName() != null) {
                                    lblNombreClientePeril.setText(user.getDisplayName());
                                } else {
                                    lblNombreClientePeril.setText("Usuario UES");
                                }
                                if (saldo != null) {
                                    lblSaldoClientePerfil.setText(String.format("Saldo: $%.2f", saldo));
                                } else {
                                    lblSaldoClientePerfil.setText("Saldo: $0.00");
                                }
                            } else {
                                Log.w(TAG, "Datos del usuario no encontrados en Realtime Database");
                                if (user.getDisplayName() != null) {
                                    lblNombreClientePeril.setText(user.getDisplayName());
                                } else {
                                    lblNombreClientePeril.setText("Usuario UES");
                                }
                                lblSaldoClientePerfil.setText("Saldo: $0.00");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "Error al leer datos del usuario", databaseError.toException());
                            Toast.makeText(getContext(), "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
                            if (user.getDisplayName() != null) {
                                lblNombreClientePeril.setText(user.getDisplayName());
                            } else {
                                lblNombreClientePeril.setText("Usuario UES");
                            }
                            lblSaldoClientePerfil.setText("Saldo: $0.00");
                        }
                    });
        } else {
            redirigirALogin();
        }
    }

    private void navegarAFragment(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // MÉTODO PARA MOSTRAR DIÁLOGO DE CERRAR SESIÓN
    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar sesión", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    // LÓGICA PARA CERRAR SESIÓN
    private void cerrarSesion() {
        mostrarProgressDialog("Cerrando sesión...");
        try {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
                limpiarDatosLocales();
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

    private void limpiarDatosLocales() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void redirigirALogin() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
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
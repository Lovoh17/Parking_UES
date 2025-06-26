package com.example.ues_parking;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {
    private TextView btnSalir;
    private TextInputEditText txtNombre, txtCorreo, txtTelefono, txtNuevaContrasena, txtConfirmarContrasena;
    private MaterialButton btnEditarPerfil;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        // Inicializar vistas
        inicializarVistas();

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Configurar eventos
        configurarEventos();
    }

    private void inicializarVistas() {
        btnSalir = findViewById(R.id.btnSalir);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        txtNombre = findViewById(R.id.lblNombreAdminPefil);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNuevaContrasena = findViewById(R.id.txtNuevaContrasena);
        txtConfirmarContrasena = findViewById(R.id.txtConfirmarNuevaContrasena);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void cargarDatosUsuario() {
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mostrarProgressDialog("Cargando datos...");

        mDatabase.child("users").child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ocultarProgressDialog();

                        if (dataSnapshot.exists()) {
                            // Obtener valores de la base de datos
                            String nombre = dataSnapshot.child("nombre").getValue(String.class);
                            String apellido = dataSnapshot.child("apellido").getValue(String.class);
                            String telefono = dataSnapshot.child("telefono").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);

                            // Mostrar datos en los campos
                            if (nombre != null && apellido != null) {
                                txtNombre.setText(nombre + " " + apellido);
                            }

                            if (telefono != null) {
                                txtTelefono.setText(telefono);
                            }

                            if (email != null) {
                                txtCorreo.setText(email);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ocultarProgressDialog();
                        Toast.makeText(EditarPerfilActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        Log.e("EditarPerfil", "Error al leer datos", databaseError.toException());
                    }
                });
    }

    private void configurarEventos() {
        // Botón para salir
        btnSalir.setOnClickListener(v -> finish());

        // Botón para editar perfil
        btnEditarPerfil.setOnClickListener(v -> {
            if (validarCampos()) {
                actualizarPerfil();
            }
        });
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nuevaContrasena = txtNuevaContrasena.getText().toString().trim();
        String confirmarContrasena = txtConfirmarContrasena.getText().toString().trim();

        if (nombre.isEmpty()) {
            txtNombre.setError("Ingrese su nombre");
            return false;
        }

        if (telefono.isEmpty()) {
            txtTelefono.setError("Ingrese su teléfono");
            return false;
        }

        if (!nuevaContrasena.isEmpty() && nuevaContrasena.length() < 6) {
            txtNuevaContrasena.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            txtConfirmarContrasena.setError("Las contraseñas no coinciden");
            return false;
        }

        return true;
    }

    private void actualizarPerfil() {
        mostrarProgressDialog("Actualizando perfil...");

        String nombreCompleto = txtNombre.getText().toString().trim();
        String[] nombres = nombreCompleto.split(" ", 2);
        String nombre = nombres.length > 0 ? nombres[0] : "";
        String apellido = nombres.length > 1 ? nombres[1] : "";
        String telefono = txtTelefono.getText().toString().trim();
        String nuevaContrasena = txtNuevaContrasena.getText().toString().trim();

        // Crear mapa de actualización
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("apellido", apellido);
        updates.put("telefono", telefono);

        // Actualizar en Realtime Database
        mDatabase.child("users").child(currentUser.getUid())
                .updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!nuevaContrasena.isEmpty()) {
                            // Si se proporcionó una nueva contraseña, actualizarla
                            currentUser.updatePassword(nuevaContrasena)
                                    .addOnCompleteListener(passwordTask -> {
                                        ocultarProgressDialog();
                                        if (passwordTask.isSuccessful()) {
                                            Toast.makeText(EditarPerfilActivity.this, "Perfil y contraseña actualizados", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado pero error al cambiar contraseña", Toast.LENGTH_SHORT).show();
                                            Log.e("EditarPerfil", "Error al actualizar contraseña", passwordTask.getException());
                                        }
                                    });
                        } else {
                            ocultarProgressDialog();
                            Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        ocultarProgressDialog();
                        Toast.makeText(EditarPerfilActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                        Log.e("EditarPerfil", "Error al actualizar datos", task.getException());
                    }
                });
    }

    private void mostrarProgressDialog(String mensaje) {
        progressDialog.setMessage(mensaje);
        progressDialog.show();
    }

    private void ocultarProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
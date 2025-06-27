package com.example.ues_parking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ues_parking.Activities.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Spinner spRol;
    private TextView lblIniciarSesion;
    private TextInputEditText txtNombreUsuario, txtCorreo, txtTelefono, txtNuevaContrasena, txtConfirmarContrasena;
    private MaterialButton btnRegistrarse;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar vistas
        spRol = findViewById(R.id.spRol);
        lblIniciarSesion = findViewById(R.id.lblIniciarSesion);
        txtNombreUsuario = findViewById(R.id.lblNombreAdminPefil);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNuevaContrasena = findViewById(R.id.txtNuevaContrasena);
        txtConfirmarContrasena = findViewById(R.id.txtConfirmarNuevaContrasena);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        // Configurar listeners
        lblIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegistrarse.setOnClickListener(v -> registrarUsuario());

        configurarSpinners();
    }

    private void configurarSpinners() {
        List<String> rol = Arrays.asList("Admin", "Cliente");
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(this, R.layout.spinner_personalizado, rol);
        rolAdapter.setDropDownViewResource(R.layout.spinner_personalizado);
        spRol.setAdapter(rolAdapter);
    }

    private void registrarUsuario() {
        // Obtener valores de los campos
        String nombre = txtNombreUsuario.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String contrasena = txtNuevaContrasena.getText().toString().trim();
        String confirmarContrasena = txtConfirmarContrasena.getText().toString().trim();
        String rol = spRol.getSelectedItem().toString().toLowerCase();

        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro en Auth exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            verificarCorreoElectronico(user);
                                        } else {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Error al enviar verificación: " + emailTask.getException().getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        // Error en registro
                        String errorMessage = "Error al registrar: " + task.getException().getMessage();

                        // Manejo específico de errores comunes
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "Este correo ya está registrado";
                        } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            errorMessage = "Contraseña demasiado débil";
                        }

                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Verifica si el correo electrónico ha sido confirmado y maneja el estado de verificación
     */
    private void verificarCorreoElectronico(FirebaseUser user) {
        // Recargar el usuario para obtener el estado más reciente de verificación
        user.reload().addOnCompleteListener(reloadTask -> {
            if (reloadTask.isSuccessful()) {
                if (user.isEmailVerified()) {
                    // Correo ya verificado - proceder con el flujo normal
                    guardarDatosUsuarioEnDatabase(user.getUid(),
                            txtNombreUsuario.getText().toString().trim(),
                            txtCorreo.getText().toString().trim(),
                            txtTelefono.getText().toString().trim(),
                            spRol.getSelectedItem().toString().toLowerCase());

                    // Redirigir al menú principal
                    redirigirSegunRol(spRol.getSelectedItem().toString().toLowerCase());
                } else {
                    // Mostrar diálogo para verificar correo
                    mostrarDialogoVerificacionCorreo(user);
                }
            } else {
                // Error al recargar usuario
                Toast.makeText(RegisterActivity.this,
                        "Error al verificar estado del correo: " + reloadTask.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra diálogo con opciones para manejar la verificación de correo
     */
    private void mostrarDialogoVerificacionCorreo(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Verificación de correo requerida");
        builder.setMessage("Hemos enviado un enlace de verificación a " + user.getEmail() +
                "\n\nPor favor verifica tu correo para continuar.");

        builder.setPositiveButton("Correo verificado", (dialog, which) -> {
            // Volver a verificar si el usuario marcó como verificado
            verificarCorreoElectronico(user);
        });

        builder.setNeutralButton("Reenviar verificación", (dialog, which) -> {
            // Reenviar el correo de verificación
            reenviarCorreoVerificacion(user);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            // Opcional: cerrar sesión si el usuario cancela
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Por favor verifica tu correo más tarde", Toast.LENGTH_SHORT).show();
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Personalizar botones
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.rojo_ues));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.rojo_ues));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blanco_20));
    }

    /**
     * Reenvía el correo de verificación
     */
    private void reenviarCorreoVerificacion(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Correo de verificación reenviado a " + user.getEmail(),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Error al reenviar: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosUsuarioEnDatabase(String userId, String name, String email, String phone, String role) {
        // Crear mapa de datos del usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("userId", userId);
        usuario.put("email", email);
        usuario.put("name", name);
        usuario.put("phone", phone);
        usuario.put("role", role);
        usuario.put("createdAt", new Date().getTime()); // Guardamos timestamp
        usuario.put("isActive", true);
        usuario.put("planType", "basic"); // Plan por defecto

        // Guardar en Realtime Database
        mDatabase.child("users").child(userId).setValue(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    // Redirigir a la pantalla principal según el rol
                    redirigirSegunRol(role);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error al guardar datos: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void redirigirSegunRol(String role) {
        // Aquí puedes implementar la lógica para redirigir a diferentes actividades según el rol
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
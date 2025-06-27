package com.example.ues_parking.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ues_parking.Models.User;
import com.example.ues_parking.RegisterActivity;
import com.example.ues_parking.R;
import com.example.ues_parking.Services.DatabaseSeederService;
import com.example.ues_parking.Services.MembershipSeederService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "GoogleSignIn";
    private static final String USERS_PATH = "users";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference databaseRef;

    private EditText etEmail, etPassword;
    private Button btnGoogleSignIn, btnIniciarSesion;
    private TextView forgotPassword, lblRegisterUsers;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();

        //seedDatabaseSafely();
    }

    /********Funcion paraa inicializar la base de datos **********/
    /*
    private void seedDatabaseSafely() {
        try {
            Log.d("MainActivity", "Attempting to seed database...");
            DatabaseSeederService seeder = new DatabaseSeederService();
            seeder.seedAllData();
            Log.d("MainActivity", "Database seeding initiated successfully");
        } catch (Exception e) {
            Log.e("MainActivity", "Error initializing database seeder", e);
            Toast.makeText(this, "Warning: Database initialization may have issues", Toast.LENGTH_SHORT).show();
        }
    }*/



    private void initViews() {
        etEmail = findViewById(R.id.txtCorreo);
        etPassword = findViewById(R.id.txtPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        forgotPassword = findViewById(R.id.lblOlvidePassword);
        lblRegisterUsers = findViewById(R.id.lblRgisterUsers);
    }

    private void setupListeners() {
        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validarCampos(email, password)) {
                iniciarSesionEmailPassword(email, password);
            }
        });

        // Botón de Google Sign In
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        // Link de registro
        lblRegisterUsers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Recuperar contraseña
        forgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                resetearPassword(email);
            } else {
                Toast.makeText(this, "Ingresa tu email primero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Usuario ya autenticado, verificar rol y redirigir
            verificarRolYRedirigir(currentUser.getUid());
        }
    }

    // ********************* Validaciones ***********************************
    private boolean validarCampos(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            etEmail.setError("Email requerido");
            isValid = false;
        } else if (!isValidEmail(email)) {
            etEmail.setError("Email inválido");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Contraseña requerida");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // ********************* Inicio de sesión con Email/Password ***********************************
    private void iniciarSesionEmailPassword(String email, String password) {
        mostrarProgressDialog("Iniciando sesión...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    ocultarProgressDialog();

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                verificarRolYRedirigir(user.getUid());
                            } else {
                                Toast.makeText(this, "Por favor verifica tu email primero.", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        }
                    } else {
                        String errorMessage = "Error en inicio de sesión";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ********************* Inicio de sesión con Google ***********************************
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Error en el inicio de sesión con Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mostrarProgressDialog("Autenticando con Google...");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verificarYCrearUsuarioGoogle(user);
                        }
                    } else {
                        ocultarProgressDialog();
                        Toast.makeText(this, "Autenticación fallida: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarYCrearUsuarioGoogle(FirebaseUser firebaseUser) {
        databaseRef.child(USERS_PATH).child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            crearUsuarioGoogleEnDB(firebaseUser);
                        } else {
                            ocultarProgressDialog();
                            verificarRolYRedirigir(firebaseUser.getUid());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ocultarProgressDialog();
                        Log.e(TAG, "Error al verificar usuario en DB", error.toException());
                        Toast.makeText(MainActivity.this, "Error al verificar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void crearUsuarioGoogleEnDB(FirebaseUser firebaseUser) {
        User user = new User(
                firebaseUser.getUid(),
                firebaseUser.getEmail(),
                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "",
                "",
                "cliente",
                "none"
        );

        databaseRef.child(USERS_PATH).child(firebaseUser.getUid()).setValue(user)
                .addOnCompleteListener(task -> {
                    ocultarProgressDialog();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Usuario de Google guardado en RTDB correctamente");
                        verificarRolYRedirigir(firebaseUser.getUid());
                    } else {
                        Log.e(TAG, "Error al guardar usuario de Google en RTDB", task.getException());
                        Toast.makeText(MainActivity.this, "Error al crear perfil de usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ********************* Verificación de roles y redirección ***********************************
    private void verificarRolYRedirigir(String userId) {
        mostrarProgressDialog("Verificando permisos...");

        databaseRef.child(USERS_PATH).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ocultarProgressDialog();

                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                redirigirSegunRol(user.getRole());
                            } else {
                                Toast.makeText(MainActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ocultarProgressDialog();
                        Log.e(TAG, "Error al obtener rol del usuario", error.toException());
                        Toast.makeText(MainActivity.this, "Error al verificar permisos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirigirSegunRol(String rol) {
        Intent intent;

        if ("admin".equalsIgnoreCase(rol)) {
            intent = new Intent(MainActivity.this, MenuAdminActivity.class);
        } else {
            // Por defecto todos los demás usuarios son clientes
            intent = new Intent(MainActivity.this, MenuClienteActivity.class);
        }

        startActivity(intent);
        finish();
    }

    // ********************* Recuperación de contraseña ***********************************
    private void resetearPassword(String email) {
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarProgressDialog("Enviando enlace de recuperación...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    ocultarProgressDialog();
                    if (task.isSuccessful()) {
                        showResetSuccessDialog(email);
                    } else {
                        handlePasswordResetError(task.getException());
                    }
                });
    }

    private void showResetSuccessDialog(String email) {
        new AlertDialog.Builder(this)
                .setTitle("Enlace enviado")
                .setMessage(String.format("Hemos enviado un enlace para restablecer tu contraseña a %s. Por favor revisa tu bandeja de entrada.", email))
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void handlePasswordResetError(Exception exception) {
        String errorMessage = "Ocurrió un error al enviar el email";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "No existe una cuenta con este email";
        } else if (exception instanceof FirebaseTooManyRequestsException) {
            errorMessage = "Demasiados intentos. Por favor inténtalo más tarde";
        } else if (exception instanceof FirebaseNetworkException) {
            errorMessage = "Error de conexión a internet";
        } else if (exception != null) {
            errorMessage = exception.getMessage();
        }

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("Entendido", null)
                .show();
    }

    // ********************* Métodos de utilidad ***********************************
    private void mostrarProgressDialog(String mensaje) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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
    protected void onDestroy() {
        super.onDestroy();
        ocultarProgressDialog();
    }

    // ********************* Métodos públicos (si son necesarios) ***********************************
    public void OpenUsers(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}
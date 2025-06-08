package com.example.parking_ues;

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

import com.example.parking_ues.Models.User;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "GoogleSignIn";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText etEmail, etPassword;
    private Button btnGoogleSignIn;
    private TextView ForgotPassword;
    private TextView ResgisterUsers;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etEmail = findViewById(R.id.txtCorreo);
        etPassword = findViewById(R.id.txtPassword);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        ForgotPassword = findViewById(R.id.lblOlvidePassword);
        ResgisterUsers = findViewById(R.id.lblRgisterUsers);

        ForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (!email.isEmpty()) {
                resetearPassword(email);
            }
        });

        ResgisterUsers.setOnClickListener(v -> registrarUsuario());

        Button btnLogin = findViewById(R.id.btnIniciarSesion);
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validarCampos(email, password)) {
                iniciarSesion(email, password);
            }
        });

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }
//********************* Inicio de seccion por correo y contraseña *********************************/
    private boolean validarCampos(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Email requerido");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Contraseña requerida");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    public void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            startActivity(new Intent(MainActivity.this, MenuAdminActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Por favor verifica tu email primero.",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error en inicio de sesión: " +
                                        Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    //********************* Inicio de seccion por Google *********************************/

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
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Si falla el inicio de sesión
                            Toast.makeText(MainActivity.this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, MenuAdminActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //registrar nuevo usuario
    public void registrarUsuario() {
        Toast.makeText(MainActivity.this, "Abre la activity para registrar nuevo user.",Toast.LENGTH_SHORT).show();
    }


    /*cambio de contraseña*/
        public void resetearPassword(String email) {
            if (!isValidEmail(email)) {
                Toast.makeText(MainActivity.this,
                        "Por favor ingresa un email válido",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Enviando enlace de recuperación...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            showResetSuccessDialog(email);
                        } else {
                            handlePasswordResetError(task.getException());
                        }
                    });
        }
        private boolean isValidEmail(String email) {
            return !TextUtils.isEmpty(email) &&
                    Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        private void showResetSuccessDialog(String email) {
            new AlertDialog.Builder(MainActivity.this)
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

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("Entendido", null)
                    .show();
        }

    public void OpenUsers(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    public void iniciarSeccion(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Autenticación fallida: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void iniciarSeccion(View view) {
        Intent intent = new Intent(this, MenuAdminActivity.class);
        startActivity(intent);
    }
}
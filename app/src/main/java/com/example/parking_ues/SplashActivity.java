package com.example.parking_ues;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgCarritoCorriendo;
    private ImageView imgHumoCarrito;
    private View sombraCarrito;
    private ProgressBar barraProgreso;
    private TextView lblTextoCarga;
    private TextView lblPorcentaje;
    private View[] lineasCarretera;

    // Textos de carga dinámicos
    private String[] textosCarga = {
            "Arrancando motores...",
            "Buscando espacios disponibles...",
            "Conectando con servidores...",
            "Cargando mapas...",
            "Casi listo para aparcar..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarVistas();
        iniciarAnimaciones();
    }

    private void inicializarVistas() {
        imgCarritoCorriendo = findViewById(R.id.img_carrito_corriendo);
        imgHumoCarrito = findViewById(R.id.img_humo_carrito);
        //sombraCarrito = findViewById(R.id.sombra_carrito);
        barraProgreso = findViewById(R.id.barra_progreso);
        lblTextoCarga = findViewById(R.id.lbl_texto_carga);
        lblPorcentaje = findViewById(R.id.lbl_porcentaje);

        // Inicializar líneas de carretera para animación
        lineasCarretera = new View[4];
        lineasCarretera[0] = findViewById(R.id.linea_carretera_1);
        lineasCarretera[1] = findViewById(R.id.linea_carretera_2);
        lineasCarretera[2] = findViewById(R.id.linea_carretera_3);
        lineasCarretera[3] = findViewById(R.id.linea_carretera_4);
    }

    private void iniciarAnimaciones() {
        // Animación del carrito moviéndose de izquierda a derecha
        animarMovimientoCarrito();

        // Animación de las líneas de carretera (efecto de movimiento)
        animarLineasCarretera();

        // Animación del humo del escape
        animarEfectoHumo();

        // Animación de la barra de progreso con textos dinámicos
        animarBarraProgreso();
    }

    private void animarMovimientoCarrito() {
        // El carrito se mueve 250dp hacia la derecha durante 4 segundos
        ObjectAnimator movimientoCarrito = ObjectAnimator.ofFloat(imgCarritoCorriendo, "translationX", 0f, 250f);
        movimientoCarrito.setDuration(4000);
        movimientoCarrito.setInterpolator(new LinearInterpolator());

        // La sombra sigue al carrito
        ObjectAnimator movimientoSombra = ObjectAnimator.ofFloat(sombraCarrito, "translationX", 0f, 250f);
        movimientoSombra.setDuration(4000);
        movimientoSombra.setInterpolator(new LinearInterpolator());

        // El humo también sigue al carrito
        ObjectAnimator movimientoHumo = ObjectAnimator.ofFloat(imgHumoCarrito, "translationX", 0f, 250f);
        movimientoHumo.setDuration(4000);
        movimientoHumo.setInterpolator(new LinearInterpolator());

        // Pequeña animación de rebote en el carrito (simula el movimiento del vehículo)
        ObjectAnimator reboteCarrito = ObjectAnimator.ofFloat(imgCarritoCorriendo, "translationY", 0f, 3f, 0f, -2f, 0f);
        reboteCarrito.setDuration(800);
        reboteCarrito.setRepeatCount(ObjectAnimator.INFINITE);
        reboteCarrito.setRepeatMode(ObjectAnimator.RESTART);
        reboteCarrito.setInterpolator(new AccelerateDecelerateInterpolator());

        // Iniciar animaciones
        movimientoCarrito.start();
        movimientoSombra.start();
        movimientoHumo.start();
        reboteCarrito.start();
    }

    private void animarLineasCarretera() {
        // Las líneas de carretera se mueven hacia la izquierda para dar sensación de velocidad
        for (int i = 0; i < lineasCarretera.length; i++) {
            ObjectAnimator animacionLinea = ObjectAnimator.ofFloat(lineasCarretera[i], "translationX", 0f, -80f);
            animacionLinea.setDuration(1000);
            animacionLinea.setRepeatCount(ObjectAnimator.INFINITE);
            animacionLinea.setRepeatMode(ObjectAnimator.RESTART);
            animacionLinea.setInterpolator(new LinearInterpolator());
            animacionLinea.setStartDelay(i * 200); // Delay escalonado para efecto fluido
            animacionLinea.start();
        }
    }

    private void animarEfectoHumo() {
        // El humo aparece y desaparece
        ObjectAnimator alphaHumo = ObjectAnimator.ofFloat(imgHumoCarrito, "alpha", 0.6f, 0.2f, 0.6f);
        alphaHumo.setDuration(500);
        alphaHumo.setRepeatCount(ObjectAnimator.INFINITE);
        alphaHumo.setRepeatMode(ObjectAnimator.REVERSE);

        // El humo también se escala ligeramente
        ObjectAnimator escalaHumo = ObjectAnimator.ofFloat(imgHumoCarrito, "scaleX", 1f, 1.2f, 1f);
        escalaHumo.setDuration(800);
        escalaHumo.setRepeatCount(ObjectAnimator.INFINITE);
        escalaHumo.setRepeatMode(ObjectAnimator.REVERSE);

        alphaHumo.start();
        escalaHumo.start();
    }

    private void animarBarraProgreso() {
        ValueAnimator animadorProgreso = ValueAnimator.ofInt(0, 100);
        animadorProgreso.setDuration(4000);
        animadorProgreso.setInterpolator(new AccelerateDecelerateInterpolator());

        animadorProgreso.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progreso = (int) animation.getAnimatedValue();
                barraProgreso.setProgress(progreso);
                lblPorcentaje.setText(progreso + "%");

                // Cambiar texto según el progreso
                actualizarTextoCarga(progreso);
            }
        });

        animadorProgreso.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Pequeño delay antes de ir a la siguiente activity
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        irAActividadPrincipal();
                    }
                }, 500);
            }
        });

        animadorProgreso.start();
    }

    private void actualizarTextoCarga(int progreso) {
        final String textoActual; // Hacer la variable final

        if (progreso < 20) {
            textoActual = textosCarga[0];
        } else if (progreso < 40) {
            textoActual = textosCarga[1];
        } else if (progreso < 60) {
            textoActual = textosCarga[2];
        } else if (progreso < 80) {
            textoActual = textosCarga[3];
        } else {
            textoActual = textosCarga[4];
        }

        if (!lblTextoCarga.getText().toString().equals(textoActual)) {
            // Animación de cambio de texto suave
            lblTextoCarga.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            lblTextoCarga.setText(textoActual);
                            lblTextoCarga.animate()
                                    .alpha(0.85f)
                                    .setDuration(200)
                                    .start();
                        }
                    })
                    .start();
        }
    }

    private void irAActividadPrincipal() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);

        // Transición suave entre activities
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
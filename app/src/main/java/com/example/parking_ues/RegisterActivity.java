package com.example.parking_ues;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parking_ues.Activities.MainActivity;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private Spinner spRol;
    private TextView lblIniciarSesion;

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
        lblIniciarSesion = findViewById(R.id.lblIniciarSesion);

        lblIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        configurarSpinners();
    }


    private void configurarSpinners() {
        List<String> rol = Arrays.asList("Admin", "Cliente");
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(this, R.layout.spinner_personalizado, rol);
        rolAdapter.setDropDownViewResource(R.layout.spinner_personalizado);
        spRol.setAdapter(rolAdapter);
    }

}
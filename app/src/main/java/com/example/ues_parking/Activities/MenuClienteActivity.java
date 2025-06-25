package com.example.ues_parking.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.ues_parking.Fragments.PerfilClienteFragment;
import com.example.ues_parking.Fragments.TiempoEstacionadoFragment;
import com.example.ues_parking.Fragments.VerEstacionamientosFragment;

import com.example.ues_parking.RegistroCode;
import com.example.ues_parking.Services.DatabaseSeederService;
import com.example.ues_parking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuClienteActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        DatabaseSeederService seeder = new DatabaseSeederService();
        seeder.seedAllData();

        //ASOCIARL ELEMENTOS
        bottomNavigationView = findViewById(R.id.bottomnavigation);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

        //LOGICA DEL BTN NAVIGATION
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navEspaciosDisponiblesYTarifas) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new VerEstacionamientosFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.navTimepoEstacionado) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new TiempoEstacionadoFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.navPerfil) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new PerfilClienteFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.navRegistroCode) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new RegistroCode())
                        .commit();
                return true;
            }
            return false;
        });
    }
}
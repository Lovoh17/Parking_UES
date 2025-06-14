package com.example.parking_ues.Activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.parking_ues.Fragments.DashboardFragment;
import com.example.parking_ues.Fragments.GestionUsuariosFragment;
import com.example.parking_ues.Fragments.GestionarEstacionamientoFragment;
import com.example.parking_ues.Fragments.PerfilAdminFragment;
import com.example.parking_ues.R;
import com.example.parking_ues.Fragments.ReportesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuAdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentContainerView fragmentContainerView;
    private Toolbar toolbarAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        bottomNavigationView = findViewById(R.id.bottomnavigation);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        toolbarAdmin = findViewById(R.id.toolbar);

        //LOGICA DEL TOOLBAR
        toolbarAdmin.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navPerfilAdmin:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new PerfilAdminFragment()).commit();

                        break;
                    case R.id.navCerrarSesion: //  caso para cerrar sesion
                       // mostrarDialogoCerrarSesion();
                        break;

                    case R.id.navSalir: // Opcional: salir de la app sin cerrar sesion
                        //mostrarDialogoSalirApp();
                        break;

                    default:
                        Log.w(TAG, "Opcion de menu no reconocida: " + item.getItemId());
                        return false;
                }
                return true;
            }
        });
        //LOGICA DEL BTNNAVIGATION
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navDashboardAdministrativo) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new DashboardFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.navGestionEstaconamiento) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new GestionarEstacionamientoFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.navGestionarUsuarios) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new GestionUsuariosFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.navReportesEstafisticas) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new ReportesFragment())
                        .commit();
                return true;
            }

            return false;
        });
    }

    //METODO MOSTAR DIALOGO PARA SERRAR SECION
    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrarSesion(); //METODO CERRAR SECION
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    //LOGICA PARA SERRar AECCION
    private void cerrarSesion() {
        try {
            Log.d("TAG", "Iniciando proceso de cierre de sesión");
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show(); // También aquí

        } catch (Exception e) {
            Log.e("TAG", "Error cerrando sesión", e);
            Toast.makeText(this, "Error cerrando sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
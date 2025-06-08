package com.example.parking_ues.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking_ues.Dialogs.UsuariosDialog;
import com.example.parking_ues.R;

public class GestionarUsuariosAdapter extends RecyclerView.Adapter<GestionarUsuariosAdapter.ViewHolderGestionarUsuariosAdapter> {
    public Context context;
    private FragmentManager fragmentManager;

    public GestionarUsuariosAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public GestionarUsuariosAdapter.ViewHolderGestionarUsuariosAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuarios, parent, false);
        return new ViewHolderGestionarUsuariosAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GestionarUsuariosAdapter.ViewHolderGestionarUsuariosAdapter holder, int position) {
        //DATOS DE PRIEBA PARA EL DISNIO
        holder.lblNombreCompleto.setText("Yoselin Joya");
        holder.lblRol.setText("Rol: Administrador");
        holder.lblCorreo.setText("joyayoselin10@mail.com");
        holder.lblFechaCreacion.setText("Creado: 2024-01-01 12:00");
        holder.lblUltimoAcceso.setText("Último acceso: 2025-05-24 08:30");

        holder.lblNombreCompleto.setText("Lino Lovo");
        holder.lblRol.setText("Rol: Administrador");
        holder.lblCorreo.setText("joyayoselin10@mail.com");
        holder.lblFechaCreacion.setText("Creado: 2024-01-01 12:00");
        holder.lblUltimoAcceso.setText("Último acceso: 2025-05-24 08:30");

        holder.lblNombreCompleto.setText("Nombre Apellido");
        holder.lblRol.setText("Rol: Administrador");
        holder.lblCorreo.setText("joyayoselin10@mail.com");
        holder.lblFechaCreacion.setText("Creado: 2024-01-01 12:00");
        holder.lblUltimoAcceso.setText("Último acceso: 2025-05-24 08:30");

        //EVENTO DEL BOTON EDITARAdd commentMore actions
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsuariosDialog usuariosDialog = new UsuariosDialog();
                usuariosDialog.show(fragmentManager, "editar");
            }
        });

        //EVENTO DEL BOTON ELIMINAR (PERO SOLO EL DIALOGO)
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("¿Eliminar Usuario?")
                    .setMessage("¿Estás seguro de que deseas eliminar este Usuario ")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        Toast.makeText(context, "Elimindo", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolderGestionarUsuariosAdapter extends RecyclerView.ViewHolder {
        ImageView imgUsuario;
        TextView lblNombreCompleto, lblRol, lblCorreo, lblFechaCreacion, lblUltimoAcceso;
        ImageButton btnEditar, btnEliminar;
        public ViewHolderGestionarUsuariosAdapter(@NonNull View itemView) {
            super(itemView);
            lblNombreCompleto = itemView.findViewById(R.id.lblNombreCompleto);
            lblRol = itemView.findViewById(R.id.lblRol);
            lblCorreo = itemView.findViewById(R.id.lblCorreo);
            lblFechaCreacion = itemView.findViewById(R.id.lblFechaDeCreacionUusuario);
            lblUltimoAcceso = itemView.findViewById(R.id.lblUltimoAcceso);
            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
            btnEliminar = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}

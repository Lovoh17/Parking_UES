package com.example.parking_ues.Models;

public class User {
    private String email;
    private String nombre;
    private long fechaRegistro;

    // Constructor vacío necesario para Firebase
    public User() {}

    public User(String email, String nombre, long fechaRegistro) {
        this.email = email;
        this.nombre = nombre;
        this.fechaRegistro = fechaRegistro;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public long getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(long fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}

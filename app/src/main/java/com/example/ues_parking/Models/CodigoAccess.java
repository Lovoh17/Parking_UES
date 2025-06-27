package com.example.ues_parking.Models;

public class CodigoAccess {
    private String codigo;
    private String status;
    private String user_id;

    // Constructor vacío requerido para Firebase
    public CodigoAccess() {}

    public CodigoAccess(String codigo, String pendiente, String uid, long l) {
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
package com.example.ues_parking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;


public class RegistroCode extends Fragment {
    private EditText txtRegistroCode;
    private MaterialButton btnRgistrarCode;
    private TextView lblMensajeInformativo;


    public RegistroCode() {
        // Required empty public constructor
    }

    public static RegistroCode newInstance(String param1, String param2) {
        RegistroCode fragment = new RegistroCode();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_code, container, false);
        AsociarlElmetosXML(view);
        return view;
    }

    private void AsociarlElmetosXML(View view){
        txtRegistroCode = view.findViewById(R.id.txtRegistroCode);
        btnRgistrarCode = view.findViewById(R.id.btnRgistrarCode);
        lblMensajeInformativo = view.findViewById(R.id.lblMensajeInformativo);
    }
}
package com.example.parking_ues;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class GestionUsuariosFragment extends Fragment {



    public GestionUsuariosFragment() {
        // Required empty public constructor
    }


    public static GestionUsuariosFragment newInstance(String param1, String param2) {
        GestionUsuariosFragment fragment = new GestionUsuariosFragment();
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
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);
        return view;
    }
}
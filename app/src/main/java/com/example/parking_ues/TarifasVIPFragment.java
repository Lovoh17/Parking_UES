package com.example.parking_ues;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TarifasVIPFragment extends Fragment {

    public TarifasVIPFragment() {
        // Required empty public constructor
    }

    public static TarifasVIPFragment newInstance(String param1, String param2) {
        TarifasVIPFragment fragment = new TarifasVIPFragment();
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
        View view = inflater.inflate(R.layout.fragment_tarifas_v_i_p, container, false);
        return view;
    }
}
package com.example.coolioevents.Entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coolioevents.R;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantHomeFragment extends Fragment implements Observer {



    public EntrantHomeFragment() {
        // Required empty public constructor
    }



    public static EntrantHomeFragment newInstance(String param1, String param2) {
        EntrantHomeFragment fragment = new EntrantHomeFragment();
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
        return inflater.inflate(R.layout.fragment_entrant_home, container, false);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
package com.example.julian.clubculturallima;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class DetailActivityFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    TextView nombre,fecha,capacidad;
    RatingBar puntuacion;


    public DetailActivityFragment() {
        // Required empty public constructor
    }

    public static DetailActivityFragment newInstance(String param1) {
        DetailActivityFragment fragment = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putString("datos", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("datos");
            //Toast.makeText(getActivity(), mParam1, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_detail_activity, container, false);
        nombre=(TextView)v.findViewById(R.id.detail_nombre);
        fecha=(TextView)v.findViewById(R.id.detail_fecha);
        capacidad=(TextView)v.findViewById(R.id.detail_capacidad);
        puntuacion=(RatingBar)v.findViewById(R.id.ratingBar);
        JSONParser p=new JSONParser();
        try {
            JSONObject o=(JSONObject)p.parse(mParam1);
            nombre.setText(o.get("nombre").toString());
            fecha.setText(o.get("fecha").toString());
            capacidad.setText(o.get("capacidad").toString());
            puntuacion.setNumStars(5);
            puntuacion.setMax(5);
            puntuacion.setActivated(false);
            puntuacion.setFocusable(false);
            puntuacion.setClickable(false);
            puntuacion.setFocusableInTouchMode(false);
            puntuacion.setRating(Float.parseFloat(o.get("puntaje").toString()));
            puntuacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    Toast.makeText(getActivity(),"Puntuacion: "+v, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return v;
    }
}

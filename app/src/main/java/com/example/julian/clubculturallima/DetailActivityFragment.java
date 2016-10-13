package com.example.julian.clubculturallima;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.julian.clubculturallima.Utils.SessionManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;


public class DetailActivityFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    TextView nombre, fecha, capacidad;
    RatingBar puntuacion;
    SessionManager session;
    String u;


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
        session=new SessionManager(getActivity());
        HashMap<String,String> user=session.getUserDetails();
        u=user.get(SessionManager.KEY_EMAIL);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("datos");
            //Toast.makeText(getActivity(), mParam1, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        nombre = (TextView) v.findViewById(R.id.detail_nombre);
        fecha = (TextView) v.findViewById(R.id.detail_fecha);
        capacidad = (TextView) v.findViewById(R.id.detail_capacidad);
        puntuacion = (RatingBar) v.findViewById(R.id.ratingBar);
        JSONParser p = new JSONParser();

        try {
            final JSONObject o = (JSONObject) p.parse(mParam1);
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
                public void onRatingChanged(RatingBar ratingBar,final float v, boolean b) {
                    //Toast.makeText(getActivity(), "Puntuacion: " + v, Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Puntuación Registrada: "+v)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    puntuar(u,String.valueOf(v),o.get("id").toString());
                                }
                            })
                            .setIcon(android.R.drawable.alert_dark_frame)
                            .show();

                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return v;
    }

    public void puntuar(final String u, final String p,final String idAct) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-service.herokuapp.com/puntuar";
        String url2 = "http://192.168.1.13:8080/Tesis_SQL/puntuar";
        String url3="http://54.227.36.192:8080/Tesis_SQL/puntuar";

        // Request a string response from the provided URL.

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("true")) {
                            Toast.makeText(getActivity(), "Puntuación registrada", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", u);
                params.put("punt", p);
                params.put("idAct",idAct);

                return params;
            }
        };
        queue.add(postRequest);
    }

}


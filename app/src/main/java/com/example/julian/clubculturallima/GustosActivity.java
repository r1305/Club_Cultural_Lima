package com.example.julian.clubculturallima;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.julian.clubculturallima.Utils.SessionManager;

import org.json.simple.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class GustosActivity extends AppCompatActivity {


    CheckBox futbol, voley, tenis, basket, fulbito, natacion, fronton, gimnasia, niños, adultos;
    Button btn_gustos;
    JSONArray l;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gustos);

        session = new SessionManager(this);
        HashMap<String,String> user=session.getUserDetails();

        futbol = (CheckBox) findViewById(R.id.futbol);
        voley = (CheckBox) findViewById(R.id.voley);
        tenis = (CheckBox) findViewById(R.id.tenis);
        basket = (CheckBox) findViewById(R.id.basket);
        fulbito = (CheckBox) findViewById(R.id.fulbito);
        natacion = (CheckBox) findViewById(R.id.natacion);
        fronton = (CheckBox) findViewById(R.id.fronton);
        gimnasia = (CheckBox) findViewById(R.id.gimnasia);
        niños = (CheckBox) findViewById(R.id.niños);
        adultos = (CheckBox) findViewById(R.id.adultos);

        btn_gustos = (Button) findViewById(R.id.btn_gustos);

        btn_gustos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 l= new JSONArray();
                if (futbol.isChecked()) {
                    l.add(futbol.getText().toString());
                }
                if (voley.isChecked()) {
                    l.add(voley.getText().toString());
                }
                if (tenis.isChecked()) {
                    l.add(tenis.getText().toString());
                }
                if (basket.isChecked()) {
                    l.add(basket.getText().toString());
                }
                if (fulbito.isChecked()) {
                    l.add(fulbito.getText().toString());
                }
                if (natacion.isChecked()) {
                    l.add(natacion.getText().toString());
                }
                if (fronton.isChecked()) {
                    l.add(fronton.getText().toString());
                }
                if (gimnasia.isChecked()) {
                    l.add(gimnasia.getText().toString());
                }
                if (niños.isChecked()) {
                    l.add(niños.getText().toString());
                }
                if (adultos.isChecked()) {
                    l.add(adultos.getText().toString());
                }
                HashMap<String,String> user=session.getUserDetails();
                String c=user.get(SessionManager.KEY_EMAIL);
                guardarGustos(c);

            }
        });

    }

    public void guardarGustos(final String correo) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://tesis-service.herokuapp.com/gustos";
        String url2 = "http://192.168.43.33:8080/Tesis_SQL/gustos";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.toString().equals("ok")) {
                            Toast.makeText(GustosActivity.this, "Registro correcto", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(GustosActivity.this, MainActivity.class);
                            startActivity(i);
                            GustosActivity.this.finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(GustosActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("gustos",l.toString());
                return params;
            }
        };
        queue.add(postRequest);
    }
}

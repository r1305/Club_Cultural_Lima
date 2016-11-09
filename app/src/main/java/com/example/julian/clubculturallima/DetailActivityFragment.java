package com.example.julian.clubculturallima;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class DetailActivityFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    TextView nombre, fecha, capacidad;
    RatingBar puntuacion;
    SessionManager session;
    Button asistir,sms;
    String u;
    String idAct;
    String evento;
    String dia;
    private static final int RESULT_PICK_CONTACT = 1;


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
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        u = user.get(SessionManager.KEY_EMAIL);
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
        sms=(Button)v.findViewById(R.id.btn_sms);
        nombre = (TextView) v.findViewById(R.id.detail_nombre);
        fecha = (TextView) v.findViewById(R.id.detail_fecha);
        capacidad = (TextView) v.findViewById(R.id.detail_capacidad);
        puntuacion = (RatingBar) v.findViewById(R.id.ratingBar);
        asistir=(Button)v.findViewById(R.id.btn_asistir);
        JSONParser p = new JSONParser();

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });



        try {
            final JSONObject o = (JSONObject) p.parse(mParam1);
            idAct=o.get("id").toString();
            evento=o.get("nombre").toString();
            nombre.setText(o.get("nombre").toString());
            dia=o.get("fecha").toString();
            fecha.setText(o.get("fecha").toString());
            capacidad.setText(o.get("capacidad").toString());
            puntuacion.setNumStars(5);
            puntuacion.setMax(5);
            puntuacion.setRating(Float.parseFloat(o.get("puntaje").toString()));
            puntuacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                    //Toast.makeText(getActivity(), "Puntuacion: " + v, Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Puntuación Registrada: " + v)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    puntuar(u, String.valueOf(v), o.get("id").toString());
                                }
                            })
                            .setIcon(android.R.drawable.alert_dark_frame)
                            .show();

                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

        asistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asistir(u,idAct);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Cursor cursor = null;
                    try {
                        String phoneNo = null ;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int n=cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        phoneNo = cursor.getString(phoneIndex);
                        name=cursor.getString(n);
                        cursor.close();
                        String[] name2=name.split(" ");
                        Toast.makeText(getActivity(),"Mensaje enviado", Toast.LENGTH_LONG).show();
                        String cadena="¡Ven y participa del evento "+evento+" a realizarse el "+dia+"!";
                        sendSMS(phoneNo, cadena);
//                        System.out.println(name2[0]+"! Ven y participa del evento "+evento
//                                +" a realizarse el "+dia+". Te esperamos!");
                        //textView2.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    String rpta;

    public AlertDialog createRadioListDialog(final String user, final String act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] items = new CharSequence[4];

        items[0] = "Mucho";
        items[1] = "Poco";
        items[2] = "Casi nada";
        items[3] = "Nada";

        rpta=items[0].toString();
        builder.setTitle("¿Le gustó la recomendación?")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(
//                                getActivity(),
//                                "Seleccionaste: " + items[which],
//                                Toast.LENGTH_SHORT)
//                                .show();
                        rpta = items[which].toString();

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        estad(user, act, rpta);
                        Toast.makeText(getActivity(), "¡Gracias!", Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }

    public void puntuar(final String u, final String p, final String idAct) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-service.herokuapp.com/puntuar";
        String url2 = "http://192.168.1.14:8080/Tesis_SQL/puntuar";
        String url3 = "http://54.227.36.192:8080/Tesis_SQL/puntuar";

        // Request a string response from the provided URL.

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("true")) {
                            createRadioListDialog(u, idAct).show();
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
                params.put("idAct", idAct);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void estad(final String u, final String idAct, final String rpta) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-service.herokuapp.com/estad";
        String url2 = "http://192.168.1.14:8080/Tesis_SQL/estad";
        String url3 = "http://54.227.36.192:8080/Tesis_SQL/estad";

        // Request a string response from the provided URL.

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response

//                            Toast.makeText(getActivity(), "Puntuación registrada", Toast.LENGTH_SHORT).show();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment reco=RecoFragment.newInstance();
                            ft.replace(R.id.flaContenido,reco);
                            ft.commit();
                            //showRadioButtonDialog();
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
                params.put("idAct", idAct);
                params.put("rpta", rpta);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void asistir(final String u, final String idAct) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "https://tesis-service.herokuapp.com/Asistencia";
        String url2 = "http://192.168.1.14:8080/Tesis_SQL/Asistencia";
        String url3 = "http://54.227.36.192:8080/Tesis_SQL/Asistencia";

        // Request a string response from the provided URL.

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response

                        Toast.makeText(getActivity(), "Asistencia registrada", Toast.LENGTH_SHORT).show();
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
                params.put("user", u);
                params.put("idAct", idAct);

                return params;
            }
        };
        queue.add(postRequest);
    }

}


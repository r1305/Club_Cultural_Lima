package com.example.julian.clubculturallima;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.julian.clubculturallima.Utils.SessionManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivitiesFragment extends Fragment{

    String idU="";
    ProgressDialog pDialog;
    SessionManager session;
    SearchView search;
    ImageView voice;

    RecyclerView activity;
    ActivityRecyclerAdapter adapter;
    List<JSONObject> l=new ArrayList<>();

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ActivitiesFragment newInstance() {
        ActivitiesFragment fragment = new ActivitiesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session=new SessionManager(getActivity().getApplicationContext());
        HashMap<String,String> datos=session.getUserDetails();
        idU=datos.get(SessionManager.KEY_EMAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_activities, container, false);
        activity=(RecyclerView)view.findViewById(R.id.recycler_view_activities);
        activity.setLayoutManager(new LinearLayoutManager(getActivity()));
        search=(SearchView)view.findViewById(R.id.search_activities);
        voice=(ImageView)view.findViewById(R.id.search_voice); 
        adapter=new ActivityRecyclerAdapter(l);

        activity.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject o=(JSONObject)view.getTag();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                Fragment activity=DetailActivityFragment.newInstance(o.toString());
                ft.replace(R.id.flaContenido,activity);
                ft.commit();
            }
        });

        search.setSubmitButtonEnabled(true);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                pDialog = new ProgressDialog(getActivity());
                String message = "Buscando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(true);
                pDialog.show();
                searchActivities(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(getActivity(), "TextChange", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Grabando voz", Toast.LENGTH_SHORT).show();
            }
        });

        new DoInBackGround().execute();

        return view;
    }


    public class DoInBackGround extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getActivities();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            String message = "Cargando...";

            SpannableString ss2 = new SpannableString(message);
            ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

            pDialog.setMessage(ss2);

            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getActivities() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://tesis-service.herokuapp.com/getActivities";
        String url2 = "http://192.168.1.13:8080/TesisWs/getActivities";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("***** " + response);
                        JSONParser jp = new JSONParser();
                        JSONObject obj;
                        try {
                            obj = (JSONObject) jp.parse(response);
                            JSONArray ja = (JSONArray) obj.get("act");
                            for(int i=0;i<ja.size();i++){
                                l.add((JSONObject)ja.get(i));
                            }
                            adapter.notifyDataSetChanged();

                            pDialog.dismiss();

                        } catch (ParseException e) {
                            Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void searchActivities(final String search) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://tesis-service.herokuapp.com/searchActivity";
        String url2 = "http://192.168.1.15:8080/TesisWs/searchActivity";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("***** " + response);
                        JSONParser jp = new JSONParser();
                        JSONObject obj;

                        try {
                            obj = (JSONObject) jp.parse(response);
                            JSONArray ja = (JSONArray) obj.get("act");
                            l.clear();
                            for(int i=0;i<ja.size();i++){
                                l.add((JSONObject)ja.get(i));
                            }

                            adapter.notifyDataSetChanged();

                            pDialog.dismiss();

                        } catch (ParseException e) {
                            Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("search", search);

                return params;
            }
        };
        queue.add(postRequest);
    }

}

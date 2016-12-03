package com.example.julian.clubculturallima;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.julian.clubculturallima.Utils.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //@Bind(R.id.drawer_layout)
    DrawerLayout dl;
    //@Bind(R.id.toolbar)
    Toolbar toolbar;
    //@Bind(R.id.navigation)
    NavigationView nav;
    //@Bind(R.id.txt_nav_header)
    TextView txt_nav;
    //@Bind(R.id.profile)
    CircleImageView img;
    SessionManager session;
    String datos;
    CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facebookSDKInitialize();
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        nav=(NavigationView)findViewById(R.id.navigation);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);
        View v=nav.getHeaderView(0);
        txt_nav=(TextView)v.findViewById(R.id.txt_nav_header);
        img=(CircleImageView)v.findViewById(R.id.profile);

        /* Validar si existe sesión*/
        session = new SessionManager(this);
        session.checkLogin();
        if(session.isLoggedIn()){
            HashMap<String,String> user=session.getUserDetails();
            getDatos(user.get(SessionManager.KEY_EMAIL));
            Fragment frag1 = RecoFragment.newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.flaContenido, frag1);
            ft.commit();
        }

        //toolbar.setNavigationIcon(R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.animate();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl.openDrawer(GravityCompat.START);
                if (dl.isDrawerOpen(GravityCompat.START)) {
                    dl.closeDrawers();
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,dl,toolbar,R.string.openDrawer,
                R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        dl.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        nav.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch(item.getItemId()){
            case R.id.logout:
                MainActivity.this.finish();
                session.logoutUser();
                LoginManager.getInstance().logOut();

                return true;
            case R.id.reco:
                Fragment reco=RecoFragment.newInstance();
                ft.replace(R.id.flaContenido,reco);
                toolbar.setTitle("Recomendaciones");
                ft.commit();
                dl.closeDrawers();
//                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.perfil:
                Fragment perfil=ProfileFragment.newInstance(datos);
                ft.replace(R.id.flaContenido,perfil);
                toolbar.setTitle("Perfil");
                ft.commit();
                dl.closeDrawers();
//                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.act:
                Fragment libros=ActivitiesFragment.newInstance();
                ft.replace(R.id.flaContenido,libros);
                toolbar.setTitle("Actividades");
                ft.commit();
                dl.closeDrawers();
//                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;

//            case R.id.sms:
//                //Fragment libros=ActivitiesFragment.newInstance();
//                //ft.replace(R.id.flaContenido,libros);
//                //toolbar.setTitle("Actividades");
//                //ft.commit();
//                //sendSMS("980858922", "Enviando SMS de prueba para tesis");
//                sendEmail();
//                dl.closeDrawers();
////                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
        }
        return false;
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

//    private void sendSMS(String phoneNumber, String message) {
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, null, null);
//    }
//
//    private void sendEmail(){
//
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//        emailIntent.setData(Uri.parse("mailto:" + "20111403@aloe.ulima.edu.pe"));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Probando");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "My email's body");
//
//        try {
//            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(MainActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    public void getDatos(final String u) {
        final RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://tesis-service.herokuapp.com/Usuario";
        String url2="http://192.168.1.15:8080/Tesis_SQL/Usuario";

        // Request a string response from the provided URL.
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //System.out.println("***** "+response+" ****");
                        JSONParser p=new JSONParser();
                        try{
                            JSONObject o=(JSONObject)p.parse(response);
                            datos=o.toString();
                            txt_nav.setText(o.get("nombre").toString());
                            Picasso.with(MainActivity.this).load(o.get("foto").toString()).into(img);
                        }catch (Exception e){
                            System.out.println("error: "+e);
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

                return params;
            }
        };
        queue.add(postRequest);
    }
}

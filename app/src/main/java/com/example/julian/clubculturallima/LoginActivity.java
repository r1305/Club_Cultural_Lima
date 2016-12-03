package com.example.julian.clubculturallima;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.julian.clubculturallima.Utils.SessionManager;
import com.example.julian.clubculturallima.Utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    EditText user, clave;
    Button login, signup;
    SessionManager session;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_login);

        session=new SessionManager(this);

        user = (EditText) findViewById(R.id.usuario);
        clave = (EditText) findViewById(R.id.clave);
        login = (Button) findViewById(R.id.btn_login);
        signup = (Button) findViewById(R.id.btn_sign_up);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(user.getText().toString(),clave.getText().toString());
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }
        });

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile,email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        new Validate().execute();
    }

    private void getFbData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            Utils.user.setEmail(object.getString("email"));
                            pDialog = new ProgressDialog(LoginActivity.this);
                            String message = "Cargando...";

                            SpannableString ss2 = new SpannableString(message);
                            ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                            pDialog.setMessage(ss2);

                            pDialog.setCancelable(true);
                            pDialog.show();
                            validar(Utils.user.getEmail());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public void validar(final String u) {
        final RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://tesis-service.herokuapp.com/validar";
        String url2="http://192.168.1.15:8080/Tesis_SQL/validar";

        // Request a string response from the provided URL.

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        pDialog.dismiss();
                        System.out.println("***** "+response+" ****");
                        if (response.equalsIgnoreCase("fail")) {

                            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                            startActivity(i);
                            LoginActivity.this.finishAffinity();
                        }else{

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            session.createLoginSession(u);
                            startActivity(i);
                            LoginActivity.this.finishAffinity();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pDialog.dismiss();
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                15,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public class Validate extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getFbData();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public void login(final String u, final String p) {
        final RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://tesis-service.herokuapp.com/login";
        String url2="http://192.168.1.15:8080/Tesis_SQL/login";


        // Request a string response from the provided URL.

        final StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.equals("error")) {
                            Toast.makeText(LoginActivity.this, "¡Usuario o contraseña incorrecta!", Toast.LENGTH_SHORT).show();
                        } else {
                            session.createLoginSession(response);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("correo", response);
                            startActivity(i);
                            LoginActivity.this.finishAffinity();
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
                params.put("psw", p);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                15,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}

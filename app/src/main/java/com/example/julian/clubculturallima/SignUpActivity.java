package com.example.julian.clubculturallima;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.julian.clubculturallima.Utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText nombre, edad, email, clave,codigo;
    Button signup,val;
    ImageView img;
    String urlImg;
    int PICK_IMAGE = 200;
    Context c = this;
    SessionManager session;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nombre = (EditText) findViewById(R.id.sign_nombre);
        edad = (EditText) findViewById(R.id.sign_edad);
        email = (EditText) findViewById(R.id.sign_correo);
        clave = (EditText) findViewById(R.id.sign_psw);
        signup = (Button) findViewById(R.id.sign_btn);
        img = (ImageView) findViewById(R.id.sign_img);
        codigo=(EditText)findViewById(R.id.sign_codigo);
        val=(Button)findViewById(R.id.sign_btn_validar);

        session = new SessionManager(c);
        img.setVisibility(View.GONE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        signup.setVisibility(View.GONE);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(SignUpActivity.this);

                String message = "Espere un momento...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(false);
                pDialog.show();
                validar(codigo.getText().toString());
                signup(nombre.getText().toString(),edad.getText().toString(),urlImg,email.getText().toString(),clave.getText().toString());
            }
        });

        val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(SignUpActivity.this);

                String message = "Validando...";

                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(1f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

                pDialog.setMessage(ss2);

                pDialog.setCancelable(false);
                pDialog.show();
                validar(codigo.getText().toString());
            }
        });
    }

    private void openGallery() {
        Intent gallery =
                new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {

                System.out.println("******* " + data.getData());
                Uri imageUri = data.getData();
                img.setImageURI(imageUri);
                img.setVisibility(View.GONE);
                writePhoto(img);

            } catch (Exception e) {
                System.out.println("*** error: " + e);
            }
        }
    }

    public void signup(
            final String name,
            final String edad,
            final String urlImg,
            final String correo,
            final String psw) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        String url = "https://tesis-service.herokuapp.com/signup";
        String url2="http://192.168.1.14:8080/Tesis_SQL/signup";
        String url3="http://54.227.36.192:8080/Tesis_SQL/signup";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.toString().equals("ok")) {
                            //Toast.makeText(SignUpActivity.this, "Registro correcto", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            Intent i = new Intent(SignUpActivity.this, GustosActivity.class);
                            session.createLoginSession(correo);
                            startActivity(i);
                            SignUpActivity.this.finish();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "¡El correo ya existe!", Toast.LENGTH_SHORT).show();
                            email.setTextColor(Color.RED);
                            email.setError("Correo ya usado");
                            Selection.setSelection((Editable) email.getText(), email.getSelectionEnd());
                            email.requestFocus();
                            imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("edad", edad);
                params.put("foto", urlImg);
                params.put("correo", correo);
                params.put("psw", psw);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void validar(final String cod) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://tesis-service.herokuapp.com/validarCodigo";
        String url2="http://192.168.1.14:8080/Tesis_SQL/validarCodigo";
        String url3="http://54.227.36.192:8080/Tesis_SQL/validarCodigo";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if (response.toString().equals("true")) {
                            pDialog.dismiss();
                            //Toast.makeText(SignUpActivity.this, "Código correcto", Toast.LENGTH_SHORT).show();
                            codigo.setEnabled(false);
                            codigo.setFocusable(false);
                            nombre.setFocusable(true);
                            val.setVisibility(View.GONE);
                            nombre.setVisibility(View.VISIBLE);
                            edad.setVisibility(View.VISIBLE);
                            email.setVisibility(View.VISIBLE);
                            clave.setVisibility(View.VISIBLE);
                            img.setVisibility(View.VISIBLE);


                        } else {
                            pDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", cod);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void writePhoto(ImageView img) {

        BitmapDrawable bm = (BitmapDrawable) img.getDrawable();
        Bitmap mysharebmp = bm.getBitmap();

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            mysharebmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            //you can create a new file name "test.jpeg"
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + File.separator + "tmp.jpeg");

            if (f.exists()) {
                f.delete();
                f.createNewFile();
            } else {
                f.createNewFile();
            }

            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            // remember close de FileOutput
            fo.close();
            urlImg = f.getPath();
            System.out.println("**** " + f.getPath());
            new Upload().execute();

            //Toast.makeText(SignUpActivity.this, urlImg, Toast.LENGTH_SHORT).show();
            //uploadPhoto(urlImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class Upload extends AsyncTask<String, Void, String> {

        Map u;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage("Uploading Image...");


            String message = "Cargando...";

            SpannableString ss2 = new SpannableString(message);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

            pDialog.setMessage(ss2);

            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Map config = new HashMap();
                config.put("cloud_name", "dsdrbqoex");
                config.put("api_key", "285423822327279");
                config.put("api_secret", "0K7-UMpvn21oyqDdKO-xJ_P9_t8");

                Cloudinary cloudinary = new Cloudinary(config);

                u = cloudinary.uploader().upload(urlImg, ObjectUtils.emptyMap());

            } catch (Exception e) {

            }

            return "Listo";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            img.setVisibility(View.VISIBLE);
            urlImg = u.get("url").toString();
            signup.setVisibility(View.VISIBLE);
            //System.out.println(u.get("url"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(i);
        SignUpActivity.this.finish();

    }
}

package com.example.julian.clubculturallima;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    String datos;

    CircleImageView imgP;
    EditText name,edad;
    int PICK_IMAGE=200;
    String urlImg;
    ImageView btn_edit;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("datos", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        if (getArguments() != null) {
            datos = getArguments().getString("datos");
        }

        //Toast.makeText(getActivity(), datos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_profile, container, false);
        imgP=(CircleImageView)v.findViewById(R.id.imgPerfil);
        name=(EditText)v.findViewById(R.id.nombre);
        edad=(EditText)v.findViewById(R.id.edad);

        try{
            JSONParser p=new JSONParser();
            JSONObject o=(JSONObject)p.parse(datos);
            Picasso.with(getActivity()).load((String)o.get("foto")).into(imgP);
            imgP.setEnabled(false );
            name.setText((String)o.get("nombre"));
            name.setEnabled(false);
            edad.setText(String.valueOf((Long)o.get("edad")));
            edad.setEnabled(false);

        }catch (Exception e){
            System.out.println("ERROR: "+e);
            Toast.makeText(getActivity(), "Error al cargar los datos", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void openGallery() {
        Intent gallery =
                new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public class Upload extends AsyncTask<String,Void,String> {
        ProgressDialog pDialog;
        Map u;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Uploading Image...");


            String message= "Espere un momento...";

            SpannableString ss2 =  new SpannableString(message);
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

                u=cloudinary.uploader().upload(urlImg, ObjectUtils.emptyMap());

            } catch (Exception e) {

            }

            return "Listo";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            imgP.setVisibility(View.VISIBLE);
            urlImg=u.get("url").toString();
            System.out.println(u.get("url"));
        }
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




}

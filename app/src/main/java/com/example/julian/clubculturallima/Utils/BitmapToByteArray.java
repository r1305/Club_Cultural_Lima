package com.example.julian.clubculturallima.Utils;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class BitmapToByteArray extends AsyncTask<Bitmap, Void, byte[]> {
    public BitmapToByteArray() {
        Log.d("LoadingImageBuffer", "Start");
    }

    protected byte[] doInBackground(Bitmap... source) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        source[0].compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    protected void onPostExecute(byte[] result) {
        Utils.imageBuffer = result;
        Utils.bufferingImage = false;
        Log.d("LoadingImageBuffer", "Ready");
    }

}

package com.example.tomdado.zadanie;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UploadFileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private Button buttonChooseFile;
    private Button buttonUpload;
    private static final int PICK_IMAGE = 100;
    private Uri fileURI;
    private Bitmap bitmap;
    String urlUpload = "http://mobv.mcomputing.eu/upload/index.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        imageView = findViewById(R.id.imageView);
        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonChooseFile.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void uploadFile(){
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, urlUpload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //smr.addFile("upfile", fileURI.toString());
        smr.addMultipartParam("upfile", "image/jpeg", fileURI.toString());
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(smr);

/*
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(), "error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String imageData = imageToString(bitmap);
                params.put("upfile", "");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(UploadFileActivity.this);
        requestQueue.add(stringRequest);*/
    }

    @Override
    public void onClick(View view){
        if(view == buttonChooseFile){
            openGallery();
        }
        if(view == buttonUpload){
            try {
                uploadFile();
            }catch (Exception e) {
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            fileURI = data.getData();
            imageView.setImageURI(fileURI);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }


}

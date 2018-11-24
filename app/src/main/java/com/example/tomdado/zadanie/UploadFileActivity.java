package com.example.tomdado.zadanie;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
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
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UploadFileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private Button buttonChooseFile;
    private Button buttonUpload;
    private static final int PICK_IMAGE = 100;
    private Uri fileURI;
    String picturePath = "";
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
        File uploadVideo = new File(picturePath);
        if(!uploadVideo.exists()){
            Toast.makeText(getApplicationContext(), "upload err: " + picturePath, Toast.LENGTH_LONG).show();
            Crashlytics.log("Upload Error. " + "Video not found.");
            return;
        }

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, urlUpload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");
                            Toast.makeText(getApplicationContext(), "resp: " + response, Toast.LENGTH_LONG).show();

                            Crashlytics.log("Upload file " + "response: " + response);
                            if (status.equals("ok")) {
                                //ulozit do DB
                                Toast.makeText(getApplicationContext(), "ok: " + response, Toast.LENGTH_LONG).show();
                            }
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                            Toast.makeText(getApplicationContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "err: " + error, Toast.LENGTH_LONG).show();
                        Crashlytics.logException(error);
                    }
                }
        );

        smr.addFile("upfile", picturePath);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(smr);

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

   /* @Override
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
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            fileURI = data.getData( );
            picturePath = getPath( getApplicationContext( ), fileURI );
            imageView.setImageURI(fileURI);
            Log.d("Picture Path", picturePath);
        }
    }
    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }


}

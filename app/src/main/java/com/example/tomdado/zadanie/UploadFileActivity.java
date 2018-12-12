package com.example.tomdado.zadanie;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UploadFileActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout mDrawerLayout;
    private ImageView imageView;
    private Button buttonChooseFile;
    private Button buttonUpload;
    private Uri fileURI;
    String picturePath = "";
    String urlUpload = "http://mobv.mcomputing.eu/upload/index.php";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String mimeType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        setNavigationView();
        imageView = findViewById(R.id.imageView);
        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonChooseFile.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            //profile acitivity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        db = FirebaseFirestore.getInstance();
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("video/*, image/*");
        gallery.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        startActivityForResult(gallery,1);
    }

    private void updateUser(@NonNull long numberOfPosts){
        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        numberOfPosts= numberOfPosts + 1;
        Map<String, Object> data = new HashMap<>();
        data.put("numberOfPosts", numberOfPosts);
        DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());
        docRef.update(data);
    }


    private void saveToDb(String fileName,String type){
        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());

        Map<String, Object> post = new HashMap<>();
        post.put("username", firebaseUser.getEmail());
        post.put("date", Calendar.getInstance().getTime() );
        post.put("userid", firebaseUser.getUid());
        post.put("type",type);

        if(type.equals("image")){
            post.put("imageurl", fileName);
            post.put("videourl", "");
        }
        else {
            post.put("videourl", fileName);
            post.put("imageurl", "");
        }

        db.collection("posts").add(post);

        docRef.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        Crashlytics.log("UploadFile activity - document exists");
                        try{
                            Long numberOfPosts = documentSnapshot.getLong("numberOfPosts");
                            updateUser(numberOfPosts);
                        } catch (@NonNull Exception e){
                            Crashlytics.logException(e);
                        }
                    }else{
                        Crashlytics.log("UploadFile activity - document doesnt exist");
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
                }
            });
    }

    private void uploadFile(){
        mimeType = getMimeType(picturePath);
        File uploadVideo = new File(picturePath);
        if(!uploadVideo.exists()){
            Toast.makeText(getApplicationContext(), "upload err: " + picturePath, Toast.LENGTH_LONG).show();
            Crashlytics.log("Upload Error. " + "Video not found.");
            return;
        }

        Ion.with(UploadFileActivity.this)
            .load("POST",urlUpload)
            .setMultipartFile("upfile", mimeType, uploadVideo)
            .asString()
            .setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception ex, String response) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String status = jObj.getString("status");
                        Crashlytics.log("Upload file " + "response: " + response);
                        if (status.equals("ok")) {
                            if(!switchMimeType(mimeType).equals("")){
                                saveToDb(jObj.getString("message"),switchMimeType(mimeType));
                                Toast.makeText(getApplicationContext(), "Upload successful!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Crashlytics.log("Unsupported file type");
                                Toast.makeText(getApplicationContext(), "Unsupported file type", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Crashlytics.log("Upload failed");
                            Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                        Toast.makeText(getApplicationContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
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
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            fileURI = data.getData( );
            picturePath = getPath( getApplicationContext( ), fileURI );
            mimeType = getMimeType(picturePath);

            if(!switchMimeType(mimeType).equals("")){
                if(switchMimeType(mimeType).equals("video")){
                    Glide.with(getApplicationContext())
                            .load(picturePath) // or URI/path
                            .into(imageView); //imageview to set thumbnail to
                }
                else {
                    Glide.with(getApplicationContext())
                            .load(fileURI) // or URI/path
                            .apply(new RequestOptions()
                                .fitCenter()
                                .centerCrop()
                            )
                            .into(imageView);

                }
            }
            else {
                Crashlytics.log("Nepodporovany typ suboru");
                Toast.makeText(getApplicationContext(), "Nepodporovany typ suboru", Toast.LENGTH_LONG).show();
            }

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

    private void setNavigationView(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        SwitchClass switchClass = new SwitchClass();
                        finish();
                        startActivity(new Intent(getApplicationContext(),switchClass.getActivity(menuItem.getItemId())));
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public String switchMimeType(String mimeType){
        switch(mimeType){
            case "image/jpeg":
                return "image";
            case "image/png":
                return "image";
            case "video/mp4":
                return "video";
        }
        return "";
    }
}

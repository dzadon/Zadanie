package com.example.tomdado.zadanie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonLogout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextView textViewName;
    private TextView textViewRegDatetime;
    private TextView textViewRegText;
    private TextView textViewNumberOfPosts;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setNavigationView();
        textViewName = findViewById(R.id.textViewName);
        textViewRegDatetime = findViewById(R.id.textViewRegDatetime);
        textViewRegText= findViewById(R.id.textViewRegText);
        textViewNumberOfPosts = findViewById(R.id.textViewNumberOfPosts);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        buttonLogout = findViewById(R.id.buttonLogout);

        if(firebaseAuth.getCurrentUser() == null){
            //profile acitivity
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }else{
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Log.d("USER","user uid "+ user.getUid());
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Crashlytics.log("Profile activity - document exists");
                                String name = documentSnapshot.getString("email");
                                textViewName.setText("Name: " + name);
                                String numberOfPosts = Long.toString(documentSnapshot.getLong("numberOfPosts"));
                                textViewNumberOfPosts.setText("Number of posts: " + numberOfPosts);
                                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                Date date = Objects.requireNonNull(documentSnapshot.getTimestamp("date")).toDate();
                                String regDatetime =  df.format(date);
                                textViewRegText.setText("DateTime of registration: ");
                                textViewRegDatetime.setText(regDatetime);

                            }else{
                                Crashlytics.log("Profile activity - document doesnt exist");
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

        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void setNavigationView(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        SwitchClass switchClass = new SwitchClass();
                        finish();
                        startActivity(new Intent(getApplicationContext(),switchClass.getActivity(menuItem.getItemId())));
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}

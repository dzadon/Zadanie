package com.example.tomdado.zadanie;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ArrayList<SectionDataModel> allSampleData;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
           Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        Log.d("ACTIVITY","ACTIVITY MAIN");
        checkPermissions();
        setNavigationView();
        allSampleData = new ArrayList<>();
        createData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(allSampleData, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView);

    }


    private void createData() {
        final List<SingleItemModel> posts = new ArrayList<>();
        final Map<String,SingleItemModel> authors = new HashMap<>();
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){
            @Override
            public void onSuccess(QuerySnapshot QueryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : QueryDocumentSnapshots) {
                    SingleItemModel item = new SingleItemModel();
                    item.setProfileView(true);
                    item.setAuthor(document.getString("email"));
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = document.getTimestamp("date").toDate();
                    String datetime =  df.format(date);
                    item.setDateTimeOfRegistration(datetime);
                    item.setNumberOfPosts(Long.toString(document.getLong("numberOfPosts")));
                    authors.put(document.getString("email"),item);
                }

                db.collection("posts").orderBy("date",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){
                    @Override
                    public void onSuccess(QuerySnapshot QueryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : QueryDocumentSnapshots) {
                            SingleItemModel item = new SingleItemModel();
                            item.setProfileView(false);
                            item.setUrl(document.getString("imageurl"));
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            Date date = document.getTimestamp("date").toDate();
                            String datetime =  df.format(date);
                            item.setDateTimeOfPost(datetime);
                            item.setAuthor(document.getString("username"));
                            posts.add(item);
                        }

                        for (SingleItemModel post : posts){
                            SectionDataModel dm = new SectionDataModel();
                            ArrayList<SingleItemModel> singleItemModels = new ArrayList<>();

                            singleItemModels.add(authors.get(post.getAuthor()));
                            for (SingleItemModel post2 : posts){
                                if(post2.getAuthor().equals(post.getAuthor())){
                                    singleItemModels.add(post2);
                                }
                            }
                            dm.setAllItemInSection(singleItemModels);
                            allSampleData.add(dm);
                        }

                    }
                });

            }
        });

    }


    private void createData2() {
        for (int i = 1; i <= 20; i++) {
            SectionDataModel dm = new SectionDataModel();
            ArrayList<SingleItemModel> singleItemModels = new ArrayList<>();

            SingleItemModel profileItem = new SingleItemModel();
            profileItem.setAuthor("Author" +i);
            profileItem.setDateTimeOfRegistration("25.10.1994");
            profileItem.setNumberOfPosts("20");
            profileItem.setProfileView(true);
            singleItemModels.add(profileItem);
            SingleItemModel postItem;
            for (int j = 1; j <= 20; j++) {
                postItem = new SingleItemModel();
                postItem.setAuthor("Author " +i+" "+ j);
                postItem.setDateTimeOfPost("25.11.2018");
               //TODO
                postItem.setUrl("");
                postItem.setProfileView(false);
                singleItemModels.add(postItem);
            }
            dm.setAllItemInSection(singleItemModels);
            allSampleData.add(dm);
        }

    }

    private void setNavigationView(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem){
                        SwitchClass switchClass = new SwitchClass();
                        finish();
                        startActivity(new Intent(getApplicationContext(),switchClass.getActivity(menuItem.getItemId())));
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
    }

}

package com.example.tomdado.zadanie;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ArrayList<SectionDataModel> allSampleData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ACTIVITY","ACTIVITY MAIN");

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

}

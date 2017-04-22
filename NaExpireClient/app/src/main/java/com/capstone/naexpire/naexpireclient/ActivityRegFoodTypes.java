package com.capstone.naexpire.naexpireclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityRegFoodTypes extends AppCompatActivity {

    ArrayList<String> foods = new ArrayList<String>();
    ArrayList<String> checked = new ArrayList<String>(); //list of currently selected food types
    ListAdapterFoods adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_food_types);
        setTitle("Register"); //set activity title

        //dummy data
        foods.add("Mexican");
        foods.add("Cajun");
        foods.add("Vietnamese");
        foods.add("Chinese");
        foods.add("Mediterranean");
        foods.add("Japanese");
        foods.add("Indian");
        foods.add("Korean");
        foods.add("Italian");
        foods.add("Thai");
        foods.add("Greek");
        foods.add("Lebanese");
        foods.add("Moroccan");
        foods.add("French");
        foods.add("Spanish");
        foods.add("German");
        foods.add("Turkish");
        foods.add("Caribbean");

        adapter = new ListAdapterFoods(this, foods, checked);
        final ListView listView = (ListView) findViewById(R.id.lstRegFoodsList);
        listView.setAdapter(adapter);
    }

    public void clickFinish(View view){
        Intent intent = new Intent(this, ActivityNavDrawer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

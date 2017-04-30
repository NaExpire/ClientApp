package com.capstone.naexpire.naexpireclient;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class FragmentEditFoodTypes extends Fragment {
    ListAdapterFoods adapter;
    ArrayList<String> foods = new ArrayList<String>();
    ArrayList<String> checked = new ArrayList<String>();

    public FragmentEditFoodTypes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_foods, container, false);

        final SharedPreferences sharedPref = getActivity().getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        //base food types
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

        //get previously checked food types
        String[] savedFoods  = sharedPref.getString("foods", "").split(",");
        for(int i = 0; i < savedFoods.length; i++){
            checked.add(savedFoods[i]);
        }

        adapter = new ListAdapterFoods(FragmentEditFoodTypes.this.getContext(), foods, checked);
        final ListView listView = (ListView) view.findViewById(R.id.lstPrefFoods);
        listView.setAdapter(adapter);

        Button userprefs = (Button) view.findViewById(R.id.btnPrefUser);
        Button save = (Button) view.findViewById(R.id.btnPrefSaveFoods);

        //switch to user prefs fragment if button is pressed
        userprefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPreferences fragmentPreferences = new FragmentPreferences();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, fragmentPreferences).commit();
            }
        });

        //save changes is tapped
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //put all checked values into a comma spearated string
                checked = adapter.getChecked();
                String allChecked = checked.get(0);
                for(int i = 1; i < checked.size(); i++){
                    allChecked += ","+checked.get(i);
                }

                //save to shared preferences
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("foods", allChecked);
                editor.commit();

                Toast.makeText(FragmentEditFoodTypes.this.getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

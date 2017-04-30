package com.capstone.naexpire.naexpireclient;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


public class FragmentPastOrders extends Fragment {
    private DatabaseHelperPastOrder dbHelperPast = null;

    ListAdapterOrdersPast adapter;

    public FragmentPastOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past_orders, container, false);

        dbHelperPast = new DatabaseHelperPastOrder(getActivity().getApplicationContext());

        FragmentPastOrders.this.getActivity().setTitle("Past Orders"); //set title

        adapter = new ListAdapterOrdersPast(FragmentPastOrders.this.getContext());
        ListView listview = (ListView) view.findViewById(R.id.lstOrdersPast);
        listview.setAdapter(adapter);

        SQLiteDatabase dbPast = dbHelperPast.getReadableDatabase();

        Cursor result = dbPast.rawQuery("SELECT * FROM past", null);

        while(result.moveToNext()){
            //0 id
            //1 items
            //2 restaurant
            //3 time
            //4 price
            //5 quantity
            //6 rating

            adapter.newItem(result.getString(0), result.getString(1), result.getString(2),
                    result.getString(3), result.getString(4), result.getString(5), result.getString(6));
        }

        dbPast.close();
        result.close();

        Button current = (Button) view.findViewById(R.id.btnOrdersCurrent);

        //navigate to current orders fragment when current orders button is tapped
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCurrentOrders fragmentCurrentOrders = new FragmentCurrentOrders();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, fragmentCurrentOrders).commit();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentPastOrders.this.getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_past_order, null);
                final TextView itemName = (TextView) dialogView.findViewById(R.id.lblPastName);
                final TextView restName = (TextView) dialogView.findViewById(R.id.lblPastRest);
                final TextView orderprices = (TextView) dialogView.findViewById(R.id.lblPastPrices);
                final TextView orderid = (TextView) dialogView.findViewById(R.id.lblPastOrderID);
                final TextView ordertotal = (TextView) dialogView.findViewById(R.id.lblPastTotal);
                final TextView ordertime = (TextView) dialogView.findViewById(R.id.lblPastTime);
                final RatingBar rating = (RatingBar) dialogView.findViewById(R.id.ratingBar);
                Button orderSubmit = (Button) dialogView.findViewById(R.id.btnPastSubmit);

                //set dialog text values
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                itemName.setText(adapter.getName(position));
                orderprices.setText("$"+decimalFormat.format(adapter.getPrice(position)));
                restName.setText(adapter.getRestaurant(position));
                orderid.setText("Order #"+adapter.getOrderId(position));
                ordertotal.setText("$"+decimalFormat.format(adapter.getTotal(position)));
                ordertime.setText("Placed at: "+adapter.getTime(position));
                rating.setRating(adapter.getRating(position));

                dialogBuilder.setView(dialogView);
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                //save rating when save changes tapped
                orderSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int stars = (int)rating.getRating();
                        adapter.setRating(position, stars);

                        SQLiteDatabase dbPast = dbHelperPast.getWritableDatabase();
                        ContentValues value = new ContentValues();
                        value.put("rating", stars);
                        dbPast.update("past", value, "id = ? and restaurant = ?",
                                new String[]{adapter.getOrderId(position), adapter.getRestaurant(position)});
                        dbPast.close();

                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }

}

package com.capstone.naexpire.naexpireclient;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class FragmentCurrentOrders extends Fragment {
    DatabaseHelperCurrentOrder dbHelperCurrent = null;

    ListAdapterOrdersCurrent adapter;

    public FragmentCurrentOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_orders, container, false);

        FragmentCurrentOrders.this.getActivity().setTitle("Current Orders"); //set title

        Button past = (Button) view.findViewById(R.id.btnOrdersPast);

        adapter = new ListAdapterOrdersCurrent(FragmentCurrentOrders.this.getContext());
        ListView listview = (ListView) view.findViewById(R.id.lstOrdersCurrent);
        listview.setAdapter(adapter);

        //set up connection to current orders database
        dbHelperCurrent = new DatabaseHelperCurrentOrder(getActivity().getApplicationContext());
        SQLiteDatabase dbCurrent = dbHelperCurrent.getReadableDatabase();

        //get all the entire current orders database
        Cursor result = dbCurrent.rawQuery("SELECT * FROM currentOrders", null);

        //order of table columns
        //0 id
        //1 name
        //2 restaurant
        //3 address
        //4 phone
        //5 price
        //6 quantity
        //7 image
        //8 time

        //add each current order to the list adapter
        while(result.moveToNext()){
            adapter.newItem(result.getString(0),result.getString(1),result.getString(2),
                    result.getString(3), result.getString(4), result.getString(5),
                    result.getString(6), result.getString(7), result.getString(8));
        }

        dbCurrent.close();
        result.close();

        //'tab' button to view past orders is tapped
        //brings user to past orders fragment
        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPastOrders fragmentPastOrders = new FragmentPastOrders();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, fragmentPastOrders).commit();
            }
        });

        //when an item in the list is tapped build a dialog showing info about the order
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentCurrentOrders.this.getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_current_order, null);
                final TextView itemName = (TextView) dialogView.findViewById(R.id.lblCurrentName);
                final TextView restName = (TextView) dialogView.findViewById(R.id.lblCurrentRest);
                final TextView orderprices = (TextView) dialogView.findViewById(R.id.lblCurrentPrices);
                final TextView orderid = (TextView) dialogView.findViewById(R.id.lblCurrentOrderID);
                final TextView ordertotal = (TextView) dialogView.findViewById(R.id.lblCurrentTotal);
                final TextView ordertime = (TextView) dialogView.findViewById(R.id.lblCurrentTime);
                Button orderDirec = (Button) dialogView.findViewById(R.id.btnCurrentDirections);
                Button orderCall = (Button) dialogView.findViewById(R.id.btnCurrentCall);
                Button orderFulfill = (Button) dialogView.findViewById(R.id.btnCurrentFullfill);

                itemName.setText(adapter.getQuantity(position) + " - " + adapter.getItemName(position));
                orderprices.setText("$"+adapter.getPrice(position));
                restName.setText(adapter.getRestaurantName(position));
                orderid.setText("Order #"+adapter.getId(position));
                ordertotal.setText("$"+adapter.getTotal(position));
                ordertime.setText("Placed at: "+adapter.getTime(position));

                dialogBuilder.setView(dialogView);
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                //get directions button tapped
                //opens Google Maps to show where the restaurant is
                orderDirec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "geo:0,0?q="+adapter.getRestaurantName(position)+", "+adapter.getAddress(position);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });

                //call restaurant button tapped
                //dials in the restaurant phone number, but does not auto-initiate the call
                orderCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "tel: "+adapter.getPhone(position);
                        Intent intent = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                //fulfill order is tapped
                orderFulfill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); //dismiss order info dialog

                        //build + show confirm fulfill dialog
                        AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(FragmentCurrentOrders.this.getContext());
                        View dialogView2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm_fulfill, null);
                        Button no = (Button) dialogView2.findViewById(R.id.btnFulfillNo);
                        Button yes = (Button) dialogView2.findViewById(R.id.btnFulfillYes);
                        dialogBuilder2.setView(dialogView2);
                        final AlertDialog dialog2 = dialogBuilder2.create();
                        dialog2.show();

                        //no tapped
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show(); //show order info dialog again
                                dialog2.dismiss(); //dismiss confirm fulfill dialog
                            }
                        });

                        //yes tapped
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //insert into past orders db
                                DatabaseHelperPastOrder dbHelperPast = new DatabaseHelperPastOrder(getActivity().getApplicationContext());
                                SQLiteDatabase dbPast = dbHelperPast.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("id", adapter.getId(position));
                                values.put("items", adapter.getItemName(position));
                                values.put("restaurant", adapter.getRestaurantName(position));
                                values.put("time", adapter.getTime(position));
                                values.put("price", adapter.getPrice(position));
                                values.put("quantity", adapter.getQuantity(position));
                                values.put("rating", "0");
                                dbPast.insert("past", null, values);
                                dbPast.close();
                                dbHelperPast.close();

                                //remove from current orders db
                                SQLiteDatabase dbCurrent = dbHelperCurrent.getWritableDatabase();
                                String[] selectionArgs = {adapter.getId(position), adapter.getRestaurantName(position)};
                                dbCurrent.delete("currentOrders", "id = ? and restaurant = ?", selectionArgs);
                                dbCurrent.close();

                                adapter.deleteItem(position);

                                dialog2.dismiss();
                            }
                        });
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        dbHelperCurrent.close();
        super.onDestroy();
    }
}

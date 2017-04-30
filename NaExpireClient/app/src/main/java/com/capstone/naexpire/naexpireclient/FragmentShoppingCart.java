package com.capstone.naexpire.naexpireclient;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class FragmentShoppingCart extends Fragment {
    private DatabaseHelperDeals dbHelperDeals = null;
    private DatabaseHelperCurrentOrder dbHelperCurrent = null;
    private SharedPreferences sharedPref;

    private ListAdapterCart adapter;
    private TextView subtotal;
    private Button toDeals, placeOrder;


    public FragmentShoppingCart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        sharedPref = getActivity().getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        dbHelperDeals = new DatabaseHelperDeals(getActivity().getApplicationContext());
        dbHelperCurrent = new DatabaseHelperCurrentOrder(getActivity().getApplicationContext());

        toDeals = (Button) view.findViewById(R.id.btnCartBack);
        placeOrder = (Button) view.findViewById(R.id.btnCartPlace);

        FragmentShoppingCart.this.getActivity().setTitle("Shopping Cart"); //set activity title

        adapter = new ListAdapterCart(FragmentShoppingCart.this.getContext(), FragmentShoppingCart.this);
        ListView listview = (ListView) view.findViewById(R.id.lstCart);
        listview.setAdapter(adapter);

        final SQLiteDatabase db = dbHelperDeals.getReadableDatabase();

        //fill shopping cart with elements added to the cart
        Cursor result = db.rawQuery("SELECT * FROM deals WHERE cartquantity <> '0'", null);
        //0 itemId
        //1 name
        //2 restaurant
        //3 address
        //4 description
        //5 price
        //6 quantity
        //7 image
        //8 cartQuantity

        while(result.moveToNext()){
            adapter.newItem(result.getString(0), result.getString(1),
                    result.getString(2), result.getString(3), result.getString(4),
                    result.getString(5), result.getString(6), result.getString(7),
                    result.getString(8));
        }

        db.close();
        result.close();

        //set the subtotal
        subtotal = (TextView) view.findViewById(R.id.lblCartSubtotal);
        subtotal.setText(adapter.updateSubtotal());

        //navigate to deals fragment if back to deals is tapped
        toDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentDeals fragmentDeals = new FragmentDeals();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, fragmentDeals).commit();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentShoppingCart.this.getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_cart, null);
                final TextView item = (TextView) dialogView.findViewById(R.id.lblItem);
                final TextView restaurant = (TextView) dialogView.findViewById(R.id.lblRestaurant);
                final TextView description = (TextView) dialogView.findViewById(R.id.lblDescription);
                final TextView address = (TextView) dialogView.findViewById(R.id.lblAddress);
                final TextView price = (TextView) dialogView.findViewById(R.id.lblPrice);
                ImageView itemPic = (ImageView) dialogView.findViewById(R.id.imgItem);
                final Button update = (Button) dialogView.findViewById(R.id.btnUpdate);

                //set dialog text values
                item.setText(adapter.getName(position));
                price.setText("$"+adapter.getPrice(position)+" /each");
                restaurant.setText(adapter.getRestaurant(position));
                description.setText(adapter.getDescription(position));
                String[] a = adapter.getAddress(position).split(",");
                address.setText(a[0]);
                final int num = adapter.getCartQuantity(position);
                Glide.with(FragmentShoppingCart.this.getContext()).load(adapter.getImage(position)).into(itemPic);

                //create number array for quantity spinner
                String[] n = new String[num];
                for(int i = 0; i < num; i++){
                    n[i] = ""+(i+1);
                }

                final Spinner mspin=(Spinner) dialogView.findViewById(R.id.spnQuantity);
                final ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(FragmentShoppingCart.this.getContext(),
                        android.R.layout.simple_spinner_item, n);
                mspin.setAdapter(sAdapter);
                mspin.setSelection(n.length-1);

                dialogBuilder.setView(dialogView);
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                //update cart value in local db if quantity changed
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int newAmount = mspin.getSelectedItemPosition() + 1;
                        if(newAmount != adapter.getCartQuantity(position)){
                            SQLiteDatabase dbDeals = dbHelperDeals.getWritableDatabase();
                            ContentValues value = new ContentValues();

                            value.put("cartQuantity", ""+newAmount);

                            String[] selectionArgs = {""+adapter.getId(position)}; //select by matching id
                            dbDeals.update("deals", value, "id = ?", selectionArgs);
                            dbDeals.close();
                            adapter.setCartQuantity(position, newAmount);
                            adapter.notifyDataSetChanged();
                            subtotal.setText(adapter.updateSubtotal());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        //place order tapped
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getSize() == 0){ //ensure shopping cart has at least 1 item
                    Toast.makeText(FragmentShoppingCart.this.getContext(), "Shopping cart is empty.",
                            Toast.LENGTH_SHORT).show();
                }
                else if(!sharedPref.getString("cardNumber", "").equals("")){ //if card info already entered
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentShoppingCart.this.getContext());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm_card, null);
                    final TextView cardNum = (TextView) dialogView.findViewById(R.id.lblConfirmCard);
                    final Button no = (Button) dialogView.findViewById(R.id.btnNo);
                    final Button yes = (Button) dialogView.findViewById(R.id.btnYes);

                    //only display last 4 of credit card #
                    cardNum.setText("XXXX - XXXX - XXXX - "+sharedPref.getString("cardNumber","").substring(12));

                    dialogBuilder.setView(dialogView);
                    final AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    //yes tapped
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //create dummy order id
                            Random rnd = new Random();
                            int orderId = 1000000 + rnd.nextInt(9000000);

                            //timestamp the order
                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
                            Date date = new Date();
                            String dateTime = dateFormat.format(date);

                            //add all items in cart into current orders database
                            SQLiteDatabase dbCurrent = dbHelperCurrent.getWritableDatabase();
                            SQLiteDatabase dbDeals = dbHelperDeals.getWritableDatabase();

                            //insert order info into current orders db
                            int length = adapter.getSize();
                            for(int i = 0; i < length; i++){
                                String currentId = ""+adapter.getId(i);
                                int dealQuantity = adapter.getQuantity(i);
                                int cartQuantity = adapter.getCartQuantity(i);

                                ContentValues values = new ContentValues();
                                values.put("id", orderId);
                                values.put("name", adapter.getName(i));
                                values.put("restaurant", adapter.getRestaurant(i));
                                values.put("address", adapter.getAddress(i));
                                values.put("phone", "623"+orderId); //TEMPORARY
                                values.put("time", dateTime);
                                values.put("price", adapter.getPrice(i));
                                values.put("image", adapter.getImage(i));
                                values.put("quantity", cartQuantity);
                                dbCurrent.insert("currentOrders", null, values);

                                //delete deal if all of them were bought
                                if(cartQuantity == dealQuantity){
                                    String[] selectionArgs = {currentId};
                                    dbDeals.delete("deals", "id = ?", selectionArgs);
                                }
                                else{ //else update number of deals left
                                    ContentValues value = new ContentValues();

                                    value.put("quantity", ""+(dealQuantity - cartQuantity));
                                    value.put("cartQuantity", ""+0);

                                    String[] selectionArgs = {currentId}; //select by matching id
                                    dbDeals.update("deals", value, "id = ?", selectionArgs);
                                }
                            }
                            dbCurrent.close();
                            dbDeals.close();

                            FragmentCurrentOrders fragmentCurrentOrders = new FragmentCurrentOrders();
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.fragment_container, fragmentCurrentOrders).commit();

                            dialog.dismiss();
                        }
                    });

                }
                else{ //if no credit card info is entered navigate to user prefs fragment
                    Toast.makeText(FragmentShoppingCart.this.getContext(),
                            "Enter credit card information.", Toast.LENGTH_LONG).show();

                    FragmentPreferences fragmentPreferences = new FragmentPreferences();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.fragment_container, fragmentPreferences).commit();
                }
            }
        });

        return view;
    }

    public void setSubtotal(){
        subtotal.setText(adapter.updateSubtotal());
    }

    //delete item at given position from local deals db
    public void deleteItem(int position){
        SQLiteDatabase dbDeals = dbHelperDeals.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("cartQuantity", ""+0);

        String[] selectionArgs = {""+adapter.getId(position)}; //select by matching id
        dbDeals.update("deals", values, "id = ?", selectionArgs);

        dbDeals.close();

        Toast.makeText(FragmentShoppingCart.this.getContext(), adapter.getName(position) + " removed from cart.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        dbHelperCurrent.close();
        super.onDestroy();
    }
}

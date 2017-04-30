package com.capstone.naexpire.naexpireclient;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FragmentDeals extends Fragment {
    private DatabaseHelperDeals dbHelperDeals = null;

    private SharedPreferences sharedPref;

    private ImageButton goToCart;
    private TextView cartTotal;
    private RelativeLayout v;

    ListAdapterDeals adapter;
    ArrayList<String> itemId = new ArrayList<>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<Double> price = new ArrayList<Double>();
    ArrayList<String> description = new ArrayList<String>();
    ArrayList<String> restname = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<Integer> quantity = new ArrayList<Integer>();
    ArrayList<String> image = new ArrayList<String>();
    private int numInCart;


    public FragmentDeals() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_deals, container, false);
        FragmentDeals.this.getActivity().setTitle("Discounts"); //set activity title

        sharedPref = getActivity().getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        dbHelperDeals = new DatabaseHelperDeals(getActivity().getApplicationContext());

        adapter = new ListAdapterDeals(FragmentDeals.this.getContext());
        ListView listview = (ListView) view.findViewById(R.id.lstDiscounts);
        listview.setAdapter(adapter);

        cartTotal = (TextView) view.findViewById(R.id.lblCartNum);
        goToCart = (ImageButton) view.findViewById(R.id.imgbtnCart);

        //spinner to select filter method for menu items
        Spinner spinner = (Spinner) view.findViewById(R.id.spnFilter);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(
                FragmentDeals.this.getContext(),
                R.array.filter_array, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spAdapter);

        //if the user just logged in
       if(sharedPref.getInt("fromLogin", 0) == 1){
           //initial data to be inserted once on the first time the app is run to set up dummy data
           //only to be used while there are no endpoints to get real deals data
            /*itemId.add("0");
            itemId.add("1");
            itemId.add("2");
            itemId.add("3");
            itemId.add("4");
            itemId.add("5");
            name.add("Beef Taco");
            name.add("Caesar Salad");
            name.add("Chicken Taco");
            name.add("Cheeseburger");
            name.add("Shrimp Taco");
            name.add("Spaghetti Carbonara");
            restname.add("Chicken on a Stick");
            restname.add("DJ's Bagels");
            restname.add("Raising Canes");
            restname.add("In n Out");
            restname.add("Fiesta Burrito");
            restname.add("Noodles and Company");
            price.add(1.23);
            price.add(3.43);
            price.add(2.34);
            price.add(2.67);
            price.add(3.45);
            price.add(2.35);
            description.add("Taco with beef");
            description.add("Leafy greens");
            description.add("Taco with chicken");
            description.add("Burger with cheese");
            description.add("Taco with shrimp");
            description.add("Pasta with pasta sauce");
            address.add("3133 N Scottsdale Rd, Scottsdale, AZ 85251");
            address.add("13693 N Fountain Hills Blvd, Fountain Hills, AZ 85268");
            address.add("960 E University Dr, Tempe, AZ 85281");
            address.add("920 E Playa Del Norte Dr, Tempe, AZ 85281");
            address.add("7402 E McDowell Rd, Scottsdale, AZ 85257");
            address.add("2000 E Rio Salado Pkwy, Tempe, AZ 85281");
            quantity.add(2);
            quantity.add(1);
            quantity.add(5);
            quantity.add(3);
            quantity.add(7);
            quantity.add(3);
            image.add("android.resource://com.capstone.naexpire.naexpireclient/drawable/tacos");
            image.add("android.resource://com.capstone.naexpire.naexpireclient/drawable/salad");
            image.add("android.resource://com.capstone.naexpire.naexpireclient/drawable/tacos2");
            image.add("android.resource://com.capstone.naexpire.naexpireclient/drawable/burger");
            image.add("android.resource://com.capstone.naexpire.naexpireclient/drawable/shrimp");
            image.add("android.resource://com.capstone.naexpire.naexpireclient/drawable/carbonara");*/

           SharedPreferences.Editor editor = sharedPref.edit();
           editor.putInt("fromLogin", 0); //set that they have not just come from login
           editor.apply();

           //reset local databases if just came from register
            if(sharedPref.getInt("fromRegister", 0) == 2){
                //reset current orders for new register
                DatabaseHelperCurrentOrder dbHelperCurrent = new DatabaseHelperCurrentOrder(getActivity().getApplicationContext());
                SQLiteDatabase dbCurrent = dbHelperCurrent.getWritableDatabase();
                dbCurrent.delete("currentOrders", null,null);
                dbCurrent.close();
                dbHelperCurrent.close();

                //reset past orders for new register
                DatabaseHelperPastOrder dbHelperPast = new DatabaseHelperPastOrder(getActivity().getApplicationContext());
                SQLiteDatabase dbPast = dbHelperPast.getWritableDatabase();
                dbPast.delete("past", null,null);
                dbPast.close();
                dbHelperPast.close();

                //reset food preferences
                editor.putString("foods", "");
                editor.putInt("fromRegister", 0);
                editor.apply();
            }

            //empty local deals database
           SQLiteDatabase db = dbHelperDeals.getWritableDatabase();
           db.delete("deals", null,null);

           //get deals from backend database
           for(int i = 0; i < 30; i++) {
               String uri = "http://138.197.33.88/api/consumer/deal/"+i+"/";
               android.util.Log.w(this.getClass().getSimpleName(), "http: "+uri);
               new getDeal().execute(uri);
           }

        }
        else{
            SQLiteDatabase dbDeals = dbHelperDeals.getReadableDatabase();

            Cursor dealsResult = dbDeals.rawQuery("SELECT * FROM deals", null);

            numInCart = 0;
            while(dealsResult.moveToNext()){
                //order of columns in db
                //0 id
                //1 name
                //2 restaurant
                //3 address
                //4 description
                //5 price
                //6 quantity
                //7 image
                //8 cartQuantity
                adapter.newItem(dealsResult.getString(0), dealsResult.getString(1),
                        dealsResult.getString(2), dealsResult.getString(3), dealsResult.getString(4),
                        dealsResult.getString(5), dealsResult.getString(6), dealsResult.getString(7),
                        dealsResult.getString(8));

                numInCart += Integer.parseInt(dealsResult.getString(8));
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("numberInCart", numInCart);
            editor.commit();

            dbDeals.close();
            dealsResult.close();
        }

        if(numInCart>0) cartTotal.setText(""+numInCart);

        //initial sort of list items
        adapter.sortDiscounts(spinner.getSelectedItemPosition());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                adapter.sortDiscounts(position); //sort based on spinner seleciton
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentDeals.this.getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_deal_info, null);
                final TextView itemName = (TextView) dialogView.findViewById(R.id.lblInfoItemName);
                final TextView restName = (TextView) dialogView.findViewById(R.id.lblInfoRestName);
                final TextView itemPrice = (TextView) dialogView.findViewById(R.id.lblInfoPrice);
                final TextView itemDesc = (TextView) dialogView.findViewById(R.id.lblInfoDescription);
                final TextView restDist = (TextView) dialogView.findViewById(R.id.lblInfoDistance);
                v = (RelativeLayout) dialogView.findViewById(R.id.rlDealSpinner);
                ImageView itemPic = (ImageView) dialogView.findViewById(R.id.imgInfoPicture);
                final Button cart = (Button) dialogView.findViewById(R.id.btnInfoCart);

                //set text & image for dialog
                itemName.setText(adapter.getName(position));
                itemPrice.setText("$"+adapter.getPrice(position));
                restName.setText(adapter.getRestaurant(position));
                itemDesc.setText(adapter.getDescription(position));
                String[] a = adapter.getAddress(position).split(",");
                if(a[0] != null) restDist.setText(a[0]);
                else restDist.setText(adapter.getAddress(position));
                final int num = adapter.getQuantity(position) - adapter.getCartQuantity(position);
                Glide.with(FragmentDeals.this.getContext()).load(adapter.getImage(position)).into(itemPic);

                //create array to load quantity spinner
                String[] n = new String[num];
                for(int i = 0; i < num; i++){
                    n[i] = ""+(i+1);
                }

                //if not all deals are in cart
                if(adapter.getCartQuantity(position) < adapter.getQuantity(position)){
                    final Spinner mspin=(Spinner) dialogView.findViewById(R.id.spnInfoQuantity);
                    final ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(FragmentDeals.this.getContext(),android.R.layout.simple_spinner_item, n);
                    mspin.setAdapter(sAdapter);

                    dialogBuilder.setView(dialogView);
                    final AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    //add to cart tapped
                    cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int amount = Integer.parseInt(mspin.getSelectedItem().toString());
                            int cartNum = adapter.getCartQuantity(position);
                            adapter.setCartQuantity(position, cartNum + amount);

                            //update quantity in local deals db
                            SQLiteDatabase dbDeals = dbHelperDeals.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("cartQuantity", ""+(cartNum + amount));
                            String[] selectionArgs = {""+adapter.getId(position)}; //select by matching id
                            dbDeals.update("deals", values, "id = ?", selectionArgs);
                            dbDeals.close();

                            adapter.notifyDataSetChanged();

                            numInCart += amount;
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("numberInCart", numInCart);
                            editor.commit();

                            cartTotal.setText(""+numInCart);

                            dialog.dismiss();
                        }
                    });
                }
                else{ //if all the deals are in the user's cart already
                    v.setVisibility(View.GONE);
                    itemPrice.setText("Sold Out"); //show that the deal is sold out
                    cart.setText("Back to Deals"); //change add to cart button text

                    dialogBuilder.setView(dialogView);
                    final AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                }
            }
        });

        //cart icon tapped
        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentShoppingCart fragmentShoppingCart = new FragmentShoppingCart();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, fragmentShoppingCart).commit();
            }
        });

        return view;
    }

    private class getDeal extends AsyncTask<String,String,String> {
        private int mealId = -1;
        private int quantity = 0, restId = 0;
        private double price = 0.0;
        private String itemName = "", restName = "", restAddress = "";

        @Override
        protected String doInBackground(String... urls){
            String line;
            StringBuilder sb = new StringBuilder();
            HttpURLConnection connection = null;

            try {
                URL requestURL = new URL(urls[0]);
                connection = (HttpURLConnection) requestURL.openConnection();
                connection.setRequestMethod("GET");

                int HttpResult = connection.getResponseCode();
                android.util.Log.w(this.getClass().getSimpleName(), "Get Deal Response Code: "+HttpResult);

                if(HttpResult == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), "utf-8"
                    ));
                    while((line = br.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    br.close();

                    try{ //get deal values
                        JSONObject obj = new JSONObject(sb.toString());
                        mealId = obj.getInt("mealID");
                        price = obj.getDouble("dealPrice");
                        quantity = obj.getInt("quantity");
                        restId = obj.getInt("restaurantID");
                        itemName = obj.getString("itemName");
                        restName = obj.getString("restaurantName");
                        restAddress = obj.getString("restaurantAddress");
                    }catch (Exception e){}

                    //insert values into local deals db
                    SQLiteDatabase db = dbHelperDeals.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("id", ""+mealId);
                    values.put("name", itemName);
                    values.put("restaurant", restName);
                    values.put("address", restAddress);
                    //values.put("description", description.get(i));
                    values.put("price", ""+price);
                    //change this when real images get returned
                    values.put("image", "@drawable/caferedux_launcher_circle.png");
                    values.put("quantity", ""+quantity);
                    values.put("cartquantity", "0");
                    db.insert("deals", null, values);
                    db.close();

                    android.util.Log.w(this.getClass().getSimpleName(),
                            "Response Message: "+sb.toString());
                }
                else android.util.Log.w(this.getClass().getSimpleName(),
                            "Response Message: "+connection.getResponseMessage());
            }
            catch (MalformedURLException ex){ ex.printStackTrace(); }
            catch (IOException e){ e.printStackTrace(); }
            finally{ connection.disconnect(); }

            return ""+mealId;
        }

        @Override
        protected void onPostExecute(String result) {
            if(Integer.parseInt(result) != -1){ //if deal existed

                //0 itemId
                //1 name
                //2 restaurant
                //3 address
                //4 description
                //5 price
                //6 quantity
                //7 image
                //8 cartQuantity
                adapter.newItem(result, itemName, restName, restAddress, "", ""+price, ""+quantity,
                        "@drawable/caferedux_launcher_circle.png", "0");
            }
        }
    }

    @Override
    public void onDestroy() {
        dbHelperDeals.close();
        super.onDestroy();
    }
}

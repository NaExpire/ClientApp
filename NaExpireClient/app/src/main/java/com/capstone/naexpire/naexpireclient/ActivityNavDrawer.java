package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ActivityNavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sharedPref;

    TextView title, detail;

    NavigationView navigationView = null;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        sharedPref = getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        //set the initial displaed fragment to be the list of deals
        FragmentDeals fragmentDeals = new FragmentDeals();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, fragmentDeals).commit();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setup to change the text values at the top of the navigation drawer
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        title = (TextView) header.findViewById(R.id.lblDrawerUsername);
        detail = (TextView) header.findViewById(R.id.lblDrawerEmail);

        //set the text values to values stored and shared preferences
        title.setText(sharedPref.getString("firstName", "") +" "+
        sharedPref.getString("lastName", ""));
        detail.setText(sharedPref.getString("email", ""));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_discounts) {
            FragmentDeals fragmentDeals = new FragmentDeals();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container, fragmentDeals).commit();
        } else if (id == R.id.nav_shopping_cart) {
            FragmentShoppingCart fragmentShoppingCart = new FragmentShoppingCart();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container, fragmentShoppingCart).commit();
        } else if (id == R.id.nav_orders) {
            FragmentCurrentOrders fragmentCurrentOrders = new FragmentCurrentOrders();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container, fragmentCurrentOrders).commit();
        } else if (id == R.id.nav_preferences) {
            FragmentPreferences fragmentPreferences = new FragmentPreferences();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container, fragmentPreferences).commit();
        } else if (id == R.id.nav_logout) {
            new logout().execute("http://138.197.33.88/api/consumer/logout/ ");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class logout extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... urls){
            String line = null;
            StringBuilder sb = new StringBuilder();
            HttpURLConnection connection = null;

            try {
                line = "false";
                URL requestURL = new URL(urls[0]);
                connection = (HttpURLConnection) requestURL.openConnection();
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("session", sharedPref.getString("sessionId", ""));
                connection.setUseCaches(false);

                int HttpResult = connection.getResponseCode();
                android.util.Log.w(this.getClass().getSimpleName(), "Response Code: "+HttpResult);

                if(HttpResult == HttpURLConnection.HTTP_OK) line = "true";
                else{
                    line = "false";
                    android.util.Log.w(this.getClass().getSimpleName(),
                            "Response Message: "+connection.getResponseMessage());
                }
            }
            catch (MalformedURLException ex){ ex.printStackTrace(); }
            catch (IOException e){ e.printStackTrace(); }
            finally{ connection.disconnect(); }

            return line;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equals("true") ){ //successful logout

                //delete old sessionId
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sessionId", "");
                editor.commit();

                Intent intent = new Intent(ActivityNavDrawer.this, ActivityLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else { //logout failed
                Toast.makeText(ActivityNavDrawer.this, "Logout failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

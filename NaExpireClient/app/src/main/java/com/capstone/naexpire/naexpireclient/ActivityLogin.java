package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ActivityLogin extends AppCompatActivity {
    private SharedPreferences sharedPref;

    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        final VideoView splashvideo = (VideoView) findViewById(R.id.splashVideo);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.splashvideo);
        splashvideo.setVideoURI(uri);
        splashvideo.start();

        splashvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        email = (EditText) findViewById(R.id.txtLoginEmail);
        password = (EditText) findViewById(R.id.txtLoginPassword);

        //if the user just came from registration set the email and password fields
        //with the credentials they entered while registering
        if(sharedPref.getInt("fromRegister", 0) == 1){
            email.setText(sharedPref.getString("email", ""));
            password.setText(sharedPref.getString("password", ""));

            //set that the user is not coming from registration, so the email and password fields
            //aren't pre-filled when the login activity is visited later
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("fromRegister", 2);
            editor.commit();
        }

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layLogin);

        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });
    }

    //user taps the login button
    public void clickLogin(View view){
        //gets the entered email and password into strings
        String enteredEmail = email.getText().toString();
        String enteredPass = password.getText().toString();

        new login().execute("http://138.197.33.88/api/consumer/login/");
        //checks that both the email and password match the ones stored in shared preferences
        /*boolean match = enteredEmail.equals(sharedPref.getString("email", "")) &&
                enteredPass.equals(sharedPref.getString("password", ""));

        //if email and password are correct && both fields are filled
        if(match && !enteredEmail.isEmpty() && !enteredPass.isEmpty()){
            if(sharedPref.getInt("fromRegister", 0) == 2){

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirmation_code, null);
                final EditText code = (EditText) dialogView.findViewById(R.id.txtConfirmCode);
                Button submit = (Button) dialogView.findViewById(R.id.btnConfirm);

                dialogBuilder.setView(dialogView);
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String confirmationCode = code.getText().toString();
                        if(confirmationCode.equals("123")){
                            Intent intent = new Intent(getBaseContext(), ActivityNavDrawer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent); //navigate to navigation drawer activity
                            dialog.dismiss();
                        }
                        else Toast.makeText(ActivityLogin.this, "Incorrect code.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                new login().execute("http://138.197.33.88/api/consumer/login/");

                Intent intent = new Intent(this, ActivityNavDrawer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent); //navigate to navigation drawer activity
            }
        }
        else if(enteredEmail.isEmpty() || enteredPass.isEmpty()) //if either field is blank
            Toast.makeText(this, "Fill all fields.", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();*/
    }

    //user taps that they forgot their password
    public void clickForgot(View view){
        Toast.makeText(this, "Recovery email sent.", Toast.LENGTH_SHORT).show();
    }

    //user taps the register button
    public void clickRegister(View view){
        Intent intent = new Intent(this, ActivityRegister.class);
        startActivity(intent);
    }

    //async call to login endpoint
    private class login extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... urls){
            String line = null;
            String loginStatus = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection connection = null;

            try {
                URL requestURL = new URL(urls[0]);
                connection = (HttpURLConnection) requestURL.openConnection();
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("charset", "utf-8");
                connection.setUseCaches(false);

                String outputString = toJsonString();
                connection.setRequestProperty("Content-Length", "" + outputString.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(outputString);
                writer.flush();
                writer.close();

                int HttpResult = connection.getResponseCode();
                if(HttpResult == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), "utf-8"
                    ));
                    while((line = br.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    br.close();

                    try{
                        JSONObject obj = new JSONObject(sb.toString());
                        loginStatus = obj.getString("sessionID");
                    }catch (Exception e){}

                    android.util.Log.w(this.getClass().getSimpleName(),
                            "Response Message: "+sb.toString());
                }
                else{
                    android.util.Log.w(this.getClass().getSimpleName(),"Response Message: "+connection.getResponseMessage());
                }
            }
            catch (MalformedURLException ex){ ex.printStackTrace(); }
            catch (IOException e){ e.printStackTrace(); }
            finally{ connection.disconnect(); }

            return loginStatus;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()){
                //store current session id
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sessionId", result);
                editor.putInt("fromLogin", 1);
                editor.commit();

                //Toast.makeText(getBaseContext(), "A verification link has been sent to your email", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getBaseContext(), ActivityNavDrawer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else Toast.makeText(ActivityLogin.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
        }

        public String toJsonString() {//creates a new JSON string from stored movie data
            String returnJ = "";
            try{
                JSONObject js = new JSONObject();
                js.put("email", email.getText().toString());
                js.put("password", password.getText().toString());
                returnJ = js.toString();
                android.util.Log.w(this.getClass().getSimpleName(),returnJ);
            }
            catch (Exception ex){
                android.util.Log.w(this.getClass().getSimpleName(),
                        "error converting to/from json");
            }
            return returnJ;
        }
    }

    protected void hideKeyboard(View view)
    {
        view.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

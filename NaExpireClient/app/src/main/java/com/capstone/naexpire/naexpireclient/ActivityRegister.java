package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegister extends AppCompatActivity {
    private SharedPreferences sharedPref;

    private EditText firstName, lastName, email, password, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPref = getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        setTitle("Register"); //set activity title

        firstName = (EditText) findViewById(R.id.txtRegUserFirst);
        lastName = (EditText) findViewById(R.id.txtRegUserLast);
        email = (EditText) findViewById(R.id.txtRegUserEmail);
        password = (EditText) findViewById(R.id.txtRegUserPassword);
        confirmPass = (EditText) findViewById(R.id.txtRegUserPassword2);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layUserInfo);

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

    protected void hideKeyboard(View view)
    {
        view.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void clickSubmit(View view){
        //checks that all fields are filled
        Boolean ready = !firstName.getText().toString().isEmpty() &&
                !lastName.getText().toString().isEmpty() &&
                !email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !confirmPass.getText().toString().isEmpty();

        //checks that password is valid
        Boolean valid = isValidPassword(password.getText().toString());

        //checks that passwords match
        Boolean match = password.getText().toString().equals(confirmPass.getText().toString());

        //if all fields are filled, password is valid and both entered passwords match
        if(ready && valid && match){
            //put values in shared preferences
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("fromRegister", 1); //store a value to signify the user just finished registering
            editor.putString("firstName", firstName.getText().toString());
            editor.putString("lastName", lastName.getText().toString());
            editor.putString("email", email.getText().toString());
            editor.putString("password", password.getText().toString());
            editor.commit();

            //new register().execute("http://138.197.33.88/api/consumer/register/");

            Intent intent = new Intent(this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent); //return to login activity
        }
        else if(!ready) Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
        else if(!valid){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_password, null);
            Button gotIt = (Button) dialogView.findViewById(R.id.btnDismiss);

            dialogBuilder.setView(dialogView);
            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            hideKeyboard(view);

            gotIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        else Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
    }

    private class register extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... urls){
            String line = null;
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

                    android.util.Log.w(this.getClass().getSimpleName(),"Response Message: "+sb.toString());
                }
                else{
                    android.util.Log.w(this.getClass().getSimpleName(),"Response Message: "+connection.getResponseMessage());
                }
            }
            catch (MalformedURLException ex){ ex.printStackTrace(); }
            catch (IOException e){ e.printStackTrace(); }
            finally{ connection.disconnect(); }

            return line;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "A verification link has been sent to your email", Toast.LENGTH_LONG).show();
        }
    }

    public String toJsonString() {//creates a new JSON string from stored movie data
        String returnJ = "";
        try{
            JSONObject js = new JSONObject();
            js.put("firstName", firstName.getText().toString());
            js.put("lastName", lastName.getText().toString());
            js.put("email", email.getText().toString());
            js.put("password", password.getText().toString());
            js.put("personalPhoneNumber", "0123456789");
            returnJ = js.toString();
            android.util.Log.w(this.getClass().getSimpleName(),returnJ);
        }
        catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "error converting to/from json");
        }
        return returnJ;
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        //needs a cap, number, special char, and 8+ chars total
        final String PASSWORD_PATTERN = "((?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}

package com.capstone.naexpire.naexpireclient;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegUserInfo extends AppCompatActivity {
    private SharedPreferences sharedPref;

    EditText firstName, lastName, email, password, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_user_info);

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

            Toast.makeText(this, "A verification link has been sent to your email", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent); //return to login activity
        }
        else if(!ready) Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show();
        else if(!valid){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_valid_password, null);
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

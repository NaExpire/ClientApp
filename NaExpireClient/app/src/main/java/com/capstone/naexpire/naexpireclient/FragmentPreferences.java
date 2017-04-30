package com.capstone.naexpire.naexpireclient;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FragmentPreferences extends Fragment {
    private SharedPreferences sharedPref;

    public FragmentPreferences() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        sharedPref = getActivity().getSharedPreferences("com.capstone.naexpire.PREFERENCE_FILE_KEY",
                Context.MODE_PRIVATE);

        FragmentPreferences.this.getActivity().setTitle("Preferences"); //set title

        final EditText username = (EditText) view.findViewById(R.id.txtPrefUsername);
        final EditText email = (EditText) view.findViewById(R.id.txtPrefEmail);
        final EditText phone = (EditText) view.findViewById(R.id.txtPrefPhone);
        final EditText cardName = (EditText) view.findViewById(R.id.txtPrefCardName);
        final EditText cardNum = (EditText) view.findViewById(R.id.txtPrefCardNumber);
        final EditText cardCvv = (EditText) view.findViewById(R.id.txtPrefCVV);
        final EditText oldPass = (EditText) view.findViewById(R.id.txtPrefCurrentPassword);
        final EditText password = (EditText) view.findViewById(R.id.txtPrefPassword);
        final EditText cPassword = (EditText) view.findViewById(R.id.txtPrefPassword2);
        Button foods = (Button) view.findViewById(R.id.btnPrefFoods);
        Button save = (Button) view.findViewById(R.id.btnPrefSave);

        username.setText(sharedPref.getString("username", ""));
        email.setText(sharedPref.getString("email", ""));
        phone.setText(sharedPref.getString("phone", ""));

        //navigate to food types fragment when food types button is pressed
        foods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);

                FragmentEditFoodTypes fragmentEditFoodTypes = new FragmentEditFoodTypes();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, fragmentEditFoodTypes).commit();
            }
        });

        //save changes tapped
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", username.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.putString("phone", phone.getText().toString());

                //if any card fields are filled
                if(!cardName.getText().toString().isEmpty() || !cardNum.getText().toString().isEmpty() ||
                        !cardCvv.getText().toString().isEmpty()){
                    if(cardNum.getText().toString().length() != 16){ //enforce card # length is 16
                        Toast.makeText(FragmentPreferences.this.getContext(), "Invalid card number.", Toast.LENGTH_SHORT).show();
                    }
                    else if(cardCvv.getText().toString().length() < 3){ //enforce cvv length is > 2
                        Toast.makeText(FragmentPreferences.this.getContext(), "Invalid security code.", Toast.LENGTH_SHORT).show();
                    }
                    //enforce all fields are filled
                    else if(cardName.getText().toString().isEmpty() || cardNum.getText().toString().isEmpty() ||
                            cardCvv.getText().toString().isEmpty()){
                        Toast.makeText(FragmentPreferences.this.getContext(), "Fill all fields.", Toast.LENGTH_SHORT).show();
                    }
                    else{ //save values if everything is valid
                        editor.putString("cardName", cardName.getText().toString());
                        editor.putString("cardNumber", cardNum.getText().toString());
                        editor.putString("cardCvv", cardCvv.getText().toString());
                    }
                }
                //if any password fields are filled
                if(!oldPass.getText().toString().isEmpty() || !password.getText().toString().isEmpty() ||
                        !cPassword.getText().toString().isEmpty()){
                    //enforce current password is entered correctly
                    if(!sharedPref.getString("password","").equals(oldPass.getText().toString())){
                        Toast.makeText(FragmentPreferences.this.getContext(), "Current password incorrect.", Toast.LENGTH_SHORT).show();
                    }
                    //enforce new password is valid
                    else if(!isValidPassword(password.getText().toString())){
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FragmentPreferences.this.getContext());
                        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_password, null);
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
                    //save new password if all is valid
                    else if(password.getText().toString().equals(cPassword.getText().toString())){
                        editor.putString("password", password.getText().toString());
                    }
                    else Toast.makeText(FragmentPreferences.this.getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
                editor.commit();

                //update restaurant name in the navigation drawer
                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                View header = navigationView.getHeaderView(0);
                TextView detail = (TextView) header.findViewById(R.id.lblDrawerEmail);

                detail.setText(sharedPref.getString("email", ""));

                Toast.makeText(FragmentPreferences.this.getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
            }
        });

        //logic to hide soft-keyboard if user taps outside of edit texts
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layPreferences);
        RelativeLayout layout2 = (RelativeLayout) view.findViewById(R.id.layPrefsScroll);

        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

        layout2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hideKeyboard(view);
                //return false;
            }
        });

        return view;
    }

    protected void hideKeyboard(View view)
    {
        view.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

package com.stfo.carddrop.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stfo.carddrop.R;

/**
 * Created by Kartik on 10/15/2017.
 */

public class SignUpDetails extends Activity {

    private EditText etName;
    private EditText etDescription;
    private EditText etPassword;
    private EditText etPhone;
    private Button buttonSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_details);
        init();
    }

    private void init() {
        etName = (EditText) findViewById(R.id.et_Name);
        etDescription = (EditText) findViewById(R.id.et_Description);
        etPhone = (EditText) findViewById(R.id.et_PhoneNumber);
        etPassword = (EditText) findViewById(R.id.et_Password);
        buttonSignUp = (Button) findViewById(R.id.button_SignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDetailsAndSignUp();
            }
        });
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkDetailsAndSignUp();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void checkDetailsAndSignUp() {

    }
}

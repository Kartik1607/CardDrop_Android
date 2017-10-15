package com.stfo.carddrop.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.stfo.carddrop.R;
import com.stfo.carddrop.utils.Constants;
import com.stfo.carddrop.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Kartik on 10/8/2017.
 */

public class LoginActivity extends Activity {

    private EditText et_PhoneNumber;
    private EditText et_Password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        et_PhoneNumber = (EditText) findViewById(R.id.et_PhoneNumber);
        et_Password = (EditText) findViewById(R.id.et_Password);


        et_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginUser();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void loginUser() {
        String phoneNumber = et_PhoneNumber.getText().toString();
        String password = et_Password.getText().toString();
        boolean checkPhone = true;
        boolean checkPassword = true;
        if(phoneNumber.trim().length() == 0) {
            checkPhone = false;
        }
        if(password.trim().length() < 6) {
            checkPassword = false;
        }

        if(!(checkPassword && checkPhone)) {
            return;
        }

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("phone", phoneNumber.trim());
            requestObject.put("password", password.trim());
        } catch (JSONException e) {
            Log.e(LoginActivity.class.toString(), e.getStackTrace().toString());
        }

        VolleySingleton volley = VolleySingleton.getInstance(this);
        Uri buildUri = Uri.parse(Constants.API_URL).buildUpon()
                        .appendPath("users").appendPath("login").build();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, buildUri.toString(),
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LoginActivity.class.toString(), response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        volley.addToRequestQueue(request);

    }

    public void onClickSignUp(View v) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void onClickLogin(View v) {
        loginUser();
    }
}

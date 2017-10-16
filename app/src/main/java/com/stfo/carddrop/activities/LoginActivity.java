package com.stfo.carddrop.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.stfo.carddrop.R;
import com.stfo.carddrop.models.User;
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
    private View content;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        et_PhoneNumber = (EditText) findViewById(R.id.et_PhoneNumber);
        et_Password = (EditText) findViewById(R.id.et_Password);
        content = findViewById(android.R.id.content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
            et_PhoneNumber.setError("Phone number is empty.");
            checkPhone = false;
        }
        if(password.trim().length() < 6) {
            et_Password.setError("Minimum 6 characters needed.");
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
                        String status = parseJson(response);
                        progressBar.setVisibility(View.GONE);
                        showLoginResult(status, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        progressBar.setVisibility(View.VISIBLE);
        volley.addToRequestQueue(request);

    }

    private String parseJson(final JSONObject json) {
        String status = null;
        try {
            status = json.getString(Constants.API_RESPONSE_STATUS);
        } catch (JSONException e) {
            Log.e(LoginActivity.class.toString(), e.getStackTrace().toString());
        }
        return status;
    }

    private void showLoginResult(final String status, final JSONObject fullResponse) {

        switch (status) {
            case Constants.NOT_REGISTERED :
                Snackbar.make(content, "Seems you are not registerd. Sign up?", Snackbar.LENGTH_LONG)
                        .setAction("SIGN UP", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickSignUp(null);
                            }
                        }).show();
                break;
            case Constants.INVALID_PASSWORD :
                Snackbar.make(content, "Incorrect Password", Snackbar.LENGTH_LONG).show();
                break;
            case Constants.SUCCESS_LOGIN :
                try {
                    User user = Constants.parseUser(fullResponse.getJSONObject("user"));
                    SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_User),MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.preference_User_ID), "NA");
                    editor.commit();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }catch (Exception e){

                }
                break;
        }
    }

    public void onClickSignUp(View v) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void onClickLogin(View v) {
        loginUser();
    }
}

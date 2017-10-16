package com.stfo.carddrop.activities;

import android.app.Activity;
import android.content.Context;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.stfo.carddrop.R;
import com.stfo.carddrop.models.User;
import com.stfo.carddrop.utils.Constants;
import com.stfo.carddrop.utils.VolleySingleton;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Created by Kartik on 10/15/2017.
 */

public class SignUpDetails extends Activity {

    private EditText etName;
    private EditText etDescription;
    private EditText etPassword;
    private EditText etPhone;
    private Button buttonSignUp;
    private View content;
    private ProgressBar progressBar;
    private Context context;

    private String imagePath;
    private String imageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_details);
        imagePath = getIntent().getStringExtra(Constants.INTENT_IMAGE_PATH);
        context = this;
        init();
    }

    private void init() {
        etName = (EditText) findViewById(R.id.et_Name);
        etDescription = (EditText) findViewById(R.id.et_Description);
        etPhone = (EditText) findViewById(R.id.et_PhoneNumber);
        etPassword = (EditText) findViewById(R.id.et_Password);
        buttonSignUp = (Button) findViewById(R.id.button_SignUp);
        content = findViewById(android.R.id.content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
        boolean checkName = true;
        boolean checkDescription = true;
        boolean checkPhone = true;
        boolean checkPassword = true;

        String name = etName.getText().toString().trim();
        if(name.length() == 0) {
            checkName = false;
            etName.setError("Name cannot be empty.");
        }

        if(name.length() >= 20) {
            checkName = false;
            etName.setError("Name cannot be longer than 20 characters.");
        }

        String description = etDescription.getText().toString().trim();

        if(description.length() >= 50) {
            checkDescription = false;
            etDescription.setError("Description cannot be longer than 50 characters.");
        }

        String phone = etPhone.getText().toString().trim();

        if(phone.length() == 0) {
            checkPhone = false;
            etPhone.setError("Mobile number can not be empty.");
        }

        String password = etPassword.getText().toString();

        if(password.length() < 6) {
            checkPassword = false;
            etPassword.setError("Password must be at least 6 characters.");
        }

        if(!(checkName && checkDescription && checkPassword && checkPhone)) {
           return;
        }

        registerUser();
    }

    private void registerUser() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("phone", phone);
            requestObject.put("password", password);
        } catch (JSONException e) {
            Log.e(LoginActivity.class.toString(), e.getStackTrace().toString());
        }

        VolleySingleton volley = VolleySingleton.getInstance(this);
        Uri buildUri = Uri.parse(Constants.API_URL).buildUpon()
                .appendPath("users").appendPath("register").build();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, buildUri.toString(),
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString(Constants.API_RESPONSE_STATUS);
                            switch (status) {
                                case Constants.ALREADY_REGISTERED :
                                    Snackbar.make(content, "User already exists with this mobile number", Snackbar.LENGTH_LONG).show();
                                    progressBar.setProgress(View.GONE);
                                    break;
                                case Constants.NEW_REGISTRATION :
                                    User user = Constants.parseUser(response.getJSONObject("user"));
                                    SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_User), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(getString(R.string.preference_User_ID), user.getId());
                                    editor.commit();
                                    uploadImage();
                            }
                        } catch (JSONException e ) {
                            Log.e(SignUpDetails.class.toString(), e.getStackTrace().toString());
                        }
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

    private void uploadImage() {
        String path = imagePath;
        final Uri serverPath = Uri.parse(Constants.API_URL).buildUpon()
                .appendPath("images").build();

        try {
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this, uploadId, serverPath.toString())
                    .addFileToUpload(path, "file")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                            Log.e("MY_APP", exception.getStackTrace().toString());
                            Snackbar.make(content, "Something went wrong", Snackbar.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            imageName = serverResponse.getBodyAsString();
                            updateUser();
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload();

        } catch (FileNotFoundException | MalformedURLException e) {
            Log.e(SignUpDetails.class.toString(), e.getStackTrace().toString());
        }
    }

    private void updateUser() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_User), MODE_PRIVATE);
        String userId = preferences.getString(getString(R.string.preference_User_ID), "NA");
        if(userId.equalsIgnoreCase("NA")) {
            Snackbar.make(content, "Something went wrong", Snackbar.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();


        final JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("name", name);
            requestObject.put("detail", description);
            requestObject.put("cardImageId", imageName);
            requestObject.put("phone",etPhone.getText().toString());
        } catch (JSONException e) {
            Log.e(SignUpDetails.class.toString(), e.getStackTrace().toString());
        }

        VolleySingleton volley = VolleySingleton.getInstance(this);
        Uri buildUri = Uri.parse(Constants.API_URL).buildUpon()
                .appendPath("users").appendPath(userId).build();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, buildUri.toString(),
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MY_APP", response.toString());
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(context, HomeActivity.class));
                        finish();
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
}

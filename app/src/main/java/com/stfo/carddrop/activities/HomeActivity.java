package com.stfo.carddrop.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.stfo.carddrop.R;
import com.stfo.carddrop.utils.Constants;
import com.stfo.carddrop.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kartik on 10/8/2017.
 */

public class HomeActivity extends Activity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private FloatingActionButton buttonFindNearby;
    private FloatingActionButton buttonDropCard;
    private View content;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String userId;

    private final int ACCESS_LOCATION_PERMISSION_DROP = 0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        initUserId();
    }

    private void initUserId() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_User), MODE_PRIVATE);
        userId = preferences.getString(getString(R.string.preference_User_ID),"NA");
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        buttonFindNearby = (FloatingActionButton) findViewById(R.id.button_FindNearby);
        buttonDropCard = (FloatingActionButton) findViewById(R.id.button_dropCard);
        buttonDropCard.setOnClickListener(this);
        buttonFindNearby.setOnClickListener(this);
        content = findViewById(android.R.id.content);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_dropCard) {
            dropCard();
        }
    }

    private void dropCard() {

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(locationPermission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                createDroppedCard(location);
                            } else {
                                Snackbar.make(content,"Something went wrong", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_LOCATION_PERMISSION_DROP);
        }
    }

    private void createDroppedCard(Location location) {

        final JSONObject requestObject = new JSONObject();

        try{
            requestObject.put("userId", userId);
            JSONArray locationArray = new JSONArray();
            locationArray.put(location.getLongitude());
            locationArray.put(location.getLatitude());
            requestObject.put("location", locationArray);
        } catch (JSONException e) {

        }


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Choose drop duration");
                alertDialogBuilder.setPositiveButton("5 min",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    requestObject.put("expire_code", 0);
                                } catch (JSONException e) {

                                }
                                saveCard(requestObject);

                            }
                        });

                alertDialogBuilder.setNegativeButton("1 Year",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    requestObject.put("expire_code", 1);
                                } catch (JSONException e) {

                                }

                                saveCard(requestObject);
                            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void saveCard(JSONObject requestObject) {
        VolleySingleton volley = VolleySingleton.getInstance(this);
        Uri buildUri = Uri.parse(Constants.API_URL).buildUpon()
                .appendPath("drops").build();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, buildUri.toString(),
                requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Snackbar.make(content, "Drop Successful", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(content, "Something went wrong", Snackbar.LENGTH_LONG).show();
                    }
                }
        );

        volley.addToRequestQueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCESS_LOCATION_PERMISSION_DROP) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dropCard();
            }
            else {
                Snackbar.make(content, "Permission required to access location.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dropCard();
                            }
                        }).show();

            }
            return;
        }
    }
}

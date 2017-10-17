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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.stfo.carddrop.R;
import com.stfo.carddrop.adapters.CardAdapter;
import com.stfo.carddrop.models.User;
import com.stfo.carddrop.utils.Constants;
import com.stfo.carddrop.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik on 10/17/2017.
 */

public class NearbyCards extends Activity{

    private RecyclerView recyclerView;
    private CardAdapter recylerViewAdapter;
    private View content;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String userId;

    private final int ACCESS_LOCATION_PERMISSION_DROP = 0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        initUserId();
        findCards();
    }


    private List<User> parseJSON(JSONArray object) {
        List<User> data = new ArrayList<>();
        try {
            for (int i = 0; i < object.length(); ++i) {
                JSONObject current = object.getJSONObject(i).getJSONObject("user");
                data.add(Constants.parseUser(current));
            }
        }catch (JSONException e) {}
        return data;
    }

    private void initUserId() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_User), MODE_PRIVATE);
        userId = preferences.getString(getString(R.string.preference_User_ID),"NA");
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        content = findViewById(android.R.id.content);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recylerViewAdapter = new CardAdapter(this, new ArrayList<User>(),2);
        recyclerView.setAdapter(recylerViewAdapter);
    }


    private void findCards() {

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(locationPermission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                findNearByCards(location);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCESS_LOCATION_PERMISSION_DROP) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findCards();
            }
            else {
                Snackbar.make(content, "Permission required to access location.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                findCards();
                            }
                        }).show();

            }
            return;
        }
    }

    private void findNearByCards(Location location) {
        VolleySingleton volley = VolleySingleton.getInstance(this);
        Uri buildUri = Uri.parse(Constants.API_URL).buildUpon()
                .appendPath("drops")
                .appendQueryParameter("lat", location.getLatitude() + "")
                .appendQueryParameter("long", location.getLongitude() + "")
                .appendQueryParameter("radius", "500").build();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, buildUri.toString(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        recylerViewAdapter.setData(parseJSON(response));
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
}

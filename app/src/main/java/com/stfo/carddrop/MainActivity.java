package com.stfo.carddrop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stfo.carddrop.Utils.Constants;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_Login),
                Context.MODE_PRIVATE
        );
        isLoggedIn = sharedPreferences.getBoolean(
                getString(R.string.preference_Login_Status),
                false
        );
        if(isLoggedIn) {

        } else {

        }
    }
}

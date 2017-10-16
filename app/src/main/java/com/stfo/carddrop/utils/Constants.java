package com.stfo.carddrop.utils;

import com.stfo.carddrop.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kartik on 10/8/2017.
 */

public class Constants {
    public static final String API_URL = "http://carddrop.herokuapp.com/api/";

    public static final String API_RESPONSE_STATUS = "status";

    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String SUCCESS_LOGIN = "SUCCESS_LOGIN";
    public static final String NEW_REGISTRATION = "NEW_REGISTRATION";
    public static final String NOT_REGISTERED = "NOT_REGISTERED";
    public static final String ALREADY_REGISTERED = "ALREADY_REGISTERED";

    public static  final String INTENT_IMAGE_PATH = "INTENT_IMAGE_PATH";

    public static User parseUser(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setId(jsonObject.getString("id"));
            user.setName(jsonObject.getString("name"));
            user.setDetail(jsonObject.getString("detail"));
            user.setCardImageId(jsonObject.getString("cardImageId"));
            user.setPhone(jsonObject.getLong("phone"));
        } catch (JSONException e) {

        }
        return  user;
    }

}

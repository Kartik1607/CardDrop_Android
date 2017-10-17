package com.stfo.carddrop.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.stfo.carddrop.R;
import com.stfo.carddrop.models.User;
import com.stfo.carddrop.utils.Constants;
import com.stfo.carddrop.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Kartik on 10/16/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<User> data;
    private Context context;
    private LayoutInflater inflater;
    private int type;
    private String userId;

    public CardAdapter(Context context, List<User> data, int type) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        this.type = type;
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.preference_User), Context.MODE_PRIVATE
        );
        userId = preferences.getString(
                context.getString(R.string.preference_User_ID),"NA"
        );
    }

    public void setData(List<User> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int id;
        if(type == 1) {
            id = R.layout.layout_card;
        }else{
            id = R.layout.layout_nearby;
        }
        return new CardViewHolder(inflater.inflate(id, parent, false));
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName, tvDetail;
        private ImageView imageView;
        private Button button_PickCard;

        public CardViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_Name);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_Description);
            imageView = (ImageView) itemView.findViewById(R.id.iv_Card);
            if(type != 1) {
                button_PickCard = (Button) itemView.findViewById(R.id.button_pick);
                button_PickCard.setOnClickListener(this);
            }
        }
        public void bind(int position) {
            User current = data.get(position);
            tvName.setText(current.getName());
            tvDetail.setText(current.getDetail());
            Uri imageUri = Uri.parse(Constants.API_URL).buildUpon().appendPath("images")
                    .appendPath(current.getCardImageId()).build();
            Log.d("MY_APP", imageUri.toString());
            Glide.with(context).load(imageUri.toString()).centerCrop().into(imageView);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            User cardUser = data.get(position);
            VolleySingleton volley = VolleySingleton.getInstance(context);
            Uri buildUri = Uri.parse(Constants.API_URL).buildUpon()
                    .appendPath("pickedCards").build();
            JSONObject requestObject = new JSONObject();
            try{
                requestObject.put("pickerId", userId);
                requestObject.put("dropperId", cardUser.getId());
            }catch (JSONException e) {

            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, buildUri.toString(),
                    requestObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Card Picked Up!", Toast.LENGTH_LONG).show();
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

}

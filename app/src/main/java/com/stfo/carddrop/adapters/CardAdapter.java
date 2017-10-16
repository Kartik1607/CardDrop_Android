package com.stfo.carddrop.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stfo.carddrop.R;
import com.stfo.carddrop.models.User;
import com.stfo.carddrop.utils.Constants;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Kartik on 10/16/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<User> data;
    private Context context;
    private LayoutInflater inflater;

    public CardAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<User> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewHolder(inflater.inflate(R.layout.layout_card, parent, false));
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDetail;
        private ImageView imageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_Name);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_Description);
            imageView = (ImageView) itemView.findViewById(R.id.iv_Card);
        }
        public void bind(int position) {
            User current = data.get(position);
            tvName.setText(current.getName());
            tvDetail.setText(current.getDetail());
            Uri imageUri = Uri.parse(Constants.API_URL).buildUpon().appendPath("images")
                    .appendPath(current.getCardImageId()).build();
            Glide.with(context).load(imageUri).centerCrop().into(imageView);
        }

    }

}

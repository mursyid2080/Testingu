package com.example.testingu.RVNearMeRestaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingu.R;
import com.example.testingu.RVDirectoryRestaurant.AdapterRestaurant;
import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.example.testingu.RestaurantDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterNearMe extends RecyclerView.Adapter<AdapterNearMe.HolderNearMe>{

    private Context context;
    public ArrayList<ModelNearMe> restaurantsList;

    public AdapterNearMe(Context context,
                             ArrayList<ModelNearMe> restaurantsList) {
        this.context = context;
        this.restaurantsList = restaurantsList;
    }

    @NonNull
    @Override
    public HolderNearMe onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row
        View view= LayoutInflater.from(context).inflate(R.layout.row_restaurant_near_me, parent,false);
        return new HolderNearMe(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNearMe.HolderNearMe holder, int position) {
        //get data
        ModelNearMe modelNearMe= restaurantsList.get(position);
        String address=modelNearMe.getAddress();
        String latitude=modelNearMe.getLatitude();
        String longitude=modelNearMe.getLongitude();
        String name=modelNearMe.getRestaurantName();
        String id=modelNearMe.getId();
        String restaurantOpen=modelNearMe.getRestaurantOpen();
        String image=modelNearMe.getImage();
        String distance=modelNearMe.getDistance();


        //set data
        holder.nearMeNameTv.setText(name);
        holder.nearMeAddressTv.setText(address);
        holder.distanceTv.setText(distance);
        holder.ratingBar.setRating((float) 3.50);

        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_store_mall_directory_24).into(holder.nearMeIv);
        }catch (Exception e){
            holder.nearMeIv.setImageResource(R.drawable.ic_baseline_store_mall_directory_24);
        }
        //handle click, go restaurant details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(context, RestaurantDetails.class);
                Bundle bundle=new Bundle();
                bundle.putString("filter", id);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantsList.size();
    }

    //view holder

    class HolderNearMe extends RecyclerView.ViewHolder{


        private ImageView nearMeIv;
        private TextView restaurantClosedTv, nearMeNameTv, nearMeAddressTv, distanceTv;
        private RatingBar ratingBar;

        public HolderNearMe(@NonNull View itemView) {
            super(itemView);

            nearMeIv=itemView.findViewById(R.id.nearMeIv);
//            restaurantClosedTv=itemView.findViewById(R.id.restaurantClosedTv);
            nearMeNameTv=itemView.findViewById(R.id.nearMeNameTv);
            nearMeAddressTv=itemView.findViewById(R.id.nearMeAddressTv);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            distanceTv=itemView.findViewById(R.id.distanceTv);

        }
    }

}

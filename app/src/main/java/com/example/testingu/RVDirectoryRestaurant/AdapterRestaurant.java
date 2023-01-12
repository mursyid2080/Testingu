package com.example.testingu.RVDirectoryRestaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingu.FilterMenu;
import com.example.testingu.R;
//import com.example.testingu.RestaurantDetails;
import com.example.testingu.RestaurantDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRestaurant extends RecyclerView.Adapter<AdapterRestaurant.HolderRestaurant>  {

    private Context context;
    public ArrayList<ModelRestaurant> restaurantsList;



    public AdapterRestaurant(Context context,
                             ArrayList<ModelRestaurant> restaurantsList) {
        this.context = context;
        this.restaurantsList = restaurantsList;

    }

    @NonNull
    @Override
    public HolderRestaurant onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row
        View view= LayoutInflater.from(context).inflate(R.layout.row_restaurant_directory, parent,false);
        return new HolderRestaurant(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRestaurant holder, int position) {
        //get data
        ModelRestaurant modelRestaurant= restaurantsList.get(position);
        String address=modelRestaurant.getAddress();
        String latitude=modelRestaurant.getLatitude();
        String longitude=modelRestaurant.getLongitude();
        String name=modelRestaurant.getRestaurantName();
        String id=modelRestaurant.getId();
        String restaurantOpen=modelRestaurant.getRestaurantOpen();
        String image=modelRestaurant.getImage();



        //set data
        holder.restaurantNameTv.setText(name);
        holder.restaurantAddressTv.setText(address);
        holder.ratingBar.setRating((float) 3.50);
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_store_mall_directory_24).into(holder.restaurantIv);
        }catch (Exception e){
            holder.restaurantIv.setImageResource(R.drawable.ic_baseline_store_mall_directory_24);
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

        return restaurantsList==null ? 0:restaurantsList.size();
    }



    //view holder

    class HolderRestaurant extends RecyclerView.ViewHolder{


        private ImageView restaurantIv;
        private TextView restaurantClosedTv, restaurantNameTv, restaurantAddressTv;
        private RatingBar ratingBar;

        public HolderRestaurant(@NonNull View itemView) {
            super(itemView);

            restaurantIv=itemView.findViewById(R.id.nearMeIv);
//            restaurantClosedTv=itemView.findViewById(R.id.restaurantClosedTv);
            restaurantNameTv=itemView.findViewById(R.id.nearMeNameTv);
            restaurantAddressTv=itemView.findViewById(R.id.nearMeAddressTv);
            ratingBar=itemView.findViewById(R.id.ratingBar);

        }
    }
}

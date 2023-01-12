package com.example.testingu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Directory extends AppCompatActivity {

    private TextView cuisineTv, KKTv;
    private RelativeLayout cuisineRl, KKRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        cuisineTv=findViewById(R.id.cuisineTv);
        KKTv=findViewById(R.id.KKTv);
        cuisineRl=findViewById(R.id.cuisineRl);
        KKRl=findViewById(R.id.KKRl);

        showCuisineUI();

        cuisineTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCuisineUI();
            }
        });
        KKTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showKKUI();
            }
        });
    }

    private void showCuisineUI(){
        cuisineRl.setVisibility(View.VISIBLE);
        KKRl.setVisibility(View.GONE);

        cuisineTv.setTextColor(getResources().getColor(R.color.black));
        cuisineTv.setBackgroundResource(R.drawable.container_bg);

        KKTv.setTextColor(getResources().getColor(R.color.gray));
        KKTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private void showKKUI(){
        cuisineRl.setVisibility(View.GONE);
        KKRl.setVisibility(View.VISIBLE);

        KKTv.setTextColor(getResources().getColor(R.color.black));
        KKTv.setBackgroundResource(R.drawable.container_bg);

        cuisineTv.setTextColor(getResources().getColor(R.color.gray));
        cuisineTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    public void filteredRestaurantRV(View v){
        //attach tag then go to restaurant recyclerview activity
        String value= (String) v.getTag();
        Intent i = new Intent(getApplicationContext(), RestaurantRV.class);
        Bundle bundle=new Bundle();
        bundle.putString("filter", value);
        i.putExtras(bundle);
        startActivity(i);

    }
}
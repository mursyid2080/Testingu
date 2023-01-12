package com.example.testingu;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingu.RVDirectoryRestaurant.AdapterRestaurant;
import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.example.testingu.RVDirectoryRestaurant.AdapterRestaurant;
import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantRV extends AppCompatActivity {

    FirebaseFirestore fStore;
    private RecyclerView restaurantRv;

    private ArrayList<ModelRestaurant> restaurantList, filteredList;
    private AdapterRestaurant adapterRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_rv);
        restaurantRv=findViewById(R.id.recyclerView);



//        nameTv=findViewById(R.id.restaurantNameTv);
        Bundle bundle=getIntent().getExtras();
        String filter=bundle.getString("filter");
        loadRestaurant(filter);



    }
    private void loadRestaurant(String filter){
        restaurantList=new ArrayList<>();
        filteredList=new ArrayList<>();
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("restaurants").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()){
                            Log.d("TAG", "onSuccess list empty");
                            return;
                        }else{
                            List<ModelRestaurant> types=queryDocumentSnapshots.toObjects(ModelRestaurant.class);
                            restaurantList.addAll(types);
                            for(int i=0; i<restaurantList.size(); i++){
                                if(restaurantList.get(i).getKk().equals(filter) ||
                                    restaurantList.get(i).getCategory().toLowerCase(Locale.ROOT).contains(filter)){

                                    filteredList.add(restaurantList.get(i));
                                }
                            }
                            Log.d("TAG", "onSuccess"+restaurantList);
                            //setup adapter
                            adapterRestaurant=new AdapterRestaurant(getApplicationContext(), filteredList);
                            //set adapter to recyclerview
                            restaurantRv.setAdapter(adapterRestaurant);
                        }
                    }
                });
    }

}
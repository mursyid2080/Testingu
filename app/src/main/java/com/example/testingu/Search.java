package com.example.testingu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.example.testingu.RVDirectoryRestaurant.AdapterRestaurant;
import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.example.testingu.RVMenu.AdapterMenu;
import com.example.testingu.RVMenu.ModelMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Search extends AppCompatActivity {

    private EditText searchEt;
    private RecyclerView searchRv;

    private ArrayList<ModelRestaurant> restaurantList, filteredList;
    private ModelRestaurant modelRestaurant;
    private AdapterRestaurant adapterRestaurant;

    private ArrayList<ModelMenu> menuList, filteredMenuList;
    private ModelMenu modelMenu;
    private AdapterMenu adapterMenu;

    FirebaseFirestore fStore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        searchEt=findViewById(R.id.searchEt);
        searchRv=findViewById(R.id.searchRv);

        //load
        loadRestaurants();
        loadMenus();

        //search
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filteredList=new ArrayList<>();
                filteredList.clear();
                //setup adapter
                adapterRestaurant=new AdapterRestaurant(getApplicationContext(), filteredList);
                //set adapter to recyclerview
                searchRv.setAdapter(adapterRestaurant);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    filteredList=new ArrayList<>();
                    //filter restaurant here
                    for(int k=0; k<restaurantList.size(); k++){
                        if(restaurantList.get(k).getRestaurantName().toLowerCase(Locale.ROOT).contains(charSequence)){
                            filteredList.add(restaurantList.get(k));
                        }
                    }
                    //filter menu here
                    for(int l=0; l<menuList.size(); l++){
                        if(menuList.get(l).getMenuName().toLowerCase(Locale.ROOT).contains(charSequence)){
                            String find=menuList.get(l).getMenuId();
                            for (int f=0; f<restaurantList.size(); f++){
                                if(find.equals(restaurantList.get(f).getId())){
                                    if(!filteredList.contains(restaurantList.get(f))){
                                        filteredList.add(restaurantList.get(f));
                                    }
                                }
                            }
                        }
                    }
                    //setup adapter
                    adapterRestaurant=new AdapterRestaurant(getApplicationContext(), filteredList);
                    //set adapter to recyclerview
                    searchRv.setAdapter(adapterRestaurant);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void loadRestaurants() {
        restaurantList= new ArrayList<>();
        modelRestaurant=new ModelRestaurant();
        fStore= FirebaseFirestore.getInstance();
        fStore.collection("restaurants").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("TAG", "onSuccess list empty");
                    return;
                }
                else{
                    //get restaurant list
                    List<ModelRestaurant> types=queryDocumentSnapshots.toObjects(ModelRestaurant.class);
                    restaurantList.addAll(types);
                    adapterRestaurant=new AdapterRestaurant(getApplicationContext(), restaurantList);
                }
            }
        });
    }
    private void loadMenus() {
        menuList = new ArrayList<>();
        filteredMenuList = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("menu").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("TAG", "onSuccess list empty");
                    return;
                } else {
                    List<ModelMenu> types = queryDocumentSnapshots.toObjects(ModelMenu.class);
                    menuList.addAll(types);
                    adapterMenu=new AdapterMenu(getApplicationContext(), menuList);
                }

            }
        });
    }
}
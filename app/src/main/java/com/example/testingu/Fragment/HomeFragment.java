package com.example.testingu.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.testingu.Directory;
import com.example.testingu.NearMe;
import com.example.testingu.R;
import com.example.testingu.RVDirectoryRestaurant.AdapterRestaurant;
import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.example.testingu.Search;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton directoryBtn;

    FirebaseFirestore fStore;
    private RecyclerView homeRv;

    private ArrayList<ModelRestaurant> restaurantList;
    private AdapterRestaurant adapterRestaurant;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        homeRv=view.findViewById(R.id.homeRv);

        //directory button
        ImageButton directoryBtn=view.findViewById(R.id.directoryBtn);
        directoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Directory.class));
            }
        });
        //near me button
        ImageButton nearMeBtn=view.findViewById(R.id.nearMeBtn);
        nearMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NearMe.class));
            }
        });
        //search button
        ImageView searchBtn=view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Search.class));
            }
        });


        loadRestaurant();
        return view;


    }
    private void loadRestaurant(){
        restaurantList=new ArrayList<>();

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

                            Log.d("TAG", "onSuccess"+restaurantList);
                            //setup adapter
                            adapterRestaurant=new AdapterRestaurant(getActivity(), restaurantList);
                            //set adapter to recyclerview
                            homeRv.setAdapter(adapterRestaurant);
                        }
                    }
                });
    }
}
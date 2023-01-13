package com.example.testingu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.example.testingu.RVMenu.AdapterMenu;
import com.example.testingu.RVMenu.ModelMenu;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

public class RestaurantDetails extends AppCompatActivity {

    private ImageView restaurantIv;
    private TextView restaurantNameTv, restaurantAddressTv;
    ImageButton mapBtn, favBtn, filterMenuBtn;
    private EditText searchMenuEt;
    private RecyclerView menuRv;
    private RatingBar rating;

    private String restaurantId, userID;
    String myLatitude,myLongitude;
    String restaurantLatitude,restaurantLongitude;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    private ArrayList<ModelRestaurant> restaurantList;
    private ModelRestaurant restaurant;

    private ArrayList<ModelMenu> menuList, filteredMenuList;
    private AdapterMenu adapterMenu;

    boolean isInMyFavorite=false;

    //for location user
    int j=1;
    private static final int REQUEST_CHECK_SETTINGS=100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS=500;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS=500;
    private static final String TAG=RestaurantDetails.class.getSimpleName();

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates=false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        fAuth=FirebaseAuth.getInstance();

        //init ui views
        restaurantIv=findViewById(R.id.nearMeIv);
        restaurantNameTv=findViewById(R.id.restaurantDetailsNameTv);
        restaurantAddressTv=findViewById(R.id.restaurantDetailsAddressTv);
        mapBtn=findViewById(R.id.mapBtn);
        favBtn=findViewById(R.id.favBtn);
        rating=findViewById(R.id.detailsRatingBar);

        searchMenuEt=findViewById(R.id.searchMenuEt);
        menuRv=findViewById(R.id.menuRv);

        //get restaurant id
        Bundle bundle=getIntent().getExtras();
        String filter =bundle.getString("filter");

//        checkIsFavorite();
        loadRestaurantDetails(filter);
        loadRestaurantMenus(filter);

        searchMenuEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapterMenu.getFilter().filter(charSequence);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



//        favBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isInMyFavorite){
//                    //in favorite, remove from list
//                    removeFromFavorite(getApplicationContext(), filter);
//                }
//                else{
//                    //not favorite, add to favorite
//                    addToFavorite(getApplicationContext(), filter);
//                }
//            }
//        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog dialog=new ProgressDialog(RestaurantDetails.this);
                dialog.setMessage("Getting user location");
                dialog.setCancelable(false);
                dialog.setInverseBackgroundForced(false);
                dialog.show();



                mFusedLocationClient= LocationServices.getFusedLocationProviderClient(RestaurantDetails.this);
                mSettingClient=LocationServices.getSettingsClient(RestaurantDetails.this);

                mLocationCallback=new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        mCurrentLocation=locationResult.getLastLocation();

                        myLatitude= String.valueOf(mCurrentLocation.getLatitude());
                        myLongitude= String.valueOf(mCurrentLocation.getLongitude());

                        if(j==1){
                            openMap();
                            dialog.dismiss();
                        }
                        j=0;


                    }
                };
                mLocationRequest=LocationRequest.create()
                        .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                        .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);
                mLocationSettingsRequest=builder.build();

                Dexter.withActivity(RestaurantDetails.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                mRequestingLocationUpdates=true;
                                startLocationUpdates();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if(response.isPermanentlyDenied()){
                                    openSettings();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();


            }
        });
    }

    private void checkIsFavorite(){
        FirebaseAuth fAuth=FirebaseAuth.getInstance();
        fAuth.getCurrentUser();
        FirebaseFirestore fStore=FirebaseFirestore.getInstance();

        fStore.collection("users").document(fAuth.getUid())
                .collection("favorite").document(restaurantId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        isInMyFavorite=task.isSuccessful();
                        if(isInMyFavorite){
                            //exist
                            favBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
                        }
                        else{
                            //not exist
                            favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                    }
                });


//        DocumentReference document= (DocumentReference) fStore.collection("users").document(fAuth.getCurrentUser().getUid())
//                .collection("favorite").document(restaurantId)
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        isInMyFavorite=value.exists();
//                        if(isInMyFavorite){
//                            //exist
//                            favBtn.setImageResource(R.drawable.ic_baseline_favorite_24);
//                        }
//                        else{
//                            //not exist
//                            favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
//                        }
//
//                    }
//                });
    }

    public static void removeFromFavorite(Context context, String restaurantId){

        FirebaseAuth fAuth=FirebaseAuth.getInstance();
        FirebaseFirestore fStore=FirebaseFirestore.getInstance();

        DocumentReference document=fStore.collection("users").document(fAuth.getUid()).collection("favorite").document(restaurantId);

        //put on hashmap

        document.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Removed from favorite list", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to remove due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static void addToFavorite(Context context, String restaurantId){

        FirebaseAuth fAuth=FirebaseAuth.getInstance();
        FirebaseFirestore fStore=FirebaseFirestore.getInstance();

        DocumentReference document=fStore.collection("users").document(fAuth.getUid()).collection("favorite").document(restaurantId);
        //put on hashmap
        HashMap<String, Object> user=new HashMap<>();
        user.put("restaurantId", restaurantId);
        document.set(user).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(context, "Added to favorite list", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onSuccess: added to favorite"+restaurantId);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: "+e.toString());
            }
        });


    }

    private void loadRestaurantDetails(String filterKey) {
        String filter=filterKey;
        restaurantList= new ArrayList<>();
        restaurant=new ModelRestaurant();
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("restaurants").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("TAG", "onSuccess list empty");
                    return;
                }
                else{
                    List<ModelRestaurant> types=queryDocumentSnapshots.toObjects(ModelRestaurant.class);
                    restaurantList.addAll(types);
                    for (int i=0; i<restaurantList.size(); i++){
                        if(restaurantList.get(i).getId().equals(filter)){
                            restaurant=restaurantList.get(i);
                        }
                    }

                }
                String restaurantName=restaurant.getRestaurantName();
                String restaurantId=restaurant.getId();
                String restaurantAddress=restaurant.getAddress();
                String image=restaurant.getImage();
                restaurantLatitude=restaurant.getLatitude();
                restaurantLongitude=restaurant.getLongitude();

                rating.setRating((float)3.5);

                restaurantNameTv.setText(restaurantName);
                restaurantAddressTv.setText(restaurantAddress);
                try{
                    Picasso.get().load(image).placeholder(R.drawable.ic_baseline_store_mall_directory_24).into(restaurantIv);
                }catch (Exception e){
                    restaurantIv.setImageResource(R.drawable.ic_baseline_store_mall_directory_24);
                }


            }
        });
    }

    private void loadRestaurantMenus(String filterKey) {
        String filter=filterKey;
        menuList= new ArrayList<>();
        filteredMenuList=new ArrayList<>();
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("menu").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("TAG", "onSuccess list empty");
                    return;
                }
                else{
                    List<ModelMenu> types=queryDocumentSnapshots.toObjects(ModelMenu.class);
                    menuList.addAll(types);
                    for(int i=0; i<menuList.size(); i++){
                        if(menuList.get(i).getMenuId().equals(filter)){

                            filteredMenuList.add(menuList.get(i));

                        }
                    }
                    Log.d("TAG", "onSuccess"+menuList);
                    adapterMenu=new AdapterMenu(RestaurantDetails.this, filteredMenuList);
                    menuRv.setAdapter(adapterMenu);
                }

            }
        });

    }

    private void openMap(){
        SystemClock.sleep(1000);
        String address="https://maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+restaurantLatitude+","+restaurantLongitude;
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void openSettings(){
        Intent intent=new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package",BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void startLocationUpdates(){
        mSettingClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode=((ApiException)e).getStatusCode();
                        switch (statusCode){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings not satisfied, attempting to upgrade location settings");

                                try{
                                    ResolvableApiException rae=(ResolvableApiException)e;
                                    rae.startResolutionForResult(RestaurantDetails.this, REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException ex) {
                                    Log.i(TAG, "PendingIntent unable to execute request");

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String error="Location settings are inadequate, and cannot be fixed";
                                Log.e(TAG, "Location settings are inadequate, and cannot be fixed");
                                Toast.makeText(RestaurantDetails.this, error, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(this, task->Log.d(TAG, "Location updates stopped"));

    }
    private boolean checkPermission(){
        int permissionState= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState== PackageManager.PERMISSION_GRANTED;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(mRequestingLocationUpdates && checkPermission()){
            startLocationUpdates();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestingLocationUpdates){
            stopLocationUpdates();
        }
    }
}
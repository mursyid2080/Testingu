package com.example.testingu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.testingu.RVDirectoryRestaurant.AdapterRestaurant;
import com.example.testingu.RVDirectoryRestaurant.ModelRestaurant;
import com.example.testingu.RVNearMeRestaurant.AdapterNearMe;
import com.example.testingu.RVNearMeRestaurant.ModelNearMe;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.logging.type.HttpRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NearMe extends AppCompatActivity {

    FirebaseFirestore fStore;
    private RecyclerView nearMeRv;

    private ArrayList<ModelNearMe> restaurantList, filteredList;
    private AdapterNearMe adapterNearMe;
    private String myLatitude, myLongitude;

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
        setContentView(R.layout.activity_near_me);

        nearMeRv=findViewById(R.id.nearMeRv);

        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Getting user location");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(NearMe.this);
        mSettingClient=LocationServices.getSettingsClient(NearMe.this);

        mLocationCallback=new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation=locationResult.getLastLocation();

                myLatitude= String.valueOf(mCurrentLocation.getLatitude());
                myLongitude= String.valueOf(mCurrentLocation.getLongitude());

                if(j==1){
                    showRv(myLatitude, myLongitude);
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



        Dexter.withActivity(NearMe.this)
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

    private void showRv(String myLatitude, String myLongitude){
        this.myLatitude=myLatitude;
        this.myLongitude=myLongitude;

        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Finding Closest Restaurants");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

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
                            List<ModelNearMe> types=queryDocumentSnapshots.toObjects(ModelNearMe.class);
                            restaurantList.addAll(types);
                            for(int i=0; i<restaurantList.size(); i++){
                                //filter by location
                                float[] results = new float[1];
                                Location.distanceBetween(Double.parseDouble(myLatitude), Double.parseDouble(myLongitude),
                                        Double.parseDouble(restaurantList.get(i).getLatitude()), Double.parseDouble(restaurantList.get(i).getLongitude()),
                                        results);
                                if(results[0]<=1000){
                                    BigDecimal value = new BigDecimal(results[0]/1000);
                                    BigDecimal value2 = value.setScale(2, RoundingMode.HALF_UP);
                                    restaurantList.get(i).setDistance(value2+"km");
                                    filteredList.add(restaurantList.get(i));
                                }
                            }
                            Log.d("TAG", "onSuccess"+restaurantList);
                            //setup adapter
                            adapterNearMe=new AdapterNearMe(getApplicationContext(), filteredList);
                            //set adapter to recyclerview
                            nearMeRv.setAdapter(adapterNearMe);
                            dialog.dismiss();
                        }
                    }
                });



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
                                    rae.startResolutionForResult(NearMe.this, REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException ex) {
                                    Log.i(TAG, "PendingIntent unable to execute request");

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String error="Location settings are inadequate, and cannot be fixed";
                                Log.e(TAG, "Location settings are inadequate, and cannot be fixed");
                                Toast.makeText(NearMe.this, error, Toast.LENGTH_SHORT).show();


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
package com.example.testingu.RVNearMeRestaurant;

public class ModelNearMe {

    private String id;
    private String restaurantName;
    private String address;
    private String image;
    private String longitude;
    private String latitude;
    private String kk;
    private String category;
    private String restaurantOpen;
    private String distance;

    public ModelNearMe() {

    }

    public ModelNearMe(String id, String restaurantName, String address, String image,
                       String latitude, String longitude, String kk, String category, String restaurantOpen, String distance) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.kk = kk;
        this.category = category;
        this.restaurantOpen = restaurantOpen;
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRestaurantOpen() {
        return restaurantOpen;
    }

    public void setRestaurantOpen(String restaurantOpen) {
        this.restaurantOpen = restaurantOpen;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String name) {
        this.restaurantName = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String logitude) {
        this.longitude = logitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getKk() {
        return kk;
    }

    public void setKk(String kk) {
        this.kk = kk;
    }
}

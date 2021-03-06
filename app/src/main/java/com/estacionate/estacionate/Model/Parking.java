package com.estacionate.estacionate.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DiegoAlejandro on 18/05/2017.
 */

public class Parking {
    public String pKey;
    public String uid;
    public String parkingName;
    public int capacity;
    public Double dayPrice;
    public Double nightPrice;
    public int spacesOcuppied;
    public Double latitude;
    public Double longitude;

    public Parking() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Parking(String uid,
                   String parkingName,
                   int capacity,
                   Double dayPrice,
                   Double nightPrice,
                   int spacesOcuppied) {
        this.uid = uid;
        this.parkingName = parkingName;
        this.capacity = capacity;
        this.dayPrice = dayPrice;
        this.nightPrice = nightPrice;
        this.spacesOcuppied = spacesOcuppied;
    }

    public Parking(String uid,
            String parkingName,
            int capacity,
            Double dayPrice,
            Double nightPrice,
            int spacesOcuppied,
            Double latitude,
            Double longitude) {
        this.uid = uid;
        this.parkingName = parkingName;
        this.capacity = capacity;
        this.dayPrice = dayPrice;
        this.nightPrice = nightPrice;
        this.spacesOcuppied = spacesOcuppied;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("parkingName", parkingName);
        result.put("capacity", capacity);
        result.put("dayPrice", dayPrice);
        result.put("nightPrice", nightPrice);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }
}

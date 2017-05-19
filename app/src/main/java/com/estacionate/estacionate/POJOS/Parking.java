package com.estacionate.estacionate.POJOS;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DiegoAlejandro on 18/05/2017.
 */

public class Parking {

    public String uid;
    public String parkingName;
    public int capacity;
    public Double dayPrice;
    public Double nightPrice;
    public int spacesOcuppied;

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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("parkingName", parkingName);
        result.put("capacity", capacity);
        result.put("dayPrice", dayPrice);
        result.put("nightPrice", nightPrice);

        return result;
    }
}

package com.estacionate.estacionate;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estacionate.estacionate.Model.Parking;
import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by DiegoAlejandro on 23/05/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.MyParkingsHolder>{

    List<Parking> parkings;

    public Adapter(List<Parking> parkings) {
        this.parkings = parkings;
    }

    @Override
    public MyParkingsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler, parent, false);
        MyParkingsHolder holder = new MyParkingsHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyParkingsHolder holder, int position) {
        final Parking parking = parkings.get(position);
        holder.textViewName.setText(parking.parkingName);
        holder.textViewSpaces.setText(String.valueOf(parking.spacesOcuppied));
        holder.button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusSpaceOccupied(parking);
            }
        });
        holder.button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusSpaceOccupied(parking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkings.size();
    }

    public static class MyParkingsHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewSpaces;
        Button button_plus,button_minus;
        public MyParkingsHolder(View itemView) {
            super(itemView);
            textViewName= (TextView) itemView.findViewById(R.id.text_view_name);
            textViewSpaces= (TextView) itemView.findViewById(R.id.text_view_spaces);
            button_plus= (Button) itemView.findViewById(R.id.button_plus);
            button_minus= (Button) itemView.findViewById(R.id.button_minus);
        }
    }

    public void plusSpaceOccupied(Parking parking){
        int newSpacesOcuppied = parking.spacesOcuppied + 1;
        if(newSpacesOcuppied<=parking.capacity){
            String key = parking.pKey;
            Firebase mRootRef = new Firebase("https://estacionate-c7098.firebaseio.com/Parking/"+key);
            Parking parkingUpdated = new Parking(parking.uid,
                                                parking.parkingName,
                                                parking.capacity,
                                                parking.dayPrice,
                                                parking.nightPrice,
                                                newSpacesOcuppied,
                                                parking.latitude,
                                                parking.longitude);
            mRootRef.setValue(parkingUpdated);
        }else{
            //Toast.makeText(getItemViewType(), "Estacionamiento registrado.", Toast.LENGTH_SHORT).show();
        }
    }

    public void minusSpaceOccupied(Parking parking){
        int newSpacesOcuppied = parking.spacesOcuppied - 1;
        if(newSpacesOcuppied>=0){
            String key = parking.pKey;
            Firebase mRootRef = new Firebase("https://estacionate-c7098.firebaseio.com/Parking/"+key);
            Parking parkingUpdated = new Parking(parking.uid,
                    parking.parkingName,
                    parking.capacity,
                    parking.dayPrice,
                    parking.nightPrice,
                    newSpacesOcuppied,
                    parking.latitude,
                    parking.longitude);
            mRootRef.setValue(parkingUpdated);
        }else{
            //Toast.makeText(getItemViewType(), "Estacionamiento registrado.", Toast.LENGTH_SHORT).show();
        }
    }
}

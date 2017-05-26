package com.estacionate.estacionate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estacionate.estacionate.Model.Parking;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyParkingsFragment extends Fragment {

    View v;
    RecyclerView rv;
    List<Parking> parkings;
    Adapter adapter;

    public MyParkingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_my_parkings, container, false);

        rv =  (RecyclerView) v.findViewById(R.id.recycler_parkings);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        parkings = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        adapter = new Adapter(parkings);
        rv.setAdapter(adapter);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        database.getReference().getRoot().child("Parking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkings.removeAll(parkings);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Parking parking = snapshot.getValue(Parking.class);
                    parking.pKey = snapshot.getKey();
                    if(parking.uid.equals(user.getUid())){
                        parkings.add(parking);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }


}

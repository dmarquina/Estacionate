package com.estacionate.estacionate;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.estacionate.estacionate.POJOS.Parking;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateParkingFragment extends Fragment {

    GoogleMap map;
    MapView mapView;
    View v;
    EditText parkingName;
    EditText capacity;
    EditText dayPrice;
    EditText nightPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        v = inflater.inflate(R.layout.fragment_create_parking, container, false);

        mapView = (MapView) v.findViewById(R.id.mapviewselector);
        mapView.onCreate(savedInstanceState);

        startCreateParkingButton();
        loadMap(v, savedInstanceState);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(-12.053623,-77.0852702), 15);
        map.animateCamera(cameraUpdate);
        return v;
    }

    private void startCreateParkingButton(){
        Button btnCreateParking = (Button) v.findViewById(R.id.btn_createParking);
        btnCreateParking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveParking();
                cleanEditFields();
            }
        } );
    }

    private void loadMap(View v, Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        try {
            fm.beginTransaction().replace(R.id.mapviewselector, supportMapFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            MapsInitializer.initialize(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CreateParkingFragment.this.map = googleMap;


                // my-location listener
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Log.e("MY_LOCATION", "ok");
                        final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Log.e("GPS", "disabled");
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                        }
                        return false;
                    }
                });

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("error4", "error4");
                    return;
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

            }
        });

    }

    public void saveParking(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        parkingName = (EditText) v.findViewById(R.id.parking_name);
        capacity = (EditText) v.findViewById(R.id.capacity);
        dayPrice = (EditText) v.findViewById(R.id.day_price_hour);
        nightPrice = (EditText) v.findViewById(R.id.night_price_hour);

        Firebase mRootRef = new Firebase("https://estacionate-c7098.firebaseio.com/Parking");

        String UID = user.getUid();
        Parking newParking = new Parking(
                UID,
                parkingName.getText().toString(),
                Integer.parseInt(capacity.getText().toString()),
                Double.parseDouble(dayPrice.getText().toString()),
                Double.parseDouble(nightPrice.getText().toString()),
                0);

        mRootRef.push().setValue(newParking);
    }

    public void cleanEditFields(){
        parkingName.setText("");
        capacity.setText("");
        dayPrice.setText("");
        nightPrice.setText("");
    }
}

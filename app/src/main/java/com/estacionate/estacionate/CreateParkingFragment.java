package com.estacionate.estacionate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.estacionate.estacionate.Model.Parking;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import butterknife.ButterKnife;

public class CreateParkingFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    final int CERO_ENTERO = 0;
    GoogleMap map;
    MapView mapView;
    View v;
    Bundle savedInstanceState;
    LocationManager lm;
    LatLng ll;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    EditText parkingName;
    EditText capacity;
    EditText dayPrice;
    EditText nightPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_parking, container, false);

        ButterKnife.bind(this, v);
        this.savedInstanceState = savedInstanceState;
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e("PERMISSION", "Solicitando permiso de GPS");
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapView = (MapView) v.findViewById(R.id.mapviewselector);
            mapView.onCreate(savedInstanceState);
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            loadMap(v, savedInstanceState);
        }
        startCreateParkingButton();
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
        try {
            MapsInitializer.initialize(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CreateParkingFragment.this.map = googleMap;

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

                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(CreateParkingFragment.this)
                        .addOnConnectionFailedListener(CreateParkingFragment.this)
                        .build();
                mGoogleApiClient.connect();

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
                CERO_ENTERO,
                ll.latitude,
                ll.longitude);

        mRootRef.push().setValue(newParking);
        Toast.makeText(getActivity(), "Estacionamiento registrado.", Toast.LENGTH_SHORT).show();
    }

    public void cleanEditFields(){
        parkingName.setText("");
        capacity.setText("");
        dayPrice.setText("");
        nightPrice.setText("");
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("MY_LOCATION", "if");
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, CreateParkingFragment.this);
            Log.e("MY_LOCATION", "else");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getContext(), "lo sentimos no podemos obtener ubicación actual", Toast.LENGTH_SHORT).show();
        } else {
            ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            map.animateCamera(update);
        }
    }
}

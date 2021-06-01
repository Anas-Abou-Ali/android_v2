package com.android.joffer.vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.joffer.R;
import com.android.joffer.database.DBHandler;
import com.android.joffer.model.Offer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;

public class OfferActivity extends FragmentActivity  implements OnMapReadyCallback{
    private GoogleMap mMap;
    private DBHandler dbHandler;
    private Offer offer;
   // private User user;
    private TextView titOffer;
    private ImageView image02;
    private ImageView image03;
    private ImageView image04;
    private TextView desOffer;
    private TextView phoneOffer;
    private TextView emailOffer;

    private TextView lastNoffer;
    private String offerLocation;
    private ActionBar toolbar;

    DatabaseReference rootRef, demoRef;

    List<Marker> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        offer=new Offer();

      // dbHandler=new DBHandler(OfferActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        image02 = (ImageView) findViewById(R.id.image02);
        image03 = (ImageView) findViewById(R.id.image03);
        image04 = (ImageView) findViewById(R.id.image04);
        titOffer=(TextView)findViewById(R.id.titOffer);
        desOffer=(TextView) findViewById(R.id.desOffer);
        lastNoffer=(TextView) findViewById(R.id.lastNoffer);
        phoneOffer=(TextView)findViewById(R.id.phoneOffer);
        emailOffer=(TextView) findViewById(R.id.emailOffer);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Intent i = getIntent();

        int  id_offer=i.getIntExtra("id_offer",-1);
       // offer= dbHandler.getOfferById(id_offer);
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef= rootRef.child("offers");
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int max=(int)dataSnapshot.getChildrenCount();
                Offer ofer=dataSnapshot.child(String.valueOf(id_offer)).getValue(Offer.class);
                //offers.add(ofer);
                titOffer.setText(ofer.getTitle());
                image02.setImageBitmap(Bitmap.createScaledBitmap(StringToBitMap(ofer.getImage1()), 300, 300, false));
                 image03.setImageBitmap(Bitmap.createScaledBitmap(StringToBitMap(ofer.getImage2()), 300, 300, false));
                image04.setImageBitmap(Bitmap.createScaledBitmap(StringToBitMap(ofer.getImage3()), 300, 300, false));
                desOffer.setText(ofer.getDescription());
                offerLocation=ofer.getAdress().trim();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OfferActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
            }
        });

       // offerLocation=offer.getAdress();
       // Log.d("user",String.valueOf(offer.getId_user()));
        //user=dbHandler.getUserById(offer.getId_user());

        //lastNoffer.setText(user.getLast_name()+" "+ user.getFirst_name());


        //phoneOffer.setText(user.getPhone());
       // Linkify.addLinks(phoneOffer, Linkify.ALL);
        //emailOffer.setText(user.getEmail());
       // Linkify.addLinks(emailOffer, Linkify.ALL);
      //  Log.d("location",offerLocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markerList = new ArrayList<>();
        markerList.add(mMap.addMarker(new MarkerOptions()
                    .position(getLocationFromAddress(this,"Rabat,Maroc")).title("rabat")));
        for(Marker m : markerList){
            // Add a marker in Sydney and move the camera
            LatLng latLng = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
            mMap.addMarker(new MarkerOptions().position(latLng) );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
        }


    }
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    Intent i=new Intent(OfferActivity.this,HomeActivity.class);
                    startActivity(i);
                case R.id.add:
                    Intent i1=new Intent(OfferActivity.this,MainActivity.class);
                    startActivity(i1);
                    return true;
                case R.id.person:

                    return true;

            }
            return false;
        }
    };
}
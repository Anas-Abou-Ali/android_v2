package com.android.joffer.vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.joffer.R;
import com.android.joffer.control.DbRetrieveDataAdapter;
import com.android.joffer.database.DBHandler;
import com.android.joffer.model.Offer;
//import com.android.joffer.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewOffer extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private DBHandler dbHandler;
    private ListView listView;
    private TextView textView;
    private DbRetrieveDataAdapter adapter;
    private ArrayAdapter<Offer> offerAdapter;
    private Spinner categorie;
    DatabaseReference rootRef, demoRef;
    private List<Offer> offersCat;
    String[] categories = {"Vetements", "Produits éléctroménagers", "Meubles", "Nourriture", "Autres Objets"};
    private ArrayList<Offer> offers = new ArrayList<Offer>();
    public static final String TAG = "view";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offer);
        //dbHandler = new DBHandler(ViewOffer.this);
        listView = (ListView) findViewById(R.id.listview);
        Intent i=getIntent();
        int  id_cat=i.getIntExtra("id_cat",-1);
        Log.d("id_cat",String.valueOf(id_cat));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef= rootRef.child("offers");
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int max=(int)dataSnapshot.getChildrenCount();
                Offer ofer=dataSnapshot.child("1").getValue(Offer.class);

               for (int i=1;i<=max;i++) {
                    offers.add(dataSnapshot.child(String.valueOf(i)).getValue(Offer.class));
                }
                ArrayList<Offer> offerCat=new ArrayList<Offer>();
                for (Offer offer :offers){
                    if(offer.getId_cat()==id_cat){
                        Log.d("cat",String.valueOf(offer.getId_cat()));
                        offerCat.add(offer);
                    }

                }
                listView.setAdapter(null);
                adapter = new DbRetrieveDataAdapter(ViewOffer.this,offerCat);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(ViewOffer.this);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewOffer.this, "Error fetching data", Toast.LENGTH_LONG).show();
            }
        });

        // ListAdapter listAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,offres);
        if(offers==null){
        Log.d(TAG, "ok");}
       // offers = (ArrayList<Offer>) dbHandler.getAllOffer();

        Log.e("Array", ">>" + offers.size());



}

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this,OfferActivity.class);
        Log.d("podition",String.valueOf(position));
        intent.putExtra("position", position);
        // Or / And

        if(offers==null){
            Log.d("offer","is null");
        }

        intent.putExtra("id_offer", offers.get(position).getId_offer());
        startActivity(intent);
     /*dbHandler.addNewUser("bouthaina","hrour","hrourBoutayna@gmail.com",null,"taounate",
                "0648500049",0);
        Toast.makeText(ViewOffer.this, "User ajouté avec succès", Toast.LENGTH_SHORT).show();*/
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    Intent i=new Intent(ViewOffer.this,HomeActivity.class);
                    startActivity(i);
                    return true;
                case R.id.add:
                    Intent i1=new Intent(ViewOffer.this,MainActivity.class);
                    startActivity(i1);
                    return true;
                case R.id.person:
                    Intent i2=new Intent(ViewOffer.this,MainActivity.class);
                    startActivity(i2);

                    return true;

            }
            return false;
        }
    };


}
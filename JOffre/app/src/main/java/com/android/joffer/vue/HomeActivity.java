package com.android.joffer.vue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.joffer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private ImageView cloths;
    private ImageView electronics;
    private ImageView pets;
    private ImageView babies;
    private ImageView fourniture;
    private ImageView books;
    private ImageView sports;
    private ImageView tools;
    DatabaseReference rootRef, demoRef;
    public static Intent makeIntent(Context c){
        return new Intent(c , HomeActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cloths=(ImageView)findViewById(R.id.cloths);
        electronics=(ImageView)findViewById(R.id.electronics);
        pets=(ImageView)findViewById(R.id.pets);
        babies=(ImageView)findViewById(R.id.babies);
        fourniture=(ImageView)findViewById(R.id.fourniture);
        books=(ImageView)findViewById(R.id.books);
       sports=(ImageView)findViewById(R.id.electronics);
       tools=(ImageView)findViewById(R.id.tools);


        cloths.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,ViewOffer.class);


                intent.putExtra("id_cat",1);
                startActivity(intent);
            }
        });
        electronics.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 2);
                startActivity(intent);
            }
        });
        fourniture.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 3);
                startActivity(intent);
            }
        });
        books.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 4);
                startActivity(intent);
            }
        });
        sports.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 5);
                startActivity(intent);
            }
        });
        babies.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 6);
                startActivity(intent);
            }
        });
        pets.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 7);
                startActivity(intent);
            }
        });
        tools.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,ViewOffer.class);

                intent.putExtra("id_cat", 8);
                startActivity(intent);
            }
        });




    }
}
package com.android.joffer.vue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.android.joffer.R;
import com.android.joffer.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ProfileActivity extends AppCompatActivity {
    TextView profileNameTextView;
    EditText profilePhone;
    TextView profileEmailTextView;
    EditText profileCity;
    Button saveProfileButton;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Users");

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileNameTextView = findViewById(R.id.profile_name);
        profileEmailTextView = findViewById(R.id.profile_email);

        profilePhone = findViewById(R.id.profile_phone);
        profileCity =  findViewById(R.id.profile_city);

        saveProfileButton = findViewById(R.id.save_profile_button);


        profileNameTextView.setText( getProfileName() );
        profileEmailTextView.setText(getProfileEmail());

        String CUID = currentUser.getUid();
        DatabaseReference UIDRef = database.getReference().child("Users").child(CUID);

        UIDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String city = dataSnapshot.child("city").getValue(String.class);
                profilePhone.setText(phone);
                profileCity.setText(city);
                Log.d("Search", "Value is: " + phone);
                Log.d("Search", "Value is: " + city);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("search", "Failed to read value.", error.toException());
            }
        });


        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }


    private String getProfileEmail(){
        String Email = (String)currentUser.getEmail();
        return Email;
    }

    private String getProfileName(){
        String Name = (String)currentUser.getDisplayName();

        return Name;
    }
    public void saveProfile(){

        Users user = new Users();
        String userPhone = profilePhone.getText().toString().trim();
        String userCity = profileCity.getText().toString().trim();
        user.setUID(currentUser.getUid());
        user.setEmail(getProfileEmail().trim());
        user.setName(getProfileName().trim());
        user.setCity(userCity.trim());
        user.setPhone(userPhone.trim());
        myRef.child(user.getUID().toString()).setValue(user);
        Toast.makeText(this,userPhone,Toast.LENGTH_LONG).show();
        Toast.makeText(this,userCity,Toast.LENGTH_LONG).show();
    }

    public static Intent makeIntent(Context c){
        return new Intent(c, ProfileActivity.class);
    }
}

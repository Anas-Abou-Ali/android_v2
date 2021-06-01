package com.android.joffer.vue;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.joffer.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.android.joffer.database.DBHandler;
import com.android.joffer.model.Image;
import com.android.joffer.model.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , LocationListener {
    private Offer offer;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private EditText titre;
    private EditText description;
    private Spinner type;
    private String photoPath;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private Button btnSuiv;
    private ImageView btnAdr;
    private DBHandler dbHandler;
    private EditText Adress;
    int maxid;
    Image img=new Image();
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Uri filePath;
    String categorie;
    String imageStorage;

    DatabaseReference rootRef, demoRef;
    int cat;
    Double latitude;
    Double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

       // dbHandler = new DBHandler(MainActivity.this);



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }

            Log.d("image1","is null");

        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef= rootRef.child("offers");


            Log.d("image2","is null");
            demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid=(int)dataSnapshot.getChildrenCount();
                /*Offer ofer=dataSnapshot.child("1").getValue(Offer.class);
                titre.setText(ofer.getTitle());
                description.setText(ofer.getDescription());
                image1.setImageBitmap(Bitmap.createScaledBitmap(StringToBitMap(ofer.getImage1()), 325, 325, false));*/
               }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
            }
        });






                 /*getUserLocation();
                locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);*/

    }

    private void init() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Log.d("image1","is null");
        offer = new Offer();
        image1 = (ImageView) findViewById(R.id.image1);
        onClickOnImage1();
        image2 = (ImageView) findViewById(R.id.image2);
        onClickOnImage2();
        image3 = (ImageView) findViewById(R.id.image3);
        onClickOnImage3();
        type = findViewById(R.id.type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(this);
        titre = (EditText) findViewById(R.id.titre);
        description = (EditText) findViewById(R.id.description);
        Adress = (EditText) findViewById(R.id.Address);
        btnSuiv = (Button) findViewById(R.id.btnSuiv);
        btnAdr = (ImageView) findViewById(R.id.btnAdr);
        onClickOnButton1();

        onClickButton();


    }

    private void onClickOnImage1() {

        image1.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage(1, 2);
            }
        });
    }
    private void onClickOnButton1() {

        btnAdr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
               getLocation();
            }
        });
    }
    private void onClickOnImage2() {

        image2.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage(3, 4);
            }
        });
    }

    private void onClickOnImage3() {

        image3.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {

                selectImage(5, 6);
            }
        });
    }

    private void onClickButton() {

        btnSuiv.setOnClickListener(new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String title = titre.getText().toString();
                String des = description.getText().toString();
                String date = getDate();
                String adr = Adress.getText().toString();

                if (title != null || !title.trim().isEmpty() || des != null || !des.trim().isEmpty() || adr != null || !adr.trim().isEmpty()) {
                    if (categorie.equals("Vetements")) cat = 1;
                    else if (categorie.equals("Eléctroniques")) cat = 2;
                    else if (categorie.equals("Meubles")) cat = 3;
                    else if (categorie.equals("Livres")) cat = 4;
                    else if (categorie.equals("Sport")) cat = 5;
                    else if (categorie.equals("Enfants")) cat = 6;
                    else if (categorie.equals("Animaux")) cat = 7;
                    else if (categorie.equals("Outils")) cat = 8;
                   // dbHandler.addNewOffer(title, des, date, images, cat, adr, 1);
                    offer.setTitle(title);
                    offer.setDescription(des);
                    offer.setDate_offer(date);
                    offer.setImage1(BitMapToString(images.get(0)));
                    offer.setImage2(BitMapToString(images.get(1)));
                    offer.setImage3(BitMapToString(images.get(2)));
                    offer.setId_cat(cat);
                    offer.setId_user(1);
                    offer.setAdress(adr.trim());
                    offer.setId_offer(maxid+1);
                    demoRef.child(String.valueOf(maxid+1)).setValue(offer);
                    Toast.makeText(MainActivity.this, "Offre ajouté avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
                }



                //Intent intent = new Intent(MainActivity.this,MapActivity.class);
                /*intent.putExtra("titre",title);
                intent.putExtra("des",des);
                intent.putExtra("cat",1);
                intent.putExtra("images",images);*/
                // startActivity(intent);
            }
        });

    }


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorie = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), categorie, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    image1.setImageBitmap(Bitmap.createScaledBitmap(image, 325, 325, false));
                    images.add(image);



                }
                break;
            case 2:

                if (resultCode == RESULT_OK) {

                    image1.setImageBitmap(Bitmap.createScaledBitmap(getImage(data), 325, 325, false));
                    Bitmap bit= getResizedBitmap(getImage(data) ,20,20 );
                    images.add(bit);

                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    image2.setImageBitmap(Bitmap.createScaledBitmap(image, 325, 325, false));
                    images.add(image);

                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {
                    image2.setImageBitmap(Bitmap.createScaledBitmap(getImage(data), 325, 325, false));
                    Bitmap bit= getResizedBitmap(getImage(data) ,20,20 );
                    images.add(bit);


                }
                break;
            case 5:
                if (resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    image3.setImageBitmap(Bitmap.createScaledBitmap(image, 325, 325, false));
                    images.add(image);

                }
                break;
            case 6:
                if (resultCode == RESULT_OK) {
                    image3.setImageBitmap(Bitmap.createScaledBitmap(getImage(data), 325, 325, false));
                    Bitmap bit= getResizedBitmap(getImage(data) ,20,20 );
                    images.add(bit);

                }
                break;


        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }


    private void selectImage(int requestCode1, int requestCode2) {
        final CharSequence[] options = {"Prendre une photo", "Choisir de la galerie", "Fermer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ajouter une photo !!!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Prendre une photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, requestCode1);

                } else if (options[item].equals("Choisir de la galerie")) {

                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(i, requestCode2);

                    } else if (options[item].equals("Fermer")) {
                        dialog.dismiss();
                    }

                }
        });
        builder.show();
    }

    private Bitmap getImage(Intent data) {

        Uri selectImage = data.getData();
        if(selectImage==null){
            Log.d("galerie","erreur");
        }
        String[] filePathImage = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(selectImage, filePathImage, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathImage[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        Bitmap image = BitmapFactory.decodeFile(path);
        if(image==null){
            Log.d("galerie","erreur");
        }
        return image;
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dates = formatter.format(date);
        return dates;
    }

   /*@Override
    public void onLocationChanged(Location location) {
        btnAdr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String address = addresses.get(0).getCountryName();
                    Adress.setText(address); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


    }
    private void getUserLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
                return;
            }
        }
    }*/
   /* private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
                Log.d("Image","null");
        }

    }*/

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
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
    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        // TODO Auto-generated method stub
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses =new ArrayList<Address>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String city = addresses.get(0).getLocality();

        String country = addresses.get(0).getCountryName();
        String street = addresses.get(0).getThoroughfare();
        String district=addresses.get(0).getSubAdminArea();
    ;
        Adress.setText(city +"," +country);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    Intent i=new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(i);
                    return true;
                case R.id.add:
                    Intent i1=new Intent(MainActivity.this,MainActivity.class);
                    startActivity(i1);
                    return true;
                case R.id.person:

                    return true;

            }
            return false;
        }
    };
    }












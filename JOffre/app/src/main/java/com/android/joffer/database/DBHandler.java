package com.android.joffer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.joffer.model.Offer;
import com.android.joffer.model.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper
{

    public static final String TAG = "DatabaseHandler";
    protected final static int VERSION = 1;
    protected final static String DATABASE_NAME = "offerDB.db";
    public static final String USER_TABLE_CREATE =
            "CREATE TABLE user (id_user INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "first_name TEXT,last_name  TEXT,email TEXT,password TEXT," +
                    "adresse TEXT,phone  TEXT,id_rate INTEGER," +
                    "FOREIGN KEY(id_rate) REFERENCES ratting(id_rate));" ;
    public static final String RATTING_TABLE_CREATE =
            "CREATE TABLE ratting(id_rate INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "rate TEXT);";
    public static final String CATEGORIE_TABLE_CREATE =
            "CREATE TABLE categorie (id_cat INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "label TEXT);";
    public static final String OFFER_TABLE_CREATE =
            "CREATE TABLE offer(id_offer INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT,description TEXT,date_offer TEXT,image1 blob,image2 blob,image3 blob,addresse TEXT," +
                    "id_cat INTEGER,id_user INTEGER," +
                    "FOREIGN KEY(id_cat) REFERENCES categorie(id_cat) " +
                    ",FOREIGN KEY(id_user) REFERENCES user(id_user));";
    public static final String MESSAGE_TABLE_CREATE =
            "CREATE TABLE message(id_message INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "msg_text TEXT,date_send TEXT,id_user1 INTEGER,id_user2 INTEGER," +
                    "FOREIGN KEY(id_user1) REFERENCES user(id_user)," +
                    "FOREIGN KEY(id_user2) REFERENCES user(id_user));";
    public static final String USER_TABLE_DROP = "DROP TABLE IF EXISTS user;";
    public static final String RATTING_TABLE_DROP = "DROP TABLE IF EXISTS ratting;";
    public static final String CATEGORIE_TABLE_DROP = "DROP TABLE IF EXISTS categorie;";
    public static final String OFFER_TABLE_DROP = "DROP TABLE IF EXISTS offer;";
    public static final String MESSAGE_TABLE_DROP = "DROP TABLE IF EXISTS message;";

    public  DBHandler(Context context) {
        super(context, DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RATTING_TABLE_CREATE);
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(CATEGORIE_TABLE_CREATE);
        db.execSQL(OFFER_TABLE_CREATE);
        db.execSQL(MESSAGE_TABLE_CREATE);
    }
    public void addNewOffer(String title, String description,String date,ArrayList<Bitmap> images, int idCat, String adr, int user ) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("first",title);
        values.put("descr", description);
        values.put("date_offer", date);
        values.put("image1", getBitmapAsByteArray(images.get(0)));
        values.put("image2", getBitmapAsByteArray(images.get(1)));
        values.put("image3", getBitmapAsByteArray(images.get(2)));
        values.put("id_cat", idCat);
        values.put("addresse", adr);
        values.put("id_user", user);
        db.insert("offer", null, values);
        db.close();
    }
    public void addNewUser(String firstN, String lastN, String email, String password, String adr, String phone, int rate) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("first_name",firstN);
        values.put("last_name", lastN);
        values.put("email", email);
        values.put("password", password);
        values.put("adresse",adr);
        values.put("phone", phone);
        values.put("id_rate", rate);

        db.insert("user", null, values);
        db.close();
    }
    public void addNewCat(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("label",title);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL(RATTING_TABLE_DROP);
        db.execSQL(USER_TABLE_DROP);
        db.execSQL(CATEGORIE_TABLE_DROP);
        db.execSQL(OFFER_TABLE_DROP);
        db.execSQL(MESSAGE_TABLE_DROP);
        onCreate(db);
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    public List<Offer> getAllOffer(){

            List<Offer> offers = new ArrayList<Offer>();
            ArrayList<Bitmap> blobs=new ArrayList<Bitmap>();
            Bitmap bitmap;


            String selectQuery = "SELECT * FROM offer ";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {


                do {
                    Offer offer = new Offer();
                    offer.setId_offer(cursor.getInt(0));
                    offer.setTitle(cursor.getString(1));
                    offer.setDescription(cursor.getString(2));
                    offer.setDate_offer(cursor.getString(3));
                    for(int i=0;i<3;i++){
                        byte[]  blob=cursor.getBlob(i+4);
                        bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);
                        blobs.add(bitmap);
                    }
                   // offer.setImages(blobs);
                    offer.setId_cat(cursor.getInt(8));
                    offer.setAdress(cursor.getString(7));
                    offer.setId_user(cursor.getInt(9));
                    offers.add(offer);
                } while (cursor.moveToNext());
            }

            db.close();
        if(offers.size() > 0 ){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Offer");
            for(Offer d : offers){
                ref.push().setValue(d.toString());
            }
        }


            return offers;


        }
    public Offer getOfferById(int id){

        Offer offer = new Offer();
        ArrayList<Bitmap> blobs=new ArrayList<Bitmap>();
        Bitmap bitmap;

        String selectQuery = "SELECT * FROM offer where id_offer=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String [] {String.valueOf(id)});
        int num=cursor.getCount();
        Log.d("num ligne",String.valueOf(num));
        if (cursor.moveToFirst()) {

            do {

                offer.setId_offer(cursor.getInt(0));
                offer.setTitle(cursor.getString(1));
                offer.setDescription(cursor.getString(2));
                offer.setDate_offer(cursor.getString(3));
                for(int i=0;i<3;i++){
                    byte[]  blob=cursor.getBlob(i+4);
                    bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    blobs.add(bitmap);
                }
                //offer.setImages(blobs);
                offer.setId_cat(cursor.getInt(8));
                offer.setAdress(cursor.getString(7));
                offer.setId_user(cursor.getInt(9));

            } while (cursor.moveToNext());
        }
        db.close();
       return offer;

    }
   /* public User getUserById(int id){

        User user = new User();


        String selectQuery = "SELECT * FROM user where id_user=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String [] {String.valueOf(id)});
        int num=cursor.getCount();
        Log.d("num ligne",String.valueOf(num));
        if (cursor.moveToFirst()) {

            do {

                user.setId_user(cursor.getInt(0));
                user.setLast_name(cursor.getString(1));
                user.setFirst_name(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setPhone(cursor.getString(6));

            } while (cursor.moveToNext());
        }
        db.close();
        return user;

    }*/
    public List<Offer> getAllOfferByCat(int cat){

        List<Offer> offers = new ArrayList<Offer>();
        ArrayList<Bitmap> blobs=new ArrayList<Bitmap>();
        Bitmap bitmap;


        String selectQuery = "SELECT * FROM offer where id_cat=? ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String [] {String.valueOf(cat)});

        if (cursor.moveToFirst()) {


            do {
                Offer offer = new Offer();
                offer.setId_offer(cursor.getInt(0));
                offer.setTitle(cursor.getString(1));
                offer.setDescription(cursor.getString(2));
                offer.setDate_offer(cursor.getString(3));
                for(int i=0;i<3;i++){
                    byte[]  blob=cursor.getBlob(i+4);
                    bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    blobs.add(bitmap);
                }
               // offer.setImages(blobs);
                offer.setId_cat(cursor.getInt(8));
                offer.setAdress(cursor.getString(7));
                offer.setId_user(cursor.getInt(9));
                offers.add(offer);
            } while (cursor.moveToNext());
        }

        db.close();


        return offers;

    }


    }


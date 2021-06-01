package com.android.joffer.control;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.joffer.R;
import com.android.joffer.database.DBHandler;
import com.android.joffer.model.Offer;
import com.android.joffer.vue.MainActivity;
import com.android.joffer.vue.OfferActivity;
import com.android.joffer.vue.ViewOffer;

public class DbRetrieveDataAdapter extends BaseAdapter {
    public static final String TAG = "list";
    private LayoutInflater mLayoutInflater = null;
    private Activity activity;
    private ViewHolder holder;
    private Offer offer;


    ArrayList<Offer>  Show;
    int i = 0 ;


    private class ViewHolder {
        public TextView Title,Description;
        public ImageView img_offer;

    }


    public DbRetrieveDataAdapter(Activity activity2,
                                ArrayList<Offer> offers) {
        this.activity = activity2;
        this.Show = offers;
        mLayoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return Show.size();
    }

    @Override
    public Object getItem(int position) {
        return Show.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.layout_list_itm, null);

            holder = new ViewHolder();
            holder.Title = (TextView) convertView.findViewById(R.id.titre_offer);
            holder.Description= (TextView) convertView.findViewById(R.id.date_offer);
            holder.img_offer = (ImageView) convertView.findViewById(R.id.img_offer);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.Title.setText(Show.get(position).getTitle());
        Log.d(TAG, Show.get(position).getTitle());
        holder.Description.setText("publié le : "+Show.get(position).getDate_offer());


        //Bitmap bm = BitmapFactory.decodeByteArray(Show.get(position)., 0, Show.get(position).getImgAry().length);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        holder.img_offer.setImageBitmap(StringToBitMap(Show.get(position).getImage1()));


        return convertView;
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



}

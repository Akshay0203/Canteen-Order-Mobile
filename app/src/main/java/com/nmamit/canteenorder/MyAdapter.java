package com.nmamit.canteenorder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {

    private ArrayList<String> hotelNames;
    private ArrayList<String> hotelAddress;
    private ArrayList<String> hotelEmail;
    private ArrayList<String> hotelId;
//    private ArrayList<Integer> hotelImages;
    private Context mContext;


    public MyAdapter( Context mContext, ArrayList<String> hotelNames, ArrayList<String> hotelAddress,
                      ArrayList<String> hotelEmail, ArrayList<String> hotelId) {
        this.hotelNames = hotelNames;
        this.hotelAddress = hotelAddress;
        this.hotelEmail = hotelEmail;
        this.hotelId = hotelId;
//        this.hotelImages = hotelImages;
        this.mContext = mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        MyViewHolder  viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHotelName, tvHotelAddress, tvHotelEmail;
//        ImageView ivHotelImage;
        LinearLayout parentLayout;
        MyViewHolder(View itemView) {
            super(itemView);
            tvHotelName = itemView.findViewById(R.id.hotel_name);
            tvHotelAddress = itemView.findViewById(R.id.hotel_address);
            tvHotelEmail = itemView.findViewById(R.id.hotel_email);
//            ivHotelImage = itemView.findViewById(R.id.hotel_image);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvHotelName.setText(hotelNames.get(position));
        holder.tvHotelAddress.setText(hotelAddress.get(position));
        holder.tvHotelEmail.setText(hotelEmail.get(position));
//        holder.ivHotelImage.setImageResource(hotelImages.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent menuIntent = new Intent(mContext, MenuActivity.class);
                menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                menuIntent.putExtra("hotelEmail", hotelEmail.get(position));
                mContext.startActivity(menuIntent);
//                Toast.makeText(mContext, "show menu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelNames.size();
    }
}

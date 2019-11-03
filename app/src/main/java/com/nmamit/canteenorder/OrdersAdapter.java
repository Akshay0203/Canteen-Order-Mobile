package com.nmamit.canteenorder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    List<String> hotelList;
    List<String> orderList;
    List<String> costList;
    List<String> statusList;
    List<String> orderTimeList;

    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrdersAdapter(Context mContext, List<String> hotelList, List<String> orderList,
                         List<String> costList, List<String> orderTimeList, List<String> statusList) {
        this.hotelList = hotelList;
        this.orderList = orderList;
        this.costList = costList;
        this.statusList = statusList;
        this.orderTimeList = orderTimeList;
//        this.hotelImages = hotelImages;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHotel, tvOrders, tvStatus ,tvTime, tvTotalCost;
        //        ImageView ivHotelImage;
        LinearLayout parentLayout;
        MyViewHolder(View itemView) {
            super(itemView);
            tvHotel = itemView.findViewById(R.id.tv_hotel_id);
            tvOrders = itemView.findViewById(R.id.tv_order_items);
            tvTotalCost = itemView.findViewById(R.id.tv_total_cost);
            tvTime = itemView.findViewById(R.id.tv_time);
//            ivHotelImage = itemView.findViewById(R.id.hotel_image);
            tvStatus = itemView.findViewById(R.id.tv_ordered_status);
            parentLayout = itemView.findViewById(R.id.order_parent_layout);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvHotel.setText(hotelList.get(position));
        holder.tvOrders.setText(orderList.get(position));
        holder.tvTotalCost.setText(costList.get(position));
        holder.tvTime.setText(orderTimeList.get(position));

        String status = statusList.get(position);
        holder.tvStatus.setText(status);
        holder.tvStatus.setText("Your order is being prepared");
        holder.tvStatus.setBackgroundColor(Color.DKGRAY);
        Log.d(TAG, "onBindViewHolder: status "+statusList.get(position));

        if(status.equals("paymentPending")) {
            Log.d(TAG, "inside if paymentPending: ");
            holder.tvStatus.setText("Your item is ready! Please be ready with the cash!");
            holder.tvStatus.setBackgroundColor(Color.MAGENTA);
        }
        else if(status.equals("completed")) {
            Log.d(TAG, "inside if completed: ");
            holder.tvStatus.setText("Payment successfully done! Thank you!");
            holder.tvStatus.setBackgroundColor(Color.GREEN);

        }
//        holder.ivHotelImage.setImageResource(hotelImages.get(position));
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }
}

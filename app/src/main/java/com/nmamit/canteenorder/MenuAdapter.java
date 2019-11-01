package com.nmamit.canteenorder;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>  {

    ArrayList<String> mMenuItems = new ArrayList<>();
    Map<String,Integer> orderedItems = new HashMap<>();

    Context mContext;

    Integer itemCount = 0;
    Integer[] quantity = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final String TAG = "MenuAdapter";
    private TextView tvItemCount;

    public MenuAdapter(Context mContext,ArrayList<String> mMenuItems, TextView tvItemCount, Map<String,Integer> orderedItems) {
        this.mMenuItems = mMenuItems;
        this.mContext = mContext;
        this.tvItemCount = tvItemCount;
        this.orderedItems = orderedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: "+position);
        final String item = mMenuItems.get(position);
        holder.tvItem.setText(item);
//        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(mContext, android.R.layout.simple_spinner_item, quantity);
//        holder.spQuantity.setAdapter(adapter);

        holder.tvIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer count = Integer.parseInt(holder.tvQty.getText().toString());
                count++;
                holder.tvQty.setText(count.toString());
            }

        });

        holder.tvDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer count = Integer.parseInt(holder.tvQty.getText().toString());
                if(count > 0) {
                    count--;
                    holder.tvQty.setText(count.toString());
                }
            }

        });
        holder.btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvQty = holder.tvQty;
                int qty = Integer.parseInt(tvQty.getText().toString());
                int diff = 0;
                if(qty < 1 && !orderedItems.containsKey(item) )
                    Toast.makeText(mContext, "Enter the quantity", Toast.LENGTH_SHORT).show();
                else {
                        if(orderedItems.containsKey(item))
//                        orderedItems.put(item, orderedItems.get(item) + qty);
                            diff = qty - orderedItems.get(item);
                        else
                            diff = qty;

                        itemCount += diff;
                        tvItemCount.setText(itemCount.toString());
                        tvItemCount.setVisibility(View.VISIBLE);

                        if(qty == 0)
                            orderedItems.remove(item);
                        else {
                            orderedItems.put(item, qty);
                            Toast.makeText(mContext, mMenuItems.get(position).split("\\r?\n")[0]+" Added!!", Toast.LENGTH_SHORT).show();
                        }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem,  tvDecrease, tvIncrease, tvQty;
//        Spinner spQuantity;
        Button btOrder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.tv_item_name);
//            spQuantity = itemView.findViewById(R.id.sp_quantity);
            tvIncrease = itemView.findViewById(R.id.increase);
            tvQty = itemView.findViewById(R.id.qty);
            tvDecrease = itemView.findViewById(R.id.decrease);
            btOrder = itemView.findViewById(R.id.bt_order);
        }
    }

}

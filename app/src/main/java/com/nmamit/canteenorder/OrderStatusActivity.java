package com.nmamit.canteenorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderStatusActivity extends BaseActivity {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final  String PREFERENCE_FILE_KEY = "com.nmamit.canteenorder.PREFERENCE_FILE_KEY";
    private SharedPreferences sharedPref;
    private static final String TAG = "OrderStatusActivity";
    String userId;
    List<Map<String, String>> bill ;
    List<String> hotelList = new ArrayList<>();
    List<String> orderList = new ArrayList<>();
    List<String> costList = new ArrayList<>();
    List<String> statusList = new ArrayList<>();
    List<String> orderTimeList = new ArrayList<>();
    private OrdersAdapter adapter;
    private RecyclerView orderstatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        orderstatusView = findViewById(R.id.order_status_view);

        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE);
        userId = sharedPref.getString("user_email","");

        showProgressDialog();
        db.collection("Order")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()) {
//                                tvHotel.setText(document.get("hotelId").toString());
                                hotelList.add(document.get("hotelId").toString());
                                bill = (List<Map<String, String>>) document.get("items");
                                String orderItem = new String();
                                for(Map<String, String> billItem : bill) {
//                                    tvOrders.append(billItem.get("name"));
                                    orderItem += billItem.get("name")+"\n";
//                                    tvOrders.append(billItem.get("price"));
//                                    tvOrders.append(billItem.get("quantity"));
//                                    tvOrders.append("\n");
                                }
                                orderList.add(orderItem);
                                costList.add(document.get("totalCost").toString());
                                orderTimeList.add(document.get("time")+"");
                                Log.d(TAG, "onComplete: status"+document.get("status").toString());
                                statusList.add(document.get("status").toString());

//                                tvStatus.setText(document.get("status").toString());
                            }
                            hideProgressDialog();
                            initAdapter();
                        }
                        else
                            Toast.makeText(OrderStatusActivity.this, "Couldn't reach servers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initAdapter() {
        adapter = new OrdersAdapter(this,hotelList, orderList,
                costList,  orderTimeList, statusList);
        orderstatusView.setAdapter(adapter);
        orderstatusView.setLayoutManager(new LinearLayoutManager(this));
    }
}

package com.nmamit.canteenorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends BaseActivity {
    HashMap<String, Integer> orderItems;
    HashMap<String, String> menu;
    ListView itemList;// itemCount;
    TextView tvTotal,tvStatus;
    Button btOrder;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy':'hh:mm:ss");
    CardView cardView ;
    Date date;
    private static final  String PREFERENCE_FILE_KEY = "com.nmamit.canteenorder.PREFERENCE_FILE_KEY";

    String hotelId;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> count = new ArrayList<>();
    private static final String TAG = "OrderActivity";
//    Map<String, String>[]  = new HashMap<String,String>()[];
    List<Map<String, String>> bill = new ArrayList<Map<String, String>>();
    Integer total = 0;
    SharedPreferences sharedPref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        itemList = findViewById(R.id.lv_items);
        tvTotal  = findViewById(R.id.tv_total);
        btOrder  = findViewById(R.id.bt_order);
        tvStatus = findViewById(R.id.tv_status);

        cardView = findViewById(R.id.status_card_view);
        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE);
        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelId");
        orderItems = (HashMap<String, Integer>)intent.getSerializableExtra("orderItems");
        menu = (HashMap<String, String>) intent.getSerializableExtra("menu");

        cardView.setVisibility(View.GONE);
//        String[] items = (String[]) orderItems.values().toArray();
        for(Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String[] orderItem = entry.getKey().split("\\r?\\n");
            Integer qty = Integer.parseInt(entry.getValue().toString());
            Integer price = Integer.parseInt(menu.get(orderItem[0]));
            items.add(entry.getKey()+"\nQuantity:"+qty);
            total += (price*qty);

            Map<String, String> billItem = new HashMap<>();
            billItem.put("name", orderItem[0]);
            billItem.put("price", price.toString());
            billItem.put("quantity", qty.toString());
            bill.add(billItem);
//            count.add(entry.getValue().toString());
        }
//        itemCount = findViewById(R.id.lv_count);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
//        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,count);
        itemList.setAdapter(adapter);
        if(total == 0) {
            tvTotal.setText("Cart is empty");
            btOrder.setVisibility(View.GONE);
        }
        else {
            tvTotal.setText("Total Amount : Rs:"+total.toString());
            btOrder.setVisibility(View.VISIBLE);
        }
//        itemCount.setAdapter(adapter2);
        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = sharedPref.getString("user_email","");
                date = new Date();
                formattedDate = formatter.format(date);
                cardView.setVisibility(View.VISIBLE);
                tvStatus.setText("Your order is being prepared");
                tvStatus.setBackgroundColor(Color.DKGRAY);

                Map<String, Object> docData = new HashMap<>();
                docData.put("hotelId",hotelId);
                docData.put("items", bill);
                docData.put("status", "cooking");
                docData.put("totalCost", total);
                docData.put("userId", userId);
                docData.put("time", formattedDate);

                showProgressDialog();
                db.collection("Order")
                        .document()
                        .set(docData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                hideProgressDialog();
                                Toast.makeText(OrderActivity.this, "Your order has been sent!!", Toast.LENGTH_SHORT).show();
                                btOrder.setEnabled(false);
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressDialog();
                                Toast.makeText(OrderActivity.this, "Failed to send your order ", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                db.collection("Order")
                        .whereEqualTo("userId",userId)
                        .whereEqualTo("hotelId",hotelId)
                        .whereEqualTo("time", formattedDate)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                               for(DocumentSnapshot snapshot : snapshots) {
                                   Log.d(TAG, snapshot.getData().toString());
                                   String source = snapshots != null && snapshots.getMetadata().hasPendingWrites()
                                           ? "Local" : "Server";

                                   if (snapshot != null && snapshot.exists()) {
//                                       Toast.makeText(OrderActivity.this, snapshot.getData().toString(), Toast.LENGTH_SHORT).show();
                                       if(snapshot.get("status").equals("paymentPending")) {
                                           Log.d(TAG, "inside if paymentPending: " + snapshot.getData().toString());
                                            tvStatus.setText("Your item is ready! Please be ready with the cash!");
                                            tvStatus.setBackgroundColor(Color.MAGENTA);
                                       }
                                       else if(snapshot.get("status").equals("completed")) {
                                           Log.d(TAG, "inside if completed: " + snapshot.getData().toString());
//                                           db.collection("Order")
//                                                   .document(snapshot.getId())
//                                                   .delete()
//                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                       @Override
//                                                       public void onSuccess(Void aVoid) {
//                                                           tvStatus.setText("Payment successfully done! Thank you!");
//                                                           tvStatus.setBackgroundColor(Color.GREEN);
//                                                           Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                                                       }
//                                                   })
//                                                   .addOnFailureListener(new OnFailureListener() {
//                                                       @Override
//                                                       public void onFailure(@NonNull Exception e) {
//                                                           Log.w(TAG, "Error deleting document", e);
//                                                       }
//                                                   });
                                           tvStatus.setText("Payment successfully done! Thank you!");
                                           tvStatus.setBackgroundColor(Color.GREEN);

                                       }

                                       Log.d(TAG, source + " data: " + snapshot.getData());
                                   } else {
                                       Log.d(TAG, source + " data: null");
                                   }
                               }
                            }
                        });

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sign_out)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        if(item.getItemId() == R.id.order_list)
        {
            Intent intent = new Intent(getApplicationContext(), OrderStatusActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}

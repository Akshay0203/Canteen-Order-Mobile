package com.nmamit.canteenorder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends BaseActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton btOrder;
    private RecyclerView recyclerView;
    private TextView tvItemCount;
    private HashMap<String,Integer> orderedItems = new HashMap<>();

    private static final String TAG = "MenuActivity";
    private ArrayList<String> menuItem = new ArrayList<>();
    private MenuAdapter adapter;
    private HashMap<String, String> menu;
    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btOrder = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.menu_view);
        tvItemCount = findViewById(R.id.tv_item_count);
        rl = findViewById(R.id.cart_view);
        tvItemCount.setVisibility(View.INVISIBLE);
        final String hotelEmail = getIntent().getStringExtra("hotelEmail");
//        Toast.makeText(this, hotelEmail, Toast.LENGTH_SHORT).show();
        showProgressDialog();

        db.collection("User")
                .document(hotelEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()  {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                menu = (HashMap<String, String>) document.get("menu");
                                for (Map.Entry<String, String> e : menu.entrySet()) {
//                                    itemName.add(e.getKey().toString());
//                                    itemPrice.add(e.getValue().toString());
                                    menuItem.add(e.getKey().toString() + "\nRs."+e.getValue().toString());
                                }
//                                Toast.makeText(MenuActivity.this, menuItem.size() + "", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Log.d(TAG, "Menu not found");
                            hideProgressDialog();
                            initAdapter();
                        } else {
                            hideProgressDialog();
                            Toast.makeText(MenuActivity.this, "Error in fetching menu", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer order = new StringBuffer();

                for(Map.Entry<String,Integer> e : orderedItems.entrySet())
                    order.append(e.getKey() + " "+ e.getValue()+"\n");

//                Toast.makeText(MenuActivity.this, order.toString(), Toast.LENGTH_SHORT).show();
                Intent orderIntent = new Intent(getApplicationContext(), OrderActivity.class);
                orderIntent.putExtra("orderItems", orderedItems);
                orderIntent.putExtra("hotelId",hotelEmail);
                orderIntent.putExtra("menu", menu);
                startActivity(orderIntent);
            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NewApi")
    public void initAdapter() {
        adapter = new MenuAdapter(this, menuItem, tvItemCount, orderedItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}

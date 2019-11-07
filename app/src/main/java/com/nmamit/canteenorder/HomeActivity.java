package com.nmamit.canteenorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> hotelId;
    private ArrayList<String> hotelNames;
    private ArrayList<Integer> hotelImages;
    private ArrayList<String> hotelAddress;
    private ArrayList<String> hotelEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        mAdapter = new MyAdapter(getApplicationContext(),myDataset);
//        recyclerView.setAdapter(mAdapter);
        initHotelValues();

//        Toast.makeText(this, hotelNames.size()+" ", Toast.LENGTH_SHORT).show();
    }

    private void initAdapter() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(getApplicationContext(),hotelNames, hotelAddress, hotelEmail, hotelId);
        recyclerView.setAdapter(mAdapter);
    }


    private void initHotelValues() {
        hotelNames = new ArrayList<String>();
        hotelImages = new ArrayList<Integer>();
        hotelAddress = new ArrayList<String>();
        hotelEmail = new ArrayList<String>();
        hotelId = new ArrayList<String>();
//        hotelImages.add(R.drawable.h1);
//        hotelImages.add(R.drawable.h2);
//        hotelNames.add("Hotel 1");
//        hotelNames.add("Hotel 2");

//        Query query = userDataRef.whereEqualTo("type","Hotel");

        showProgressDialog();
        db.collection("User")
                .whereEqualTo("type", "Hotel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String address = document.get("address").toString();
                                String name = document.get("name").toString();
                                String userId = document.get("userId").toString();
                                String email = document.getId();
//                                Toast.makeText(HomeActivity.this, name, Toast.LENGTH_SHORT).show();

                                hotelId.add(userId);
                                hotelAddress.add(address);
                                hotelNames.add(name);
                                hotelEmail.add(email);
                            }
                            hideProgressDialog();
                            initAdapter();
                        } else {
                            hideProgressDialog();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

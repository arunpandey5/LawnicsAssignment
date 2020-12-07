package com.example.lawnics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PhotoList extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<PhotoModel> photoList = new ArrayList<>();
    FloatingActionButton openCamera;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        openCamera = findViewById(R.id.openCamera);
        recyclerView = findViewById(R.id.photoRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://lawnics-f6b93-default-rtdb.firebaseio.com/").child("ImageList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PhotoModel photoModel = dataSnapshot.getValue(PhotoModel.class);
                        photoList.add(photoModel);
                    }
                    photoAdapter = new PhotoAdapter(photoList);
                    recyclerView.setAdapter(photoAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoList.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
}
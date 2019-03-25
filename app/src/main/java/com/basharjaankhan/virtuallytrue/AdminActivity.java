package com.basharjaankhan.virtuallytrue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private DatabaseReference UserRef;
    private ListView listView;

    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.playerList);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String entry = "";
                    int coins = 0;
                    Map user = (HashMap) snap.getValue();
                    String userName = user.get("fullname").toString();
                    Log.i("user", userName);
                    entry += userName;
                    Map transactions = (HashMap) user.get("transactions");
                    if (transactions != null) {
                        for (Object transaction : transactions.values()) {
                            Map transactionMap = (HashMap) transaction;
                            Log.i("transaction", transactionMap.toString());
                            coins += Integer.parseInt(transactionMap.get("coins").toString());
                        }
                    }
                    entry += " : " + coins;
                    list.add(entry);
                }

                Log.i("Complete List", list.toString());

                adapter = new ArrayAdapter<String>(AdminActivity.this, android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}

package com.example.virtuallytrue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private TextView textViewPlayerName, textViewCoins, textViewCollegeName;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference UserRef, EventRef;
    private String currentUserID;

    private String saveCurrentDate, saveCurrentTime, randomTrasactionID;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scanButton = findViewById(R.id.scanButton);
        textViewPlayerName = findViewById(R.id.textViewPlayerName);
        textViewCollegeName = findViewById(R.id.textViewCollegeName);
        textViewCoins = findViewById(R.id.textViewCoins);
        navigationView = findViewById(R.id.navigation_view);
        mToolbar = findViewById(R.id.main_page_toolbar);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        EventRef = FirebaseDatabase.getInstance().getReference().child("Events");

        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Virtually True");
        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = navigationView.inflateHeaderView(R.layout.navigation_header);

        mAuth = FirebaseAuth.getInstance();

        qrScan = new IntentIntegrator(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.setOrientationLocked(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan(Arrays.asList("QR_CODE"));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                Toast.makeText(this, "Profile pressed", Toast.LENGTH_SHORT).show();
                SendUserToProfileActivity();
                break;
            case R.id.nav_admin:
                Toast.makeText(this, "Admin pressed", Toast.LENGTH_SHORT).show();
                SendUserToAdminActivity();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "About pressed", Toast.LENGTH_SHORT).show();
                SendUserToAboutActivity();
                break;
            case R.id.nav_developer:
                Toast.makeText(this, "Developer pressed", Toast.LENGTH_SHORT).show();
                SendUserToDeveloperActivity();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToAdminActivity() {
        Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
        startActivity(adminIntent);

    }

    private void SendUserToDeveloperActivity() {
        Intent developerIntent = new Intent(MainActivity.this, DeveloperActivity.class);
        startActivity(developerIntent);

    }

    private void SendUserToAboutActivity() {
        Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(aboutIntent);

    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);

    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendusertoLoginActiviy();
        } else {
            CheckUserExistence();
            updateCoins();
        }
    }

    private void CheckUserExistence() {
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.hasChild(currentUserID))) {
                    SendUserToSetupActivity();
                } else {
                    String fullname = dataSnapshot.child(currentUserID).child("fullname").getValue().toString();
                    String collegename = dataSnapshot.child(currentUserID).child("collegename").getValue().toString();

                    textViewPlayerName.setText(fullname);
                    textViewCollegeName.setText(collegename);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            // qrcode has nothing
            if (result.getContents() == null) {
                Toast.makeText(this, "Result not found.", Toast.LENGTH_SHORT).show();
            } else {
                // qr code has data
                try {
                    String encoded = new String(Base64.decode(result.getContents(), Base64.DEFAULT), "UTF-8");

                    JSONObject jsonObject = new JSONObject(encoded);


                    final String eventName = jsonObject.get("eventName").toString();
                    final String crateName = jsonObject.get("crateName").toString();

                    EventRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("snap", dataSnapshot.child(eventName).toString());
                            final int coins = Integer.parseInt(dataSnapshot.child(eventName).child(crateName).getValue().toString());

//                    coins = jsonObject.get("coins").toString();

                            // setting values to textViews
                            textViewCoins.setText("" + coins);


                            Calendar calFordDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            saveCurrentDate = currentDate.format(calFordDate.getTime());

                            Calendar calFordTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                            saveCurrentTime = currentTime.format(calFordDate.getTime());

                            randomTrasactionID = saveCurrentDate + saveCurrentTime;

                            Map transaction = new HashMap();
                            transaction.put("timestamp", randomTrasactionID);
                            transaction.put("coins", coins);

                            UserRef.child(currentUserID).child("transactions").child(eventName).updateChildren(transaction);
                            updateCoins();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                    // encoded format mismatch
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateCoins() {

        UserRef.child(currentUserID).child("transactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int coins = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Map sub = (HashMap) d.getValue();
                    coins += Integer.parseInt(sub.get("coins").toString());
                }
                textViewCoins.setText("" + coins);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendusertoLoginActiviy() {
        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

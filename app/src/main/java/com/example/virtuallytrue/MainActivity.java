package com.example.virtuallytrue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private TextView textViewEventName, textViewCoins;
    private FirebaseAuth mAuth;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scanButton = findViewById(R.id.scanButton);
        textViewEventName = findViewById(R.id.textViewEventName);
        textViewCoins = findViewById(R.id.textViewCoins);

        mAuth = FirebaseAuth.getInstance();

        qrScan = new IntentIntegrator(this);



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
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendusertoLoginActiviy();
        }
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
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    // setting values to textViews
                    textViewEventName.setText(jsonObject.getString("eventName"));
                    textViewCoins.setText(jsonObject.getString("coins"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    // encoded format mismatch
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendusertoLoginActiviy() {
        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

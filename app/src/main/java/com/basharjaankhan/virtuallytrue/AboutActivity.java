package com.basharjaankhan.virtuallytrue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        description = findViewById(R.id.about_description);

        description.setText(Html.fromHtml(getString(R.string.description)));
    }
}

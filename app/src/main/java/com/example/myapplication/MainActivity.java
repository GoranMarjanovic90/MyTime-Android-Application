package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button updateButton;
    private TextView systemTimeTextView;
    private TextView ntpTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateButton = findViewById(R.id.updateButton);
        systemTimeTextView = findViewById(R.id.systemTimeTextView);
        ntpTimeTextView = findViewById(R.id.ntpTimeTextView);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date systemDate = new Date();
                SimpleDateFormat systemDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedSystemTime = systemDateFormat.format(systemDate);


                systemTimeTextView.setText("System Time: " + formattedSystemTime);


                String ntpTime = "NTP Time: ";

                ntpTimeTextView.setText(ntpTime);
            }
        });
    }
}
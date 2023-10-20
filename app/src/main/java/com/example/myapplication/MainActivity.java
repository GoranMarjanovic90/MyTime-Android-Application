package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
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

                // Start a background thread to fetch NTP time
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String ntpTime = getNtpTime();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ntpTimeTextView.setText("NTP Time: " + ntpTime);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private String getNtpTime() {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(5000);

        try {
            client.open();
            TimeInfo timeInfo = client.getTime(InetAddress.getByName("pool.ntp.org"));

            long ntpTimeMillis = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date ntpDate = new Date(ntpTimeMillis);
            SimpleDateFormat ntpDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            return ntpDateFormat.format(ntpDate);
        } catch (IOException e) {
            e.printStackTrace();
            return "I'm offline!";
        } finally {
            client.close();
        }
    }
}
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
    private Button pauseResumeButton;
    private TextView systemTimeTextView;
    private TextView ntpTimeTextView;

    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        updateButton = findViewById(R.id.updateButton);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        systemTimeTextView = findViewById(R.id.systemTimeTextView);
        ntpTimeTextView = findViewById(R.id.ntpTimeTextView);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPaused) {

                    Date systemDate = new Date();
                    SimpleDateFormat systemDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                    String formattedSystemTime = systemDateFormat.format(systemDate);
                    systemTimeTextView.setText("System Time: " + formattedSystemTime);
                    systemTimeTextView.setTextSize(20);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            final String ntpTime = getNtpTime();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ntpTimeTextView.setText("NTP Time: " + ntpTime);
                                    ntpTimeTextView.setTextSize(20);
                                }
                            });
                        }
                    }).start();
                }
            }
        });


        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = !isPaused;

                pauseResumeButton.setText(isPaused ? "Resume" : "Pause");
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
            return "Failed to fetch NTP time";
        } finally {
            client.close();
        }
    }
}
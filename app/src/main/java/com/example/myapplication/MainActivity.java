package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView timeTextView;
    private Button pauseResumeButton;

    private boolean isPaused = false;
    private Handler handler;
    private Runnable timeUpdateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = findViewById(R.id.timeTextView);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);

        handler = new Handler();
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateAndScheduleNextTimeUpdate();
            }
        };

        updatePauseResumeButtonText();

        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = !isPaused;
                updatePauseResumeButtonText();
                if (!isPaused) {
                    updateAndScheduleNextTimeUpdate();
                }
            }
        });

        updateAndScheduleNextTimeUpdate();
    }

    private void updateAndScheduleNextTimeUpdate() {
        if (!isPaused) {
            if (isOnline()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String ntpTime = getNtpTime(getApplicationContext());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeTextView.setText("NTP Time: " + ntpTime);
                                timeTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                timeTextView.setTextSize(20);
                            }
                        });
                    }
                }).start();
            } else {
                Date systemDate = new Date();
                SimpleDateFormat systemDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedSystemTime = systemDateFormat.format(systemDate);
                timeTextView.setText("System Time: " + formattedSystemTime);
                timeTextView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                timeTextView.setTextSize(20);
            }

            handler.postDelayed(timeUpdateRunnable, 1000);
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getNtpTime(Context context) {
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
            showToastException("NTP time fetch failed. Please check your network.", context);
            return "Failed to fetch NTP time";
        } finally {
            client.close();
        }
    }

    private void showToastException(final String message, final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePauseResumeButtonText() {
        pauseResumeButton.setText(isPaused ? "Resume" : "Pause");
    }
}

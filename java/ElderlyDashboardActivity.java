package com.carebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ElderlyDashboardActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_dashboard);

        session = new SessionManager(this);

        // Greet user
        TextView tvHello = findViewById(R.id.tv_hello);
        String name = session.getName();
        tvHello.setText("Hello, " + (name.isEmpty() ? "Friend" : name) + "!");

        // Card navigation
        CardView cardMeds      = findViewById(R.id.card_meds);
        CardView cardLogs      = findViewById(R.id.card_logs);
        CardView cardMood      = findViewById(R.id.card_mood);
        CardView cardAiTips    = findViewById(R.id.card_ai_tips);
        CardView cardSOS       = findViewById(R.id.card_sos);
        CardView cardProfile   = findViewById(R.id.card_profile);

        // Bottom nav
        TextView navHome    = findViewById(R.id.nav_home);
        TextView navMeds    = findViewById(R.id.nav_meds);
        TextView navLogs    = findViewById(R.id.nav_logs);
        TextView navProfile = findViewById(R.id.nav_profile);

        cardMeds.setOnClickListener(v ->
            startActivity(new Intent(this, MedicineActivity.class)));

        cardLogs.setOnClickListener(v ->
            startActivity(new Intent(this, AddLogActivity.class)));

        cardMood.setOnClickListener(v ->
            startActivity(new Intent(this, MoodTrackerActivity.class)));

        cardAiTips.setOnClickListener(v ->
            startActivity(new Intent(this, AIHealthTipsActivity.class)));

        cardSOS.setOnClickListener(v ->
            startActivity(new Intent(this, SOSActivity.class)));

        cardProfile.setOnClickListener(v ->
            startActivity(new Intent(this, ProfileActivity.class)));

        // Bottom navigation
        navMeds.setOnClickListener(v ->
            startActivity(new Intent(this, MedicineActivity.class)));
        navLogs.setOnClickListener(v ->
            startActivity(new Intent(this, AddLogActivity.class)));
        navProfile.setOnClickListener(v ->
            startActivity(new Intent(this, ProfileActivity.class)));
    }
}

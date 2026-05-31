// ═══════════════════════════════════════════════════════════
// FILE: SplashActivity.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager session = new SessionManager(this);

        new Handler().postDelayed(() -> {
            if (session.isLoggedIn()) {
                String role = session.getRole();
                navigateByRole(role);
            } else {
                startActivity(new Intent(this, RoleSelectionActivity.class));
            }
            finish();
        }, 2000);
    }

    private void navigateByRole(String role) {
        Intent intent;
        switch (role) {
            case "senior": intent = new Intent(this, ElderlyDashboardActivity.class); break;
            case "family": intent = new Intent(this, FamilyDashboardActivity.class);  break;
            case "doctor": intent = new Intent(this, DoctorDashboardActivity.class);  break;
            default:       intent = new Intent(this, RoleSelectionActivity.class);    break;
        }
        startActivity(intent);
    }
}


// ═══════════════════════════════════════════════════════════
// FILE: RoleSelectionActivity.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        LinearLayout cardSenior = findViewById(R.id.card_senior);
        LinearLayout cardFamily = findViewById(R.id.card_family);
        LinearLayout cardDoctor = findViewById(R.id.card_doctor);

        cardSenior.setOnClickListener(v -> openLogin("senior"));
        cardFamily.setOnClickListener(v -> openLogin("family"));
        cardDoctor.setOnClickListener(v -> openLogin("doctor"));
    }

    private void openLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}

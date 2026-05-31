// ═══════════════════════════════════════════════════════════
// FILE: LoginActivity.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private String role;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        role    = getIntent().getStringExtra("role");
        db      = new DatabaseHelper(this);
        session = new SessionManager(this);

        etEmail    = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin  = findViewById(R.id.btn_login);
        TextView tvSignUp = findViewById(R.id.tv_signup);

        // Show role in subtitle
        TextView tvSubtitle = findViewById(R.id.tv_subtitle);
        tvSubtitle.setText("Welcome back as " + capitalize(role));

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(pass))  { etPassword.setError("Required"); return; }

        if (db.loginUser(email, pass)) {
            User user = db.getUser(email);
            session.saveSession(email, user.getName(), role);
            navigateToDashboard();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent;
        switch (role) {
            case "senior": intent = new Intent(this, ElderlyDashboardActivity.class); break;
            case "family": intent = new Intent(this, FamilyDashboardActivity.class);  break;
            case "doctor": intent = new Intent(this, DoctorDashboardActivity.class);  break;
            default:       intent = new Intent(this, ElderlyDashboardActivity.class); break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}


// ═══════════════════════════════════════════════════════════
// FILE: SignUpActivity.java
// ═══════════════════════════════════════════════════════════
package com.carebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private String role;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        role    = getIntent().getStringExtra("role");
        db      = new DatabaseHelper(this);
        session = new SessionManager(this);

        etName     = findViewById(R.id.et_name);
        etEmail    = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnSignUp  = findViewById(R.id.btn_signup);
        TextView tvLogin  = findViewById(R.id.tv_login);

        TextView tvSubtitle = findViewById(R.id.tv_subtitle);
        tvSubtitle.setText("Create your account as " + capitalize(role));

        btnSignUp.setOnClickListener(v -> attemptSignUp());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void attemptSignUp() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name))  { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(pass))  { etPassword.setError("Required"); return; }
        if (pass.length() < 6)        { etPassword.setError("Min 6 characters"); return; }

        if (db.registerUser(name, email, pass, role)) {
            session.saveSession(email, name, role);
            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
            navigateToDashboard();
        } else {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent;
        switch (role) {
            case "senior": intent = new Intent(this, ElderlyDashboardActivity.class); break;
            case "family": intent = new Intent(this, FamilyDashboardActivity.class);  break;
            case "doctor": intent = new Intent(this, DoctorDashboardActivity.class);  break;
            default:       intent = new Intent(this, ElderlyDashboardActivity.class); break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}

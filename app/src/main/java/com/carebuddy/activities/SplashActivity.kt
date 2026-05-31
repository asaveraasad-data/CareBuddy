package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.carebuddy.R
import com.carebuddy.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val session = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (session.isLoggedIn()) {
                // Navigate to correct dashboard based on role
                navigateToDashboard(session.getUserRole())
            } else {
                startActivity(Intent(this, RoleSelectionActivity::class.java))
            }
            finish()
        }, 2000)
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "Senior" -> Intent(this, ElderlyDashboardActivity::class.java)
            "Family" -> Intent(this, FamilyDashboardActivity::class.java)
            "Doctor" -> Intent(this, DoctorDashboardActivity::class.java)
            else -> Intent(this, RoleSelectionActivity::class.java)
        }
        startActivity(intent)
    }
}

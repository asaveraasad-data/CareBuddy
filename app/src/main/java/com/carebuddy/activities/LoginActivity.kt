package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityLoginBinding
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private var selectedRole: String = "Senior"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)
        selectedRole = intent.getStringExtra("role") ?: "Senior"

        binding.tvRoleSubtitle.text = "Welcome back as $selectedRole"

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("role", selectedRole)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Password reset coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val user = db.userDao().login(email, password, selectedRole)

            runOnUiThread {
                binding.btnLogin.isEnabled = true
                binding.progressBar.visibility = View.GONE

                if (user != null) {
                    session.saveUserSession(user.id, user.fullName, user.email, user.role)
                    navigateToDashboard(user.role)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity,
                        "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "Senior" -> Intent(this, ElderlyDashboardActivity::class.java)
            "Family" -> Intent(this, FamilyDashboardActivity::class.java)
            "Doctor" -> Intent(this, DoctorDashboardActivity::class.java)
            else -> Intent(this, ElderlyDashboardActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

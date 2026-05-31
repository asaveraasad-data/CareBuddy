package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivitySignUpBinding
import com.carebuddy.models.User
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private var selectedRole: String = "Senior"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)
        selectedRole = intent.getStringExtra("role") ?: "Senior"

        binding.tvRoleSubtitle.text = "Create your account as $selectedRole"

        binding.btnSignUp.setOnClickListener {
            performSignUp()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun performSignUp() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSignUp.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val emailExists = db.userDao().emailExists(email)

            if (emailExists > 0) {
                runOnUiThread {
                    binding.btnSignUp.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@SignUpActivity,
                        "Email already registered", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val newUser = User(
                fullName = name,
                email = email,
                password = password,
                role = selectedRole
            )

            val userId = db.userDao().insert(newUser)

            runOnUiThread {
                binding.btnSignUp.isEnabled = true
                binding.progressBar.visibility = View.GONE

                session.saveUserSession(userId.toInt(), name, email, selectedRole)
                Toast.makeText(this@SignUpActivity,
                    "Account created successfully!", Toast.LENGTH_SHORT).show()

                val intent = when (selectedRole) {
                    "Senior" -> Intent(this@SignUpActivity, ElderlyDashboardActivity::class.java)
                    "Family" -> Intent(this@SignUpActivity, FamilyDashboardActivity::class.java)
                    "Doctor" -> Intent(this@SignUpActivity, DoctorDashboardActivity::class.java)
                    else -> Intent(this@SignUpActivity, ElderlyDashboardActivity::class.java)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}

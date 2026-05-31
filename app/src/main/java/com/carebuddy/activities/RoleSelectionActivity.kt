package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carebuddy.R
import com.carebuddy.databinding.ActivityRoleSelectionBinding

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardSenior.setOnClickListener {
            goToLogin("Senior")
        }

        binding.cardFamily.setOnClickListener {
            goToLogin("Family")
        }

        binding.cardDoctor.setOnClickListener {
            goToLogin("Doctor")
        }
    }

    private fun goToLogin(role: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("role", role)
        startActivity(intent)
    }
}

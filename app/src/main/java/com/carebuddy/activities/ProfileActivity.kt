package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityProfileBinding
import com.carebuddy.models.User
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        loadUserProfile()

        binding.ivBack.setOnClickListener { finish() }
        binding.btnEditProfile.setOnClickListener { enableEditing(true) }
        binding.btnSaveChanges.setOnClickListener { saveProfile() }
        binding.tvCancel.setOnClickListener {
            enableEditing(false)
            loadUserProfile()
        }

        binding.btnLogout.setOnClickListener {
            session.logout()
            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            currentUser = db.userDao().getUserById(session.getUserId())
            currentUser?.let { user ->
                runOnUiThread {
                    binding.etFullName.setText(user.fullName)
                    binding.etAge.setText(user.age.toString())
                    binding.etBloodGroup.setText(user.bloodGroup)
                    binding.etEmergencyContactName.setText(user.emergencyContactName)
                    binding.etEmergencyContactPhone.setText(user.emergencyContactPhone)
                    binding.etEmergencyRelation.setText(user.emergencyContactRelation)
                    binding.etMedicalConditions.setText(user.medicalConditions)
                    binding.etAllergies.setText(user.allergies)
                    binding.etCurrentMedications.setText(user.currentMedications)
                    binding.tvUserEmail.text = user.email
                    binding.tvUserRole.text = user.role
                }
            }
        }
    }

    private fun enableEditing(enable: Boolean) {
        val editableFields = listOf(
            binding.etFullName, binding.etAge, binding.etBloodGroup,
            binding.etEmergencyContactName, binding.etEmergencyContactPhone,
            binding.etEmergencyRelation, binding.etMedicalConditions,
            binding.etAllergies, binding.etCurrentMedications
        )

        editableFields.forEach { field ->
            field.isEnabled = enable
            field.isFocusableInTouchMode = enable
        }

        binding.btnEditProfile.visibility = if (enable) View.GONE else View.VISIBLE
        binding.layoutSaveCancel.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun saveProfile() {
        val name = binding.etFullName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            currentUser?.let { user ->
                val updatedUser = user.copy(
                    fullName = name,
                    age = binding.etAge.text.toString().toIntOrNull() ?: user.age,
                    bloodGroup = binding.etBloodGroup.text.toString().trim(),
                    emergencyContactName = binding.etEmergencyContactName.text.toString().trim(),
                    emergencyContactPhone = binding.etEmergencyContactPhone.text.toString().trim(),
                    emergencyContactRelation = binding.etEmergencyRelation.text.toString().trim(),
                    medicalConditions = binding.etMedicalConditions.text.toString().trim(),
                    allergies = binding.etAllergies.text.toString().trim(),
                    currentMedications = binding.etCurrentMedications.text.toString().trim()
                )
                db.userDao().update(updatedUser)
                currentUser = updatedUser

                runOnUiThread {
                    enableEditing(false)
                    Toast.makeText(this@ProfileActivity,
                        "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

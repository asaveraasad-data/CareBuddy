package com.carebuddy.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivitySosBinding
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class SOSActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySosBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private var countDownTimer: CountDownTimer? = null
    private var isSOSActive = false

    companion object {
        const val CALL_PERMISSION_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        setupSOSButton()
        loadEmergencyContacts()
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setupSOSButton() {
        binding.btnSOS.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startSOSCountdown()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    cancelSOSCountdown()
                    true
                }
                else -> false
            }
        }
    }

    private fun startSOSCountdown() {
        if (isSOSActive) return
        isSOSActive = true
        binding.tvSOSInstruction.text = "Hold... 3"

        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000) + 1
                runOnUiThread {
                    binding.tvSOSInstruction.text = "Hold... $secondsLeft"
                    binding.btnSOS.animate().scaleX(1.1f).scaleY(1.1f).duration = 200
                }
            }

            override fun onFinish() {
                runOnUiThread {
                    triggerSOS()
                }
            }
        }.start()
    }

    private fun cancelSOSCountdown() {
        countDownTimer?.cancel()
        isSOSActive = false
        binding.tvSOSInstruction.text = "Press and hold for 3 seconds to activate emergency alert"
        binding.btnSOS.animate().scaleX(1f).scaleY(1f).duration = 200
    }

    private fun triggerSOS() {
        binding.cardAlertSent.visibility = android.view.View.VISIBLE
        binding.tvSOSInstruction.text = "Emergency Alert Activated!"

        // Call emergency contact
        lifecycleScope.launch {
            val user = db.userDao().getUserById(session.getUserId())
            user?.let {
                if (it.emergencyContactPhone.isNotEmpty()) {
                    runOnUiThread {
                        makeCall(it.emergencyContactPhone)
                    }
                } else {
                    runOnUiThread {
                        makeCall("911")
                    }
                }
            }
        }
    }

    private fun loadEmergencyContacts() {
        lifecycleScope.launch {
            val user = db.userDao().getUserById(session.getUserId())
            user?.let {
                runOnUiThread {
                    binding.tvEmergencyName.text = it.emergencyContactName.ifEmpty { "Emergency Contact" }
                    binding.tvEmergencyPhone.text = it.emergencyContactPhone.ifEmpty { "Not set" }
                    binding.tvEmergencyRelation.text = it.emergencyContactRelation.ifEmpty { "Contact" }

                    // Medical info
                    binding.tvBloodType.text = it.bloodGroup.ifEmpty { "Not set" }
                    binding.tvAllergies.text = it.allergies.ifEmpty { "None" }
                    binding.tvMedications.text = it.currentMedications.ifEmpty { "None" }
                }
            }
        }

        // Call buttons
        binding.btnCallEmergency.setOnClickListener {
            makeCall("911")
        }

        binding.btnCallContact.setOnClickListener {
            lifecycleScope.launch {
                val user = db.userDao().getUserById(session.getUserId())
                val phone = user?.emergencyContactPhone ?: ""
                runOnUiThread {
                    if (phone.isNotEmpty()) makeCall(phone)
                    else Toast.makeText(this@SOSActivity,
                        "No emergency contact set. Go to Profile to add one.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun makeCall(number: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST
            )
            return
        }
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            triggerSOS()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}

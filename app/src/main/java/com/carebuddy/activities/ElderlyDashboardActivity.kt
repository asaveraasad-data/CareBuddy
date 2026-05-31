package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityElderlyDashboardBinding
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ElderlyDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElderlyDashboardBinding
    private lateinit var session: SessionManager
    private lateinit var db: CareBuddyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElderlyDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        db = CareBuddyDatabase.getDatabase(this)

        setupUI()
        setupNavigation()
        loadDashboardData()
    }

    private fun setupUI() {
        val name = session.getUserName().split(" ").firstOrNull() ?: "there"
        binding.tvGreeting.text = "Hello, $name!"

        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date())
    }

    private fun setupNavigation() {
        // Bottom Nav
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_meds -> {
                    startActivity(Intent(this, MedicineActivity::class.java))
                    true
                }
                R.id.nav_logs -> {
                    startActivity(Intent(this, HealthLogsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Quick Action Cards
        binding.cardHealthLogs.setOnClickListener {
            startActivity(Intent(this, AddLogActivity::class.java))
        }
        binding.cardMoodTracker.setOnClickListener {
            startActivity(Intent(this, MoodTrackerActivity::class.java))
        }
        binding.cardAiTips.setOnClickListener {
            startActivity(Intent(this, AIHealthTipsActivity::class.java))
        }
        binding.cardSos.setOnClickListener {
            startActivity(Intent(this, SOSActivity::class.java))
        }

        // Notification icon
        binding.ivNotification.setOnClickListener {
            // Future: show notifications
        }
    }

    private fun loadDashboardData() {
        val userId = session.getUserId()

        lifecycleScope.launch {
            // Load today's medicines
            val todayMeds = db.medicineDao().getMedicinesByTimeSlot(userId, "Morning") +
                    db.medicineDao().getMedicinesByTimeSlot(userId, "Afternoon") +
                    db.medicineDao().getMedicinesByTimeSlot(userId, "Evening") +
                    db.medicineDao().getMedicinesByTimeSlot(userId, "Night")

            // Load recent BP logs for chart
            val bpLogs = db.healthLogDao().getLast7LogsByType(userId, "Blood Pressure")

            runOnUiThread {
                // Update medication card
                if (todayMeds.isNotEmpty()) {
                    binding.tvMedCount.text = "${todayMeds.size} medications due"
                    val nextMed = todayMeds.firstOrNull()
                    binding.tvNextMed.text = "Next: ${nextMed?.name} at ${nextMed?.timeValue}"
                } else {
                    binding.tvMedCount.text = "No medications today"
                    binding.tvNextMed.text = "All done for today!"
                }

                // Calculate simple health score (demo logic)
                val healthScore = calculateHealthScore(bpLogs.size)
                binding.tvHealthScore.text = "$healthScore"
                binding.tvHealthStatus.text = when {
                    healthScore >= 80 -> "Good health status"
                    healthScore >= 60 -> "Fair health status"
                    else -> "Needs attention"
                }

                // Update BP chart data
                updateBPChart(bpLogs)
            }
        }

        // Observe medicine changes live
        db.medicineDao().getMedicinesForUser(userId).observe(this) { medicines ->
            val activeCount = medicines.size
            binding.tvMedCount.text = "$activeCount medications due"
        }
    }

    private fun calculateHealthScore(dataPoints: Int): Int {
        // Simple rule-based score (in real app, calculate from actual health data)
        return when {
            dataPoints >= 5 -> 85
            dataPoints >= 3 -> 72
            dataPoints >= 1 -> 65
            else -> 60
        }
    }

    private fun updateBPChart(logs: List<com.carebuddy.models.HealthLog>) {
        // Chart values are shown in layout; real chart integration via MPAndroidChart
        if (logs.isEmpty()) {
            binding.tvBpTrend.text = "No data yet - start logging!"
        } else {
            binding.tvBpTrend.text = "↗ Stable trend - Looking good!"
        }
    }
}

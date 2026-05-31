package com.carebuddy.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityAiHealthTipsBinding
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

data class HealthTip(
    val category: String,
    val title: String,
    val description: String,
    val status: String,        // "Good", "Monitor", "Alert"
    val recommendations: List<String>
)

class AIHealthTipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiHealthTipsBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiHealthTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        binding.ivBack.setOnClickListener { finish() }
        binding.btnGenerateTips.setOnClickListener {
            generateHealthTips()
        }

        // Load last entered health data
        binding.btnChatAssistant.setOnClickListener {
            Toast.makeText(this, "AI Chat Assistant coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateHealthTips() {
        val age = binding.etAge.text.toString().trim()
        val bp = binding.etBloodPressure.text.toString().trim()
        val sugar = binding.etBloodSugar.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()

        if (age.isEmpty()) {
            Toast.makeText(this, "Please enter at least your age", Toast.LENGTH_SHORT).show()
            return
        }

        val tips = mutableListOf<HealthTip>()

        // Rule-based BP analysis
        if (bp.isNotEmpty()) {
            val bpTip = analyzeBP(bp)
            tips.add(bpTip)
        }

        // Rule-based blood sugar
        if (sugar.isNotEmpty()) {
            val sugarTip = analyzeSugar(sugar.toIntOrNull() ?: 0)
            tips.add(sugarTip)
        }

        // Rule-based weight/BMI
        if (weight.isNotEmpty() && age.isNotEmpty()) {
            val weightTip = analyzeWeight(weight.toIntOrNull() ?: 0)
            tips.add(weightTip)
        }

        // Default tips based on age
        tips.addAll(getAgeTips(age.toIntOrNull() ?: 65))

        displayTips(tips)

        // Also save to DB as health logs
        saveHealthData(bp, sugar, weight)
    }

    private fun analyzeBP(bp: String): HealthTip {
        return try {
            val parts = bp.split("/")
            val systolic = parts[0].toInt()
            val diastolic = parts[1].toInt()

            when {
                systolic < 120 && diastolic < 80 -> HealthTip(
                    "Cardiovascular", "Your BP is Normal", "Good",
                    "Your blood pressure reading of $bp mmHg is within healthy range.",
                    listOf("Continue your current routine", "Maintain low-sodium diet", "Exercise 30 minutes daily")
                )
                systolic in 120..129 && diastolic < 80 -> HealthTip(
                    "Cardiovascular", "BP Slightly Elevated", "Monitor",
                    "Your blood pressure is slightly above normal. Monitor regularly.",
                    listOf("Reduce sodium intake", "Practice relaxation techniques", "Increase physical activity")
                )
                else -> HealthTip(
                    "Cardiovascular", "High Blood Pressure Detected", "Alert",
                    "Your blood pressure reading requires attention. Consult your doctor.",
                    listOf("Consult your doctor immediately", "Avoid high-sodium foods", "Monitor daily")
                )
            }
        } catch (e: Exception) {
            HealthTip("Cardiovascular", "BP Data Error", "Monitor",
                "Could not parse BP value. Enter as systolic/diastolic (e.g., 120/80).",
                listOf("Re-enter BP in correct format"))
        }
    }

    private fun analyzeSugar(sugar: Int): HealthTip {
        return when {
            sugar in 70..99 -> HealthTip(
                "Nutrition", "Blood Sugar Normal", "Good",
                "Your fasting blood sugar is in the healthy range.",
                listOf("Maintain balanced diet", "Eat at regular intervals", "Stay hydrated")
            )
            sugar in 100..125 -> HealthTip(
                "Nutrition", "Monitor Your Sugar", "Monitor",
                "Your blood sugar is slightly elevated. Watch your carbohydrate intake.",
                listOf("Reduce sugar and refined carbs", "Check levels after meals", "Consult your doctor if it persists")
            )
            else -> HealthTip(
                "Nutrition", "High Blood Sugar Alert", "Alert",
                "Your blood sugar level is high. Please consult your doctor.",
                listOf("Consult doctor immediately", "Avoid sugary foods", "Monitor daily")
            )
        }
    }

    private fun analyzeWeight(weight: Int): HealthTip {
        return when {
            weight in 50..85 -> HealthTip(
                "Fitness", "Healthy Weight Range", "Good",
                "Your BMI appears to be in the healthy range. Keep up the good work!",
                listOf("Maintain balanced diet", "Stay physically active", "Monitor weight weekly")
            )
            weight > 85 -> HealthTip(
                "Fitness", "Weight Management", "Monitor",
                "Consider working toward a healthier weight for better overall health.",
                listOf("Try gentle walks daily", "Reduce portion sizes", "Consult a nutritionist")
            )
            else -> HealthTip(
                "Fitness", "Maintain Your Weight", "Monitor",
                "Ensure you are getting adequate nutrition.",
                listOf("Eat nutrient-rich foods", "Consult a doctor about healthy weight goals")
            )
        }
    }

    private fun getAgeTips(age: Int): List<HealthTip> {
        val tips = mutableListOf<HealthTip>()

        tips.add(HealthTip(
            "Activity", "Stay Active",
            "Regular physical activity improves mood and overall health.",
            "Good",
            listOf(
                "Try gentle stretching exercises",
                "Join a local senior fitness class",
                "Set a goal of 5,000 steps per day"
            )
        ))

        tips.add(HealthTip(
            "Sleep", "Better Sleep Quality",
            "Quality sleep is essential for recovery and mental health.",
            "Good",
            listOf(
                "Maintain a consistent sleep schedule",
                "Avoid caffeine after 2 PM",
                "Create a relaxing bedtime routine"
            )
        ))

        if (age >= 65) {
            tips.add(HealthTip(
                "Prevention", "Fall Prevention",
                "Preventing falls is crucial for seniors.",
                "Good",
                listOf(
                    "Remove trip hazards at home",
                    "Use grab bars in bathroom",
                    "Do balance exercises daily"
                )
            ))
        }

        return tips
    }

    private fun displayTips(tips: List<HealthTip>) {
        binding.layoutTipsResult.visibility = View.VISIBLE
        binding.tipsContainer.removeAllViews()

        tips.forEach { tip ->
            val tipView = layoutInflater.inflate(com.carebuddy.R.layout.item_health_tip, binding.tipsContainer, false)
            // Set tip data to the view
            val tvCategory = tipView.findViewById<android.widget.TextView>(com.carebuddy.R.id.tvCategory)
            val tvTitle = tipView.findViewById<android.widget.TextView>(com.carebuddy.R.id.tvTitle)
            val tvDescription = tipView.findViewById<android.widget.TextView>(com.carebuddy.R.id.tvDescription)
            val tvStatus = tipView.findViewById<android.widget.TextView>(com.carebuddy.R.id.tvStatus)
            val tvRecommendations = tipView.findViewById<android.widget.TextView>(com.carebuddy.R.id.tvRecommendations)

            tvCategory.text = tip.category
            tvTitle.text = tip.title
            tvDescription.text = tip.description
            tvStatus.text = tip.status

            val statusColor = when (tip.status) {
                "Good" -> getColor(com.carebuddy.R.color.status_good)
                "Monitor" -> getColor(com.carebuddy.R.color.status_monitor)
                "Alert" -> getColor(com.carebuddy.R.color.status_alert)
                else -> getColor(com.carebuddy.R.color.text_secondary)
            }
            tvStatus.setTextColor(statusColor)
            tvRecommendations.text = tip.recommendations.joinToString("\n") { "• $it" }

            binding.tipsContainer.addView(tipView)
        }
    }

    private fun saveHealthData(bp: String, sugar: String, weight: String) {
        val userId = session.getUserId()
        lifecycleScope.launch {
            if (bp.isNotEmpty()) {
                db.healthLogDao().insert(com.carebuddy.models.HealthLog(
                    userId = userId, metricType = "Blood Pressure", value = bp, unit = "mmHg"
                ))
            }
            if (sugar.isNotEmpty()) {
                db.healthLogDao().insert(com.carebuddy.models.HealthLog(
                    userId = userId, metricType = "Blood Sugar", value = sugar, unit = "mg/dL"
                ))
            }
        }
    }
}

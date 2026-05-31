package com.carebuddy.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityAddLogBinding
import com.carebuddy.models.HealthLog
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class AddLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLogBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private var selectedMetric: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        setupMetricCards()
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setupMetricCards() {
        val metricCards = listOf(
            binding.cardBloodPressure to Pair("Blood Pressure", "mmHg"),
            binding.cardBloodSugar to Pair("Blood Sugar", "mg/dL"),
            binding.cardHeartRate to Pair("Heart Rate", "bpm"),
            binding.cardTemperature to Pair("Temperature", "°F")
        )

        metricCards.forEach { (card, metricPair) ->
            card.setOnClickListener {
                selectedMetric = metricPair.first
                showInputForm(metricPair.first, metricPair.second)
                updateCardSelection(metricCards, card)
            }
        }
    }

    private fun updateCardSelection(
        cards: List<Pair<androidx.cardview.widget.CardView, Pair<String, String>>>,
        selectedCard: androidx.cardview.widget.CardView
    ) {
        cards.forEach { (card, _) ->
            card.strokeWidth = if (card == selectedCard) 4 else 0
            card.strokeColor = if (card == selectedCard)
                getColor(com.carebuddy.R.color.primary_blue) else 0
        }
    }

    private fun showInputForm(metric: String, unit: String) {
        binding.layoutInputForm.visibility = View.VISIBLE
        binding.tvInputLabel.text = "Enter $metric value ($unit)"
        binding.tvUnitHint.text = unit

        val hint = when (metric) {
            "Blood Pressure" -> "e.g., 120/80"
            "Blood Sugar" -> "e.g., 95"
            "Heart Rate" -> "e.g., 72"
            "Temperature" -> "e.g., 98.6"
            else -> ""
        }
        binding.etValue.hint = hint
        binding.etValue.text?.clear()

        binding.btnSaveLog.setOnClickListener {
            saveHealthLog(metric, unit)
        }
    }

    private fun saveHealthLog(metric: String, unit: String) {
        val value = binding.etValue.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (value.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val log = HealthLog(
                userId = session.getUserId(),
                metricType = metric,
                value = value,
                unit = unit,
                notes = notes
            )
            db.healthLogDao().insert(log)

            runOnUiThread {
                Toast.makeText(this@AddLogActivity,
                    "$metric logged successfully!", Toast.LENGTH_SHORT).show()
                binding.layoutInputForm.visibility = View.GONE
                binding.etValue.text?.clear()
                selectedMetric = ""
            }
        }
    }
}

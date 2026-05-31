package com.carebuddy.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityAddMedicineBinding
import com.carebuddy.models.Medicine
import com.carebuddy.utils.AlarmScheduler
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMedicineBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private var editMedicineId: Int = -1
    private var selectedTimeSlot: String = "Morning"
    private var selectedRepeat: String = "Every Day"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)
        editMedicineId = intent.getIntExtra("medicine_id", -1)

        setupTimeSlotSelection()
        setupRepeatSelection()

        if (editMedicineId != -1) {
            loadMedicineForEdit()
        }

        binding.ivBack.setOnClickListener { finish() }

        binding.btnSaveMedication.setOnClickListener {
            saveMedicine()
        }

        binding.tvCancel.setOnClickListener { finish() }
    }

    private fun setupTimeSlotSelection() {
        val timeCards = listOf(
            binding.cardMorning to "Morning",
            binding.cardAfternoon to "Afternoon",
            binding.cardEvening to "Evening",
            binding.cardNight to "Night"
        )

        timeCards.forEach { (card, slot) ->
            card.setOnClickListener {
                selectedTimeSlot = slot
                updateTimeSlotUI(timeCards, slot)
            }
        }
        updateTimeSlotUI(timeCards, "Morning")
    }

    private fun updateTimeSlotUI(
        timeCards: List<Pair<androidx.cardview.widget.CardView, String>>,
        selected: String
    ) {
        timeCards.forEach { (card, slot) ->
            card.setCardBackgroundColor(
                if (slot == selected)
                    getColor(com.carebuddy.R.color.primary_blue)
                else
                    getColor(com.carebuddy.R.color.card_background)
            )
        }
    }

    private fun setupRepeatSelection() {
        val repeatButtons = listOf(
            binding.btnEveryDay to "Every Day",
            binding.btnWeekly to "Weekly",
            binding.btnAlternateDays to "Alternate Days",
            binding.btnCustom to "Custom"
        )

        repeatButtons.forEach { (btn, repeat) ->
            btn.setOnClickListener {
                selectedRepeat = repeat
                updateRepeatUI(repeatButtons, repeat)
            }
        }
        updateRepeatUI(repeatButtons, "Every Day")
    }

    private fun updateRepeatUI(
        buttons: List<Pair<android.widget.TextView, String>>,
        selected: String
    ) {
        buttons.forEach { (btn, repeat) ->
            if (repeat == selected) {
                btn.setBackgroundResource(com.carebuddy.R.drawable.bg_repeat_selected)
                btn.setTextColor(getColor(com.carebuddy.R.color.primary_blue))
            } else {
                btn.setBackgroundResource(com.carebuddy.R.drawable.bg_repeat_unselected)
                btn.setTextColor(getColor(com.carebuddy.R.color.text_secondary))
            }
        }
    }

    private fun loadMedicineForEdit() {
        lifecycleScope.launch {
            val medicine = db.medicineDao().getMedicineById(editMedicineId)
            medicine?.let {
                runOnUiThread {
                    binding.etMedicineName.setText(it.name)
                    binding.etDosage.setText(it.dosage)
                    binding.etStartDate.setText(it.startDate)
                    binding.etEndDate.setText(it.endDate)
                    binding.switchReminder.isChecked = it.reminderEnabled
                    selectedTimeSlot = it.timeSlot
                    selectedRepeat = it.repeatType
                }
            }
        }
    }

    private fun saveMedicine() {
        val name = binding.etMedicineName.text.toString().trim()
        val dosage = binding.etDosage.text.toString().trim()
        val startDate = binding.etStartDate.text.toString().trim()
        val endDate = binding.etEndDate.text.toString().trim()
        val reminderEnabled = binding.switchReminder.isChecked

        if (name.isEmpty() || dosage.isEmpty()) {
            Toast.makeText(this, "Please enter medicine name and dosage", Toast.LENGTH_SHORT).show()
            return
        }

        val timeValue = when (selectedTimeSlot) {
            "Morning" -> "08:00"
            "Afternoon" -> "14:00"
            "Evening" -> "18:00"
            "Night" -> "22:00"
            else -> "08:00"
        }

        lifecycleScope.launch {
            val medicine = Medicine(
                id = if (editMedicineId != -1) editMedicineId else 0,
                userId = session.getUserId(),
                name = name,
                dosage = dosage,
                timeSlot = selectedTimeSlot,
                timeValue = timeValue,
                repeatType = selectedRepeat,
                startDate = startDate,
                endDate = endDate,
                reminderEnabled = reminderEnabled
            )

            val savedId = db.medicineDao().insert(medicine)

            if (reminderEnabled) {
                AlarmScheduler.scheduleMedicineReminder(
                    this@AddMedicineActivity,
                    medicine.copy(id = savedId.toInt())
                )
            }

            runOnUiThread {
                Toast.makeText(this@AddMedicineActivity,
                    "Medicine saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

package com.carebuddy.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carebuddy.adapters.MoodHistoryAdapter
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityMoodTrackerBinding
import com.carebuddy.models.MoodEntry
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class MoodTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodTrackerBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private lateinit var adapter: MoodHistoryAdapter
    private var selectedMood: String = ""
    private var selectedScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        setupMoodSelection()
        setupRecyclerView()
        observeMoodHistory()
        loadMoodStats()

        binding.ivBack.setOnClickListener { finish() }

        binding.btnSaveMood.setOnClickListener {
            saveMood()
        }
    }

    private fun setupMoodSelection() {
        val moodCards = listOf(
            Triple(binding.cardGreat, "Great", 5),
            Triple(binding.cardGood, "Good", 4),
            Triple(binding.cardOkay, "Okay", 3),
            Triple(binding.cardSad, "Sad", 2)
        )

        moodCards.forEach { (card, mood, score) ->
            card.setOnClickListener {
                selectedMood = mood
                selectedScore = score
                updateMoodUI(moodCards, card)
            }
        }
    }

    private fun updateMoodUI(
        cards: List<Triple<androidx.cardview.widget.CardView, String, Int>>,
        selected: androidx.cardview.widget.CardView
    ) {
        cards.forEach { (card, _, _) ->
            card.strokeWidth = if (card == selected) 4 else 0
        }
    }

    private fun saveMood() {
        if (selectedMood.isEmpty()) {
            Toast.makeText(this, "Please select your mood", Toast.LENGTH_SHORT).show()
            return
        }

        val note = binding.etMoodNote.text.toString().trim()

        lifecycleScope.launch {
            val entry = MoodEntry(
                userId = session.getUserId(),
                mood = selectedMood,
                moodScore = selectedScore,
                note = note
            )
            db.moodDao().insert(entry)

            runOnUiThread {
                Toast.makeText(this@MoodTrackerActivity,
                    "Mood logged: $selectedMood 😊", Toast.LENGTH_SHORT).show()
                binding.etMoodNote.text?.clear()
                selectedMood = ""
                selectedScore = 0
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MoodHistoryAdapter()
        binding.rvMoodHistory.layoutManager = LinearLayoutManager(this)
        binding.rvMoodHistory.adapter = adapter
    }

    private fun observeMoodHistory() {
        db.moodDao().getMoodsForUser(session.getUserId()).observe(this) { moods ->
            adapter.submitList(moods.take(10))
        }
    }

    private fun loadMoodStats() {
        lifecycleScope.launch {
            val avg = db.moodDao().getAverageMoodScore(session.getUserId()) ?: 0f
            runOnUiThread {
                binding.tvAvgMoodScore.text = "Your average mood score: ${String.format("%.1f", avg)}/5 ⭐"
            }
        }
    }
}

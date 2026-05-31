package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carebuddy.adapters.HealthLogAdapter
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityHealthLogsBinding
import com.carebuddy.models.HealthLog
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class HealthLogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHealthLogsBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private lateinit var adapter: HealthLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        setupRecyclerView()
        observeLogs()

        binding.ivBack.setOnClickListener { finish() }
        binding.fabAddLog.setOnClickListener {
            startActivity(Intent(this, AddLogActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = HealthLogAdapter(
            onDeleteClick = { log -> deleteLog(log) }
        )
        binding.rvHealthLogs.layoutManager = LinearLayoutManager(this)
        binding.rvHealthLogs.adapter = adapter
    }

    private fun observeLogs() {
        db.healthLogDao().getLogsForUser(session.getUserId()).observe(this) { logs ->
            adapter.submitList(logs)
            binding.tvEmptyState.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun deleteLog(log: HealthLog) {
        lifecycleScope.launch {
            db.healthLogDao().deleteById(log.id)
        }
    }
}

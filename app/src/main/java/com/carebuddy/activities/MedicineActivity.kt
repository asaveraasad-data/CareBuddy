package com.carebuddy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carebuddy.adapters.MedicineAdapter
import com.carebuddy.database.CareBuddyDatabase
import com.carebuddy.databinding.ActivityMedicineBinding
import com.carebuddy.models.Medicine
import com.carebuddy.utils.AlarmScheduler
import com.carebuddy.utils.SessionManager
import kotlinx.coroutines.launch

class MedicineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicineBinding
    private lateinit var db: CareBuddyDatabase
    private lateinit var session: SessionManager
    private lateinit var adapter: MedicineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CareBuddyDatabase.getDatabase(this)
        session = SessionManager(this)

        setupRecyclerView()
        observeMedicines()

        binding.btnAddMedicine.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MedicineAdapter(
            onDeleteClick = { medicine -> deleteMedicine(medicine) },
            onEditClick = { medicine ->
                val intent = Intent(this, AddMedicineActivity::class.java)
                intent.putExtra("medicine_id", medicine.id)
                startActivity(intent)
            }
        )
        binding.rvMedicines.layoutManager = LinearLayoutManager(this)
        binding.rvMedicines.adapter = adapter
    }

    private fun observeMedicines() {
        val userId = session.getUserId()
        db.medicineDao().getMedicinesForUser(userId).observe(this) { medicines ->
            adapter.submitList(medicines)
            binding.tvEmptyState.visibility = if (medicines.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun deleteMedicine(medicine: Medicine) {
        lifecycleScope.launch {
            db.medicineDao().softDelete(medicine.id)
            AlarmScheduler.cancelMedicineReminder(this@MedicineActivity, medicine.id)
        }
    }

    override fun onResume() {
        super.onResume()
        // RecyclerView updates automatically via LiveData observer
    }
}

package com.carebuddy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carebuddy.databinding.ItemHealthLogBinding
import com.carebuddy.models.HealthLog
import java.text.SimpleDateFormat
import java.util.*

class HealthLogAdapter(
    private val onDeleteClick: (HealthLog) -> Unit
) : ListAdapter<HealthLog, HealthLogAdapter.LogViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemHealthLogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LogViewHolder(private val binding: ItemHealthLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(log: HealthLog) {
            binding.tvMetricType.text = log.metricType
            binding.tvValue.text = "${log.value} ${log.unit}"

            val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            binding.tvTimestamp.text = sdf.format(Date(log.timestamp))

            if (log.notes.isNotEmpty()) {
                binding.tvNotes.text = log.notes
                binding.tvNotes.visibility = android.view.View.VISIBLE
            } else {
                binding.tvNotes.visibility = android.view.View.GONE
            }

            // Color-code metric type
            val iconRes = when (log.metricType) {
                "Blood Pressure" -> com.carebuddy.R.drawable.ic_bp
                "Blood Sugar" -> com.carebuddy.R.drawable.ic_sugar
                "Heart Rate" -> com.carebuddy.R.drawable.ic_heart_rate
                "Temperature" -> com.carebuddy.R.drawable.ic_temperature
                else -> com.carebuddy.R.drawable.ic_health
            }
            binding.ivMetricIcon.setImageResource(iconRes)

            binding.btnDelete.setOnClickListener { onDeleteClick(log) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HealthLog>() {
        override fun areItemsTheSame(oldItem: HealthLog, newItem: HealthLog) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: HealthLog, newItem: HealthLog) = oldItem == newItem
    }
}

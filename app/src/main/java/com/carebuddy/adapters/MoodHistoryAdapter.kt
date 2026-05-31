package com.carebuddy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carebuddy.databinding.ItemMoodHistoryBinding
import com.carebuddy.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodHistoryAdapter : ListAdapter<MoodEntry, MoodHistoryAdapter.MoodViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoodViewHolder(private val binding: ItemMoodHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: MoodEntry) {
            binding.tvMood.text = entry.mood

            val emojiRes = when (entry.mood) {
                "Great" -> com.carebuddy.R.drawable.ic_mood_great
                "Good" -> com.carebuddy.R.drawable.ic_mood_good
                "Okay" -> com.carebuddy.R.drawable.ic_mood_okay
                "Sad" -> com.carebuddy.R.drawable.ic_mood_sad
                else -> com.carebuddy.R.drawable.ic_mood_good
            }
            binding.ivMoodEmoji.setImageResource(emojiRes)

            val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            val displayTime = when {
                isToday(entry.timestamp) -> "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(entry.timestamp))}"
                isYesterday(entry.timestamp) -> "Yesterday, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(entry.timestamp))}"
                else -> sdf.format(Date(entry.timestamp))
            }
            binding.tvTimestamp.text = displayTime

            if (entry.note.isNotEmpty()) {
                binding.tvNote.text = entry.note
                binding.tvNote.visibility = android.view.View.VISIBLE
            } else {
                binding.tvNote.visibility = android.view.View.GONE
            }
        }

        private fun isToday(timestamp: Long): Boolean {
            val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
            val cal2 = Calendar.getInstance()
            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        }

        private fun isYesterday(timestamp: Long): Boolean {
            val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
            val cal2 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
        override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry) = oldItem == newItem
    }
}

package com.carebuddy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carebuddy.databinding.ItemMedicineBinding
import com.carebuddy.models.Medicine

class MedicineAdapter(
    private val onDeleteClick: (Medicine) -> Unit,
    private val onEditClick: (Medicine) -> Unit
) : ListAdapter<Medicine, MedicineAdapter.MedicineViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = ItemMedicineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MedicineViewHolder(private val binding: ItemMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medicine: Medicine) {
            binding.tvMedicineName.text = medicine.name
            binding.tvDosage.text = medicine.dosage
            binding.tvTimeSlot.text = "${medicine.timeSlot} • ${medicine.timeValue}"
            binding.tvRepeat.text = medicine.repeatType
            binding.ivReminder.setImageResource(
                if (medicine.reminderEnabled) com.carebuddy.R.drawable.ic_bell_on
                else com.carebuddy.R.drawable.ic_bell_off
            )

            // Time slot color indicator
            val slotColor = when (medicine.timeSlot) {
                "Morning" -> com.carebuddy.R.color.slot_morning
                "Afternoon" -> com.carebuddy.R.color.slot_afternoon
                "Evening" -> com.carebuddy.R.color.slot_evening
                "Night" -> com.carebuddy.R.color.slot_night
                else -> com.carebuddy.R.color.primary_blue
            }
            binding.viewTimeIndicator.setBackgroundColor(
                binding.root.context.getColor(slotColor)
            )

            binding.btnEdit.setOnClickListener { onEditClick(medicine) }
            binding.btnDelete.setOnClickListener { onDeleteClick(medicine) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Medicine>() {
        override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine) = oldItem == newItem
    }
}

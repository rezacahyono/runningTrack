package com.tugaspti.runningtrack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.data.entity.Run
import com.tugaspti.runningtrack.databinding.ItemRunningBinding
import com.tugaspti.runningtrack.utils.TrackingUtils
import java.text.SimpleDateFormat
import java.util.*


class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    private lateinit var binding: ItemRunningBinding
    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    // ListDiffer to efficiently deal with changes in the RecyclerView
    val differ = AsyncListDiffer(this, diffCallback)

    inner class RunViewHolder(itemView: ItemRunningBinding) : RecyclerView.ViewHolder(itemView.root)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        binding = ItemRunningBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        // set item data
        holder.itemView.apply {
            Glide.with(this).load(run.image).into(binding.imageRun)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(calendar.time)

            "${run.avgSpeed}km/h".also {
                binding.tvAverageSpeed.text = it
            }
            "${run.distance / 1000f}km".also {
                binding.tvTotalDistance.text = it
            }
            binding.tvTotalTime.text = TrackingUtils.getFormattedStopWatchTime(run.timeMillis)

            "${run.caloriesBurned}kcal".also {
                binding.tvTotalCalories.text = it
            }
        }
    }
}
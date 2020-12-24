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
import com.tugaspti.runningtrack.utils.TrackingUtils
import kotlinx.android.synthetic.main.item_running.view.*
import java.text.SimpleDateFormat
import java.util.*


class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

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

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_running, parent, false)
        return RunViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        // set item data
        holder.itemView.apply {
            Glide.with(this).load(run.image).into(imageRun)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            "${run.avgSpeed}km/h".also {
                tvAverageSpeed.text = it
            }
            "${run.distance / 1000f}km".also {
                tvTotalDistance.text = it
            }
            tvTotalTime.text = TrackingUtils.getFormattedStopWatchTime(run.timeMillis)

            "${run.caloriesBurned}kcal".also {
                tvTotalCalories.text = it
            }
        }
    }
}
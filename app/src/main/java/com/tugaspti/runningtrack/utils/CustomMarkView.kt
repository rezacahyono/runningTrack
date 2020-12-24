package com.tugaspti.runningtrack.utils

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.tugaspti.runningtrack.data.entity.Run
import kotlinx.android.synthetic.main.mark_view.view.*
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ViewConstructor")
class CustomMarkView(
    private val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]
        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd-MMMM-yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        "${run.avgSpeed}km/h".also {
            tvAvgSpeed.text = it
        }
        "${run.distance / 1000f}km".also {
            tvDistance.text = it
        }
        tvDuration.text =
            TrackingUtils.getFormattedStopWatchTime(
                run.timeMillis
            )
        "${run.caloriesBurned}kcal".also {
            tvCaloriesBurned.text = it
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}
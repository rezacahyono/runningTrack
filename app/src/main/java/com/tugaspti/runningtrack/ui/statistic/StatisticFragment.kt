package com.tugaspti.runningtrack.ui.statistic

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.tugaspti.runningtrack.R
import com.tugaspti.runningtrack.utils.Constant.Companion.KEY_MODE_THEME
import com.tugaspti.runningtrack.utils.Constant.Companion.POLYLINE_COLOR
import com.tugaspti.runningtrack.utils.CustomMarkView
import com.tugaspti.runningtrack.utils.TrackingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistic.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class StatisticFragment : Fragment() {

    private val viewModel: StatisticViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(activity != null){
            subscribeToObservers()

            val theme = sharedPref.getBoolean(KEY_MODE_THEME, false)
            if (theme){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            setupLineChart(theme)

        }
    }

    private fun setupLineChart(state: Boolean) {
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = POLYLINE_COLOR
            textColor = if (state){
                Color.WHITE
            }else{
                Color.BLACK
            }
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor = POLYLINE_COLOR
            textColor = if (state){
                Color.WHITE
            }else{
                Color.BLACK
            }
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = POLYLINE_COLOR
            textColor = if (state){
                Color.WHITE
            }else{
                Color.BLACK
            }
            setDrawGridLines(false)
        }
        barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObservers() {
        viewModel.totalDistance.observe(viewLifecycleOwner,  {
            // in case DB is empty it will be null
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10) / 10f
                val totalDistanceString = "${totalDistance}km"
                tvTotalDistance.text = totalDistanceString
            }
        })

        viewModel.totalTimeMillis.observe(viewLifecycleOwner,  {
            it?.let {
                val totalTimeInMillis = TrackingUtils.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeInMillis
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner,  {
            it?.let {
                val roundedAvgSpeed = round(it * 10F) / 10F
                val totalAvgSpeed = "${roundedAvgSpeed}km/h"
                tvAverageSpeed.text = totalAvgSpeed
            }
        })


        viewModel.totalCalories.observe(viewLifecycleOwner,  {
            it.let {
                val totalCaloriesBurned = "${it}kcal"
                tvTotalCalories.text = totalCaloriesBurned
            }
        })

        viewModel.runsSortByDate.observe(viewLifecycleOwner, {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeed) }

                val bardataSet = BarDataSet(allAvgSpeeds, "Avg Speed over Time")
                bardataSet.apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                val lineData = BarData(bardataSet)
                barChart.data = lineData
                val marker = CustomMarkView(
                    it.reversed(),
                    requireContext(),
                    R.layout.mark_view
                )
                barChart.marker = marker
                barChart.invalidate()
            }
        })
    }


}
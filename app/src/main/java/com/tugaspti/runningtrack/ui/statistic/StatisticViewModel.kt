package com.tugaspti.runningtrack.ui.statistic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.tugaspti.runningtrack.repository.MainRepository

class StatisticViewModel @ViewModelInject constructor(mainRepository: MainRepository): ViewModel() {

    var totalDistance = mainRepository.totalDistance()
    var totalTimeMillis = mainRepository.totalTimeMillis()
    var totalAvgSpeed = mainRepository.totalAvgSpeed()
    var totalCalories = mainRepository.totalCalories()

    var runsSortByDate = mainRepository.getAllRunSortByDate()
}
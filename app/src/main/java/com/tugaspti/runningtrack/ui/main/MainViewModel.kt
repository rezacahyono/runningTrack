package com.tugaspti.runningtrack.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugaspti.runningtrack.data.entity.ImageRun
import com.tugaspti.runningtrack.data.entity.Run
import com.tugaspti.runningtrack.repository.MainRepository
import com.tugaspti.runningtrack.utils.SortType
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel @ViewModelInject constructor(val mainRepository: MainRepository): ViewModel() {

    private val runSortByDate = mainRepository.getAllRunSortByDate()

    private val runSortByDistance = mainRepository.getAllRunSortByDistance()

    private val runSortByTimeMillis = mainRepository.getAllRunSortByTimeMillis()

    private val runSortByAvgSpeed = mainRepository.getAllRunSortByAvgSpeed()

    private val runSortByCalories = mainRepository.getAllRunSortByCalories()

    fun imageRun(): LiveData<List<ImageRun>> = mainRepository.getimageRun()

    val run = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        run.addSource(runSortByDate) { result ->
            Timber.d("RUNS SORTED BY DATE")
            if(sortType == SortType.DATE) {
                result?.let { run.value = it }
            }
        }
        run.addSource(runSortByDistance) { result ->
            if(sortType == SortType.DISTANCE) {
                result?.let { run.value = it }
            }
        }
        run.addSource(runSortByTimeMillis) { result ->
            if(sortType == SortType.RUNNING_TIME) {
                result?.let { run.value = it }
            }
        }
        run.addSource(runSortByAvgSpeed) { result ->
            if(sortType == SortType.AVG_SPEED) {
                result?.let { run.value = it }
            }
        }
        run.addSource(runSortByCalories) { result ->
            if(sortType == SortType.CALORIES_BURNED) {
                result?.let { run.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType) {
        SortType.DATE -> runSortByDate.value?.let { run.value = it }
        SortType.DISTANCE -> runSortByDistance.value?.let { run.value = it }
        SortType.RUNNING_TIME -> runSortByTimeMillis.value?.let { run.value = it }
        SortType.AVG_SPEED -> runSortByAvgSpeed.value?.let { run.value = it }
        SortType.CALORIES_BURNED -> runSortByCalories.value?.let { run.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

    fun deleteRun(run: Run) = viewModelScope.launch {
        mainRepository.deleteRun(run)
    }


}
package com.tugaspti.runningtrack.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugaspti.runningtrack.data.entity.ImageRun
import com.tugaspti.runningtrack.data.entity.Run
import com.tugaspti.runningtrack.data.room.RunDao
import com.tugaspti.runningtrack.utils.DataDummy
import javax.inject.Inject

class MainRepository @Inject constructor(val runDao: RunDao) {
    suspend fun insertRun(run: Run) = runDao.insert(run)

    suspend fun deleteRun(run: Run) = runDao.delete(run)

    fun getAllRunSortByDate() = runDao.getAllRunSortByDate()

    fun getAllRunSortByDistance() = runDao.getAllRunSortByDistance()

    fun getAllRunSortByTimeMillis() = runDao.getAllRunSortByTimeMillis()

    fun getAllRunSortByAvgSpeed() = runDao.getAllRunSortByAvgSpeed()

    fun getAllRunSortByCalories() = runDao.getAllRunSortByCaloriesBurned()

    fun totalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun totalDistance() = runDao.getTotalDistance()

    fun totalCalories() = runDao.getTotalCaloriesBurned()

    fun totalTimeMillis() = runDao.getTotalTimeMillis()

    fun getimageRun(): LiveData<List<ImageRun>>{
        val data = MutableLiveData<List<ImageRun>>()
        try {
            data.value = DataDummy.loadImage()
        }catch (e: Exception){
            e.printStackTrace()
        }
        return data
    }

}
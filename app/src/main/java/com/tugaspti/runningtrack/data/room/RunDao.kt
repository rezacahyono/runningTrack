package com.tugaspti.runningtrack.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tugaspti.runningtrack.data.entity.Run


@Dao
interface RunDao {

    @Query("SELECT * FROM running ORDER by timeStamp DESC")
    fun getAllRunSortByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM running ORDER by timeMillis DESC")
    fun getAllRunSortByTimeMillis(): LiveData<List<Run>>

    @Query("SELECT * FROM running ORDER by caloriesBurned DESC")
    fun getAllRunSortByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * FROM running ORDER by avgSpeed DESC")
    fun getAllRunSortByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM running ORDER by distance DESC")
    fun getAllRunSortByDistance(): LiveData<List<Run>>

    @Query("SELECT SUM(timeMillis) FROM running")
    fun getTotalTimeMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM running")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeed) FROM running")
    fun getTotalAvgSpeed(): LiveData<Float>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run)

    @Delete
    suspend fun delete(run: Run)
}
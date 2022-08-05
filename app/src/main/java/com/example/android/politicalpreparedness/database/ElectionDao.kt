package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //COMPLETED: Add insert query
    @Insert
    fun insert(election : Election)

    //COMPLETED: Add select all election query
    @Query ("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>>

    //COMPLETED: Add select single election query
    @Query("SELECT * FROM election_table WHERE id=:electionId")
    suspend fun getElectionById(electionId:Int) : Election?

    //COMPLETED: Add delete query
    @Delete
    suspend fun delete(election:Election)

    //COMPLETED: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()
}
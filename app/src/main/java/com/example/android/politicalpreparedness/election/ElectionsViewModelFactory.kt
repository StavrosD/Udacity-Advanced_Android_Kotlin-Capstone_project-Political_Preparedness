package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao

//COMPLETED: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(
    private val dataSource: ElectionDao,
    private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(dataSource , application ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class - ElectionsViewModelFactory ")
    }

}
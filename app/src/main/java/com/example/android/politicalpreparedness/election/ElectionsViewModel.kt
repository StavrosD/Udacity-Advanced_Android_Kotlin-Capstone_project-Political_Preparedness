package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//COMPLETED: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val dataSource:ElectionDao,
    application: Application,
) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)

    //COMPLETED: Create live data val for upcoming elections
    val upcomingElectionsList = MutableLiveData<MutableList<Election>>()

    //COMPLETED: Create live data val for saved elections
    var savedElectionsList : LiveData<List<Election>>? = null

    private val _showUpcomingElectionsLoading = MutableLiveData<Boolean>()
    val showUpcomingElectionsLoading
        get() = _showUpcomingElectionsLoading

    val showLocalElectionsLoading = MutableLiveData<Boolean>()

    private val _navigateToVoterInfoDetails = MutableLiveData<Election?>()
    val navigateToVoterInfoDetails
        get() = _navigateToVoterInfoDetails

    init {
        loadUpcomingElections()
        loadSavedElections()
    }

    fun onElectionClicked(election:Election){
        _navigateToVoterInfoDetails.value = election
    }

    fun onElectionDetailsNavigated(){
        _navigateToVoterInfoDetails.value = null
    }
    //COMPLETED: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun loadUpcomingElections(){
        viewModelScope.launch{
            _showUpcomingElectionsLoading.postValue(true)
            upcomingElectionsList.postValue(CivicsApi.retrofitService.getElections().elections.toMutableList())
            _showUpcomingElectionsLoading.postValue(false)
        }
    }

    fun loadSavedElections(){
        savedElectionsList = dataSource.getElections()
    }

    //COMPLETED: Create functions to navigate to saved or upcoming election voter info
    // I used SafeArgs navigation
}
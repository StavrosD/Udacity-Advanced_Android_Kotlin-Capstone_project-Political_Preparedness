package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(
    private val dataSource: ElectionDao,
    private val electionId: Int,
    private val division: Division) : ViewModel() {

    //COMPLETED: Add live data to hold voter info
    val election = MutableLiveData<Election?>()
    val locations = MutableLiveData<String?>()
    val contests = MutableLiveData<String?>()
    val state = MutableLiveData<List<State>?>()
    val electionElectionOfficials = MutableLiveData<List<ElectionOfficial>?>()
    val errorMessage = MutableLiveData<String?>()
    init{
        populateVoterInfo()
        saveButtonInit()
    }
    //COMPLETED: Add var and methods to populate voter info
    private val voterInfo = MutableLiveData<VoterInfoResponse?>()
    private fun populateVoterInfo(){
            viewModelScope.launch {
                try {
                    val address = division.state + "," + division.country
                    val voterInfoResponse = CivicsApi.retrofitService.getVoterInfo(
                        address,
                        electionId,
                        false
                    )

                    voterInfo.postValue(voterInfoResponse)
                    election.postValue(voterInfoResponse.election)
                } catch (e : Exception){
                    errorMessage.postValue(e.localizedMessage)

                }
        }
    }

    //COMPLETED: Add var and methods to support loading URLs
    private val _votingElectionsUrl = Transformations.map(voterInfo){ voterInfoResponse->
        voterInfoResponse?.let {
            it.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
        }?: kotlin.run {
            ""
        }
    }
    val votingUrl: LiveData<String> get() = _votingElectionsUrl

    private val _ballotInformationUrl = Transformations.map(voterInfo){ voterInfoResponse->
        voterInfoResponse?.let {
            it.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
        }?: kotlin.run {
            ""
        }
    }
    val ballotUrl: LiveData<String> get() = _ballotInformationUrl

    /* private fun populateUrl(){  // I used a transformation to set the url. This method is not needed

        voterInfo?.value?.let {
            _url.postValue(it.state?.get(0)?.electionAdministrationBody?.electionInfoUrl)
        }?:run {
            _url.postValue("")
        }
    } */

    //COMPLETED: Add var and methods to save and remove elections to local database
    private fun removeElectionFromLocalDB(){
        election.value?.let {
            viewModelScope.launch {
                dataSource.delete(it)
                _electionSavedStatus.postValue(false)
            }
        }
    }

    private fun saveElectionToLocalDB(){
        election.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                dataSource.insert(it)

                _electionSavedStatus.postValue(true)
            }
        }
    }

    fun saveButtonClick() {
        _electionSavedStatus.value?.let {savedToLocalDB ->
            if (!savedToLocalDB) {
                saveElectionToLocalDB()
            } else {
                removeElectionFromLocalDB()
            }
            _electionSavedStatus.postValue(!savedToLocalDB)
        }
    }

    //COMPLETED: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    private val _electionSavedStatus = MutableLiveData<Boolean>()
    val electionSavedStatus:LiveData<Boolean> get() = _electionSavedStatus

    private fun saveButtonInit(){
        viewModelScope.launch {
            val election = dataSource.getElectionById(electionId)
            election?.let {
                _electionSavedStatus.postValue(true)
            }?: run{
                _electionSavedStatus.postValue(false)
            }
        }
    }
    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}
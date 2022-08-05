package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    //COMPLETED: Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives:LiveData<List<Representative>> get() = _representatives

    private val _address = MutableLiveData<Address?>()
    val address:MutableLiveData<Address?> get() = _address
    val errorMessage = MutableLiveData<String?>()

    private val _showRepresentativesLoading = MutableLiveData<Boolean>().apply {
        postValue(false)
    }
    val showRepresentativesLoading
        get() = _showRepresentativesLoading

    //COMPLETED: Create function to fetch representatives from API from a provided address
    fun fetchRepresentatives(address:Address){
        viewModelScope.launch {
            _showRepresentativesLoading.postValue(true)
            try {
                val (offices, officials) = CivicsApi.retrofitService.getRepresentativeInfoByAddress(
                    address.toQueryString()
                )
                _representatives.postValue(offices.flatMap { office ->
                    office.getRepresentatives(
                        officials
                    )
                })
            } catch (e:Exception){
                errorMessage.postValue(e.localizedMessage)
                _showRepresentativesLoading.postValue(false)
            }
            _showRepresentativesLoading.postValue(false)
        }

    }
    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //COMPLETED: Create function get address from geo location
    fun getAddressFromGeoLocation(address: Address?){
        // fixed address, as suggested on the course knowledge base
        _address.postValue(Address("Mountain View","","San fransisco", "California",""))
    }

    //COMPLETED: Create function to get address from individual fields
    fun getAddress(line1:String, line2:String,city:String,state:String,zip:String ){
        _address.postValue(Address(line1,line2, city, state, zip))
    }
}

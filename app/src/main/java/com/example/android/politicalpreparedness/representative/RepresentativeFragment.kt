package com.example.android.politicalpreparedness.representative

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.location.LocationServices
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        //COMPLETED: Add Constant for Location request
        const val LOCATION_REQUEST = 1
    }

    //COMPLETED: Declare ViewModel
    private val viewModel by lazy {
        ViewModelProvider(this)[(RepresentativeViewModel::class.java)]
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //COMPLETED: Establish bindings
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //COMPLETED: Define and assign Representative adapter
        val adapter =  RepresentativeListAdapter(RepresentativeListener {

        })

        //COMPLETED: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner){
            it?.let{
                adapter.submitList(it)
            }
        }
        binding.recyclerView.adapter = adapter

        //COMPLETED: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener{
            viewModel.showRepresentativesLoading.postValue(true)
            val address = Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )
            viewModel.address.postValue(address)
        }

        binding.buttonLocation.setOnClickListener{
            viewModel.showRepresentativesLoading.postValue(true)
            getLocation()
        }

        viewModel.address.observe(viewLifecycleOwner){
            it?.let {
                viewModel.fetchRepresentatives(it)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(),"ERROR! $errorMessage", Toast.LENGTH_LONG).show()
            }
        }

        ArrayAdapter.createFromResource(this.context!!, R.array.states, android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.state.adapter = it
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //COMPLETED: Handle location permission result to get location on permission granted
        if (grantResults.isEmpty()) return
        if ((requestCode == LOCATION_REQUEST) && grantResults.contains(PackageManager.PERMISSION_GRANTED)){
            getLocation()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //COMPLETED: Request Location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_REQUEST
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //COMPLETED: Check if permission is already granted and return (true = granted, false = denied/other)
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun getLocation() {
        //COMPLETED: Get location from LocationServices
        //COMPLETED: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        if (checkLocationPermissions()){
            LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener {
                viewModel.getAddressFromGeoLocation(geoCodeLocation(it))

            }
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address =  geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map {
                    it?.let {
                        Address(
                            it.thoroughfare,
                            it.subThoroughfare,
                            it.locality,
                            it.adminArea,
                            it.postalCode
                        )
                    }
                }
                .first()
        return address

    }



}
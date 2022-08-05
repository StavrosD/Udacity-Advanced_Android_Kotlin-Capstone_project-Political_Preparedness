package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    //COMPLETED: Declare ViewModel
    private val viewModel by lazy {
        //COMPLETED: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource,application)

        return@lazy ViewModelProvider(this,viewModelFactory)[(ElectionsViewModel::class.java)]
    }

    private lateinit var savedElectionsAdapter : ElectionListAdapter
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //COMPLETED: Add binding values
        val binding = FragmentElectionBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //COMPLETED: Link elections to voter info
        viewModel.navigateToVoterInfoDetails.observe(viewLifecycleOwner) {
            it?.let {

                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        it
                    )
                )
                viewModel.navigateToVoterInfoDetails.postValue(null)
            }
        }

        //COMPLETED: Initiate recycler adapters
        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it)
            )
        })
        binding.rvUpcomingElections.adapter = upcomingElectionsAdapter

        viewModel.upcomingElectionsList.observe(viewLifecycleOwner) {
            it?.let {
                upcomingElectionsAdapter.submitList(it)
            }
        }

        savedElectionsAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it)
            )
        })

        viewModel.savedElectionsList?.observe(viewLifecycleOwner){
            it?.let {
                savedElectionsAdapter.submitList(it)
            }
        }

        binding.rvSavedElections.adapter = savedElectionsAdapter

        //COMPLETED: Populate recycler adapters
        // implemented in the layout
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Update list in case an election is followed or unfollowed
        savedElectionsAdapter.notifyDataSetChanged()


    }

    // COMPLETED: Refresh adapters when fragment loads
    // Not needed. Bound in the layout file
    // override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    super.onViewCreated(view, savedInstanceState)
    //    binding.rvUpcomingElections.adapter?.notifyDataSetChanged()
    //    binding.rvSavedElections.adapter?.notifyDataSetChanged()
    // }
}
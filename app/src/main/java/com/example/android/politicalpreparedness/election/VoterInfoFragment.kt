package com.example.android.politicalpreparedness.election
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.MainActivity
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding


class VoterInfoFragment : Fragment() {
    private val viewModel : VoterInfoViewModel by lazy {
        //COMPLETED: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
        try {
            requireArguments()
        } catch (exception:Exception){
            Toast.makeText(
                requireActivity().applicationContext,
                R.string.invalid_fragment_arguments,
                Toast.LENGTH_LONG
            )
            findNavController().navigateUp()
 //           findNavController().popBackStack()
        }
        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())

        val electionId = args.argElection.id
        val division = args.argElection.division

        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionId, division)
        return@lazy ViewModelProvider(this, viewModelFactory)[(VoterInfoViewModel::class.java)]
    }





    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //COMPLETED: Add ViewModel values and create ViewModel
        // created above

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        //COMPLETED: Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.errorMessage.observe(viewLifecycleOwner){errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(),"ERROR! $errorMessage",Toast.LENGTH_LONG).show()
                //requireActivity().supportFragmentManager.popBackStack()
                requireActivity().onBackPressed()
            }
        }

        //COMPLETED: Handle loading of URLs
        viewModel.votingUrl.observe(viewLifecycleOwner){ url->
            if (url != "" ) {
                binding.stateLocations.setOnClickListener{
                    loadUrlIntent(url)
                }
            } else {
             binding.stateLocations.setOnClickListener {  }
            }
        }


        viewModel.ballotUrl.observe(viewLifecycleOwner){ url->
            if (url != "" ) {
                binding.stateBallot.setOnClickListener{
                    loadUrlIntent(url)
                }
            } else {
                binding.stateBallot.setOnClickListener {  }
            }
        }

        //COMPLETED: Handle save button UI state
        //COMPLETED: cont'd Handle save button clicks
        // Implemented inside fragment_voter_info.xml

        val mainActivity = (activity as MainActivity?)!!

        viewModel.election.observe(viewLifecycleOwner){
            it?.let {
                mainActivity.supportActionBar!!.setWindowTitle(it.name)
            }?: run{
                mainActivity.supportActionBar!!.setWindowTitle(getString(R.string.app_name))
            }
        }

        return binding.root
    }

    //COMPLETED: Create method to load URL intents
     private fun loadUrlIntent(url:String){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    }
}
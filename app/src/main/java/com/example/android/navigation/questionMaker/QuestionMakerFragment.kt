package com.example.android.navigation.questionMaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.android.navigation.R
import com.example.android.navigation.database.QuestionsDatabase
import com.example.android.navigation.databinding.FragmentQuestionMakerBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalArgumentException

class QuestionMakerFragment : Fragment() {

    private lateinit var viewModel : QuestionMakerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentQuestionMakerBinding>(inflater, R.layout.fragment_question_maker, container, false)

        val dataSource = QuestionsDatabase.getInstance(requireContext()).questionsDatabaseDao
        val viewModelFactory = QuestionMakerViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestionMakerViewModel::class.java)


        //lleva de vuelta al fragmento QuestionAdderFragment
        binding.cancelButton.setOnClickListener { it.findNavController().navigate(QuestionMakerFragmentDirections.actionQuestionMakerFragmentToQuestionAdderFragment(false))}

        viewModel.addQuestionErrorDuplicateEvent.observe(viewLifecycleOwner, Observer { onError ->
            if (onError) showSnackbar(getString(R.string.duplicated_question_error), 0)
        })

        viewModel.addQuestionErrorUnfilledEvent.observe(viewLifecycleOwner, Observer { onError ->
            if (onError) showSnackbar(getString(R.string.unfilled_question_error), 1)
        })

        viewModel.questionWasAddedEvent.observe(viewLifecycleOwner, Observer { wasAdded ->
            if (wasAdded) view?.findNavController()?.navigate(QuestionMakerFragmentDirections.actionQuestionMakerFragmentToQuestionAdderFragment(true))
        })
        // Inflate the layout for this fragment
        return binding.root
    }


    /**
     * Muestra un snackbar con el mensaje introducido por la duración de tiempo establecida:
     *
     * Para **duration = 0** será de duración ***corta*** y para **duration = 1** será de duración ***larga***.
     */
    private fun showSnackbar (message: String, @IntRange(from = 0, to = 1) duration: Int) {
        val showLength = when (duration) {
            0 -> Snackbar.LENGTH_SHORT
            1 -> Snackbar.LENGTH_LONG
            else -> {throw IllegalArgumentException("Invalid duration value")}
        }

        Snackbar.make(
            requireView(),
            message,
            showLength
        ).show()
    }
}
package com.example.android.navigation.questionAdder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.android.navigation.R
import com.example.android.navigation.database.QuestionsDatabase
import com.example.android.navigation.databinding.FragmentQuestionAdderBinding


class QuestionAdderFragment : Fragment() {


    private lateinit var viewModel: QuestionAdderViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentQuestionAdderBinding>(inflater, R.layout.fragment_question_adder, container, false)

        val dataSource = QuestionsDatabase.getInstance(requireContext()).questionsDatabaseDao
        val viewModelFactory = QuestionAdderViewModelFactory(dataSource, requireActivity().application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(QuestionAdderViewModel::class.java)

        binding.questionAdderViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        //establece un listener en el botón para volver al TitleFragment
        binding.backButton.setOnClickListener{ it.findNavController().navigate(R.id.action_questionAdderFragment_to_titleFragment)}
        //lleva al QuestionMakerFragment
        binding.addButton.setOnClickListener{it.findNavController().navigate(R.id.action_questionAdderFragment_to_questionMakerFragment)}
        // Inflate the layout for this fragment
        return binding.root
    }

}
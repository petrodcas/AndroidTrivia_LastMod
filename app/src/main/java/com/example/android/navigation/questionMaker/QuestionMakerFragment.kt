package com.example.android.navigation.questionMaker

import android.os.Bundle
import android.util.Log
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

        Log.d(":::HELPME", "SE HA ENTRADO EN EL ONCREATEVIEW() DEL QUESTIONMAKERFRAGMENT")
        val binding = DataBindingUtil.inflate<FragmentQuestionMakerBinding>(inflater, R.layout.fragment_question_maker, container, false)
        Log.d(":::HELPME", "SE HA BINDEADO EL QUESTIONMAKERFRAGMENT")
        val dataSource = QuestionsDatabase.getInstance(requireContext()).questionsDatabaseDao
        Log.d(":::HELPME", "SE HA CONSEGUIDO INSTANCIA DE LA BBDD EN EL QUESTIONMAKERFRAGMENT")
        val viewModelFactory = QuestionMakerViewModelFactory(dataSource)
        Log.d(":::HELPME", "SE HA CREADO EL VIEWMODELFACTORY DEL QUESTIONMAKERFRAGMENT")
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestionMakerViewModel::class.java)
        Log.d(":::HELPME", "SE HA CREADO EL VIEWMODEL DEL QUESTIONMAKERFRAGMENT")


        //lleva de vuelta al fragmento QuestionAdderFragment
        binding.cancelButton.setOnClickListener { it.findNavController().navigate(R.id.action_questionMakerFragment_to_questionAdderFragment)}
        binding.okButton.setOnClickListener {
            Log.d(":::HELPME", "SE HA PRESIONADO EL OKBUTTON DEL QUESTIONMAKERFRAGMENT")
            viewModel.onMakeQuestion(requireView()) }

        Log.d(":::HELPME", "SE HAN VINCULADO CORRECTAMENTE LOS BOTONES DEL QUESTIONMAKERFRAGMENT")

        viewModel.addQuestionErrorDuplicateEvent.observe(viewLifecycleOwner, Observer { onError ->
            Log.d(":::HELPME", "SE HA ENTRADO OBSERVER DEL DUPLICATEERROR DEL QUESTIONMAKERFRAGMENT")
            if (onError) {
                Log.d(":::HELPME", "SE HA MOSTRADO EL SNACKBAR DE DUPLICATEERROR DEL QUESTIONMAKERFRAGMENT")
                showSnackbar(getString(R.string.duplicated_question_error), 0)
                viewModel.onDuplicateQuestionEventFinish()
            }
            Log.d(":::HELPME", "SE HA SALIDO DEL OBSERVER DEL DUPLICATEERROR DEL QUESTIONMAKERFRAGMENT")
        })

        viewModel.addQuestionErrorUnfilledEvent.observe(viewLifecycleOwner, Observer { onError ->
            Log.d(":::HELPME", "SE HA ENTRADO EN EL OBSERVER DEL UNFILLED_ERROR DEL QUESTIONMAKERFRAGMENT")
            if (onError) {
                Log.d(":::HELPME", "SE HA MOSTRADO EL SNACKBAR DEL OBSERVER DEL UNFILLED_ERROR DEL QUESTIONMAKERFRAGMENT")
                showSnackbar(getString(R.string.unfilled_question_error), 1)
                viewModel.onUnfilledFieldEventFinish()
            }
            Log.d(":::HELPME", "SE HA SALIDO DEL OBSERVER DEL UNFILLED_ERROR DEL QUESTIONMAKERFRAGMENT")
        })

        viewModel.questionWasAddedEvent.observe(viewLifecycleOwner, Observer { wasAdded ->
            Log.d(":::HELPME", "SE HA ENTRADO EN EL OBSERVER DEL ADDED_EVENT DEL QUESTIONMAKERFRAGMENT")
            if (wasAdded) {
                viewModel.onQuestionAddedEventFinish()
                requireView().findNavController().navigate(R.id.action_questionMakerFragment_to_questionAdderFragment)
            }
            Log.d(":::HELPME", "SE HA SALIDO DEL OBSERVER DEL ADDED_EVENT DEL QUESTIONMAKERFRAGMENT")
        })
        // Inflate the layout for this fragment
        Log.d(":::HELPME", "SE HA SALIDO DEL ONCREATEVIEW() DEL QUESTIONMAKERFRAGMENT")
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
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

    /** Referencia al viewmodel */
    private lateinit var viewModel : QuestionMakerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //se vincula al layout
        val binding = DataBindingUtil.inflate<FragmentQuestionMakerBinding>(inflater, R.layout.fragment_question_maker, container, false)
        //BBDD
        val dataSource = QuestionsDatabase.getInstance(requireContext()).questionsDatabaseDao
        //Factory del viewmodel
        val viewModelFactory = QuestionMakerViewModelFactory(dataSource)
        //se crea el viewmodel
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestionMakerViewModel::class.java)

        //Se establece un listener que lleva de vuelta al fragmento QuestionAdderFragment
        binding.cancelButton.setOnClickListener { it.findNavController().navigate(R.id.action_questionMakerFragment_to_questionAdderFragment)}
        //Se establece un listener en el botón de aceptar para gestionar la creación de la pregunta
        binding.okButton.setOnClickListener { viewModel.onMakeQuestion(requireView()) }

        //Se crea un observador para mostrar un snackbar en caso de Error de pregunta duplicada: el campo del texto de la pregunta
        //tiene una restricción de unicidad, así que no se puede repetir en la base de datos.
        viewModel.addQuestionErrorDuplicateEvent.observe(viewLifecycleOwner, Observer { onError ->
            if (onError) {
                showSnackbar(getString(R.string.duplicated_question_error), 0)
                viewModel.onDuplicateQuestionEventFinish()
            }
        })

        //Se crea un observador para mostrar un snackbar en caso de Error por no haberse rellenado todos los campos del formulario de
        //creación de la pregunta
        viewModel.addQuestionErrorUnfilledEvent.observe(viewLifecycleOwner, Observer { onError ->
            if (onError) {
                showSnackbar(getString(R.string.unfilled_question_error), 1)
                viewModel.onUnfilledFieldEventFinish()
            }
        })

        //Se crea un observador para pasar al fragmento QuestionAdder en caso de haberse añadido correctamente la pregunta
        viewModel.questionWasAddedEvent.observe(viewLifecycleOwner, Observer { wasAdded ->
            if (wasAdded) {
                viewModel.onQuestionAddedEventFinish()
                requireView().findNavController().navigate(R.id.action_questionMakerFragment_to_questionAdderFragment)
            }
        })

        return binding.root
    }


    /**
     * Muestra un snackbar con el mensaje introducido por la duración de tiempo establecida:
     *
     * Para **duration = 0** será de duración ***corta*** y para **duration = 1** será de duración ***larga***.
     *
     * @param message mensaje a introducir
     * @param duration 0 para snackbar de duración corta o 1 para duración larga.
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
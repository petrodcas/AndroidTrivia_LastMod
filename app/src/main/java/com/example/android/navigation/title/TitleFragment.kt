package com.example.android.navigation.title

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.R
import com.example.android.navigation.database.Question
import com.example.android.navigation.database.QuestionsDatabase
import com.example.android.navigation.databinding.FragmentTitleBinding
import com.example.android.navigation.utils.Level
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TitleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TitleFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var viewModel: TitleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(inflater, R.layout.fragment_title,
        container, false)

        val dataSource = QuestionsDatabase.getInstance(requireContext()).questionsDatabaseDao
        val viewModelFactory = ViewModelProvider(this, TitleViewModelFactory(dataSource))
        viewModel = viewModelFactory.get(TitleViewModel::class.java)


        //TODO: BORRAR ESTA PARTE DE PRUEBAS

        val q1 = Question(text="Pregunta1", answers=listOf("res1", "res2", "res3", "res4"), hint="es la res1")
        val q2 = Question(text="Pregunta2", answers=listOf("res1", "res2", "res3", "res4"), hint="es la res1")
        val q3 = Question(text="Pregunta3", answers=listOf("res1", "res2", "res3", "res4"), hint="es la res1")


        Log.d(":::DATABASE", "Tratando de borrar las preguntas...")
        viewModel.clearQuestions()
        Log.d(":::DATABASE", "Borradas correctamente...")
        Log.d(":::DATABASE", "Tratando de agregar las preguntas...")
        var added = viewModel.addQuestions(listOf(q2, q3))

        if (!added) {
            Log.d(":::DATABASE_ERROR", "No se pudieron agregar las preguntas.")
        }
        else {
            Log.d(":::DATABASE_OK", "Las preguntas se agregaron correctamente.")
        }


        Log.d(":::DATABASE", "Finalizada la acción...")

        //TODO: HASTA AQUÍ ES LA PARTE DE PRUEBAS

        //se añade un listener al botón de play
        binding.playButton.setOnClickListener { view: View ->
                //si hay un nivel seleccionado, entonces se puede jugar
                if (viewModel.selectedLevel != Level.NO_SELECTED)
                    view.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment(viewModel.selectedLevel))
                else {
                    //si no hay nivel seleccionado, se muestra un snackbar de duración corta que además dispone de un botón que inicia el diálogo
                    //de selección de dificultad
                    val cantPlaySnackbar = Snackbar.make(
                        view,
                        getString(R.string.noSelectedLevel),
                        Snackbar.LENGTH_SHORT
                    )
                    cantPlaySnackbar.setAction(R.string.difficulty) {
                        showLevelSelectionDialog(view)
                    }
                    cantPlaySnackbar.show()
                }
        }

        //se asignan listeners a los botones
        binding.titleToAboutButton.setOnClickListener { it.findNavController().navigate(R.id.action_titleFragment_to_aboutFragment) }
        binding.titleToRulesButton.setOnClickListener { it.findNavController().navigate(R.id.action_titleFragment_to_rulesFragment) }
        binding.difficultyButton.setOnClickListener { this.showLevelSelectionDialog(it) }
        //se determina que este fragmento dispone de menú de opciones
        this.setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TitleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TitleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    /**
     * Muestra un menú de diálogo básico (*MaterialAlertDialog*) con botones de confirmación y cancelar, cuyas opciones se corresponden con los
     * posibles niveles a seleccionar.
     */
    private fun showLevelSelectionDialog (view: View) {
        val items : Array<String> = Array<String>(Level.values().size - 1) { i -> getString(Level.values()[i + 1].stringId) }
        var checkedItem : Level = viewModel.selectedLevel

        MaterialAlertDialogBuilder(view.context)
            .setTitle(getString(R.string.select_difficulty))
            .setPositiveButton(getString(R.string.confirmChoice)) { _, _ ->
                if (checkedItem != Level.NO_SELECTED) {
                    viewModel.changeDifficulty(checkedItem)
                }
            }
            .setNeutralButton(getString(R.string.cancelChoice)) { _, _ -> /*no hay necesidad de hacer nada */ }
            .setSingleChoiceItems(items, checkedItem.ordinal - 1) { _, which ->
                checkedItem = Level.values()[which + 1]
            }
            .show()
    }

}
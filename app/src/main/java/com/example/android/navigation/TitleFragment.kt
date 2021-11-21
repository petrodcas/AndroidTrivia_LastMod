package com.example.android.navigation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.FragmentTitleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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


    private var selectedLevel : Level = Level.NO_SELECTED

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

        //selectedLevel = Level.NO_SELECTED

        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(inflater, R.layout.fragment_title,
        container, false)

        //se añade un listener al botón de play
        binding.playButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_gameFragment)
        }

        binding.titleToAboutButton.setOnClickListener { it.findNavController().navigate(R.id.action_titleFragment_to_aboutFragment) }
        binding.titleToRulesButton.setOnClickListener { it.findNavController().navigate(R.id.action_titleFragment_to_rulesFragment) }
        binding.difficultyButton.setOnClickListener { this.showLevelSelectionDialog(it) }
        //TODO: MODIFICAR EL BOTÓN DE PLAY Y AÑADIR LÓGICA AL BOTÓN DE DIFICULTAD




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


    private fun showLevelSelectionDialog (view: View) {
        val items : Array<String> = Array<String>(3) { i -> getString(Level.values()[i + 1].stringId) }
        var checkedItem : Level = selectedLevel

        MaterialAlertDialogBuilder(view.context)
            .setTitle(getString(R.string.select_difficulty))
            .setPositiveButton(getString(R.string.confirmChoice)) { _, _ ->
                if (checkedItem != Level.NO_SELECTED) {
                    selectedLevel = checkedItem
                }
            }
            .setNeutralButton(getString(R.string.cancelChoice)) { _, _ -> /*no hay necesidad de hacer nada */ }
            .setSingleChoiceItems(items, checkedItem.ordinal) { _, which ->
                checkedItem = Level.values()[which]
            }
            .show()
    }

}
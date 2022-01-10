/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation.gameWon

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.android.navigation.R
import com.example.android.navigation.databinding.FragmentGameWonBinding


class GameWonFragment : Fragment() {

    /** Referencia al viewmodel */
    private lateinit var viewModel: GameWonViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameWonBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_won, container, false)

        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        //factory del viewmodel
        val viewModelFactory = GameWonViewModelFactory(args.numAciertos, args.selectedLevel, args.score)
        //se crea el viewmodel a través del factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameWonViewModel::class.java)
        //se vincula el viewmodel a una variable del layout
        binding.gameWonViewModel = viewModel


        //establece un listener para jugar de nuevo (lleva a GameFragment)
        binding.nextMatchButton.setOnClickListener{ it.findNavController().navigate(
            GameWonFragmentDirections.actionGameWonFragmentToGameFragment(
                viewModel.selectedLevel
            )
        ) }

        //establece un listener al botón de salida para cerrar la aplicación
        binding.gameWonExitButton.setOnClickListener { activity?.finishAffinity() }
        //establece que este fragmento tiene actionbar
        setHasOptionsMenu(true)

        return binding.root
    }


    /**
     * Crea un intent implícito para compartir datos.
     */
    private fun getShareIntent() : Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain").putExtra(Intent.EXTRA_TEXT,
            getString(R.string.share_success_text, viewModel.numAciertos, viewModel.selectedLevel.numOfQuestions, viewModel.score))
        return shareIntent
    }

    /**
     * Solicita a la actividad host de este fragmento que comparta el intent creado por getShareIntent()
     */
    private fun shareSuccess() : Unit {
        startActivity(getShareIntent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //infla el layout del menú
        inflater.inflate(R.menu.winner_menu, menu)
        //establece que el botón de compartir no sea mostrado si la actividad no se ha resuelto correctamente
        if (getShareIntent().resolveActivity(requireActivity().packageManager) == null) {
            menu.findItem(R.id.share).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //gestiona qué elemento del menú ha sido seleccionado y aplica los acciones pertinentes
        when (item.itemId) {
            R.id.share -> shareSuccess()
        }
        return super.onOptionsItemSelected(item)
    }
}

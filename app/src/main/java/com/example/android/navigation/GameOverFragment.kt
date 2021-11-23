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

package com.example.android.navigation

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameOverBinding

class GameOverFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameOverBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_over, container, false)

        val args = GameOverFragmentArgs.fromBundle(requireArguments())
        //establece un listener al botón de volver a jugar
        binding.tryAgainButton.setOnClickListener{ it.findNavController().navigate(GameOverFragmentDirections.actionGameOverFragmentToGameFragment(args.selectedLevel)) }
        //establece el mensaje de derrota del textview
        binding.gameOverMsg.text = getString(R.string.loseMsg, args.numAciertos + 1, args.numPreguntas, args.score)
        //establece un listener al botón de salir para cerrar la aplicación
        binding.gameOverExitButton.setOnClickListener { activity?.finishAffinity() }

        //establece que este fragmento tiene actionbar
        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * Crea un intent implícito para compartir datos
     */
    private fun getShareIntent() : Intent {
        val args = GameOverFragmentArgs.fromBundle(requireArguments())
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain").putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.share_success_text, args.numAciertos, args.numPreguntas, args.score))
        return shareIntent
    }

    /**
     * Solicita a la actividad a la que está anclada este fragmento que comparta el intent creado por getShare()
     */
    private fun shareSuccess() : Unit {
        startActivity(getShareIntent())
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //infla el layout del menú
        inflater.inflate(R.menu.loser_menu, menu)
        //establece que el botón de compartir no sea mostrado si no se ha resuelto correctamente la actividad
        if (getShareIntent().resolveActivity(requireActivity().packageManager) == null) {
            menu.findItem(R.id.loserShareButton).isVisible = false
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //gestiona qué elemento del menú ha sido seleccionado y aplica las acciones necesarias
        when (item.itemId) {
            R.id.loserShareButton -> shareSuccess()
        }
        return super.onOptionsItemSelected(item)
    }
}

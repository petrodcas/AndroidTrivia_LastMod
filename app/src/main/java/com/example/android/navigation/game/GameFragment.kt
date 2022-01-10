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

package com.example.android.navigation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.android.navigation.R
import com.example.android.navigation.database.QuestionsDatabase
import com.example.android.navigation.databinding.FragmentGameBinding
import com.google.android.material.snackbar.Snackbar


class GameFragment : Fragment() {

    /** Referencia al viewModel */
    private lateinit var viewModel: GameViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        //argumentos transmitidos por NavDirections
        val args = GameFragmentArgs.fromBundle(requireArguments())

        //bbdd
        val dataSource = QuestionsDatabase.getInstance(requireContext()).questionsDatabaseDao
        //referencia a la aplicación
        val application = requireNotNull(this.activity).application
        //el factory del viewmodel
        val viewModelFactory = ViewModelProvider(this, GameViewModelFactory(args.currentLevel, dataSource, application))
        //se crea el viewmodel a través del factory si no existía ya
        viewModel = viewModelFactory.get(GameViewModel::class.java)


        //se establece el valor del textView que muestra el nivel de la partida en curso
        binding.tvGameLevelInfo.text = getString(R.string.currentLevel, getString(args.currentLevel.stringId))


        // se liga el viewmodel a la variable del layout, así como el lyfecycleowner
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //se vincula un listener al botón de submit
        binding.submitButton.setOnClickListener {
            viewModel.onSubmit(binding.questionRadioGroup.checkedRadioButtonId)
        }

        //se crea un observador para el evento de victoria
        viewModel.wonGameEvent.observe(viewLifecycleOwner, Observer { won ->
            if (won) {
                viewModel.onGameWonEventFinished()

                requireNotNull(view).findNavController()
                    .navigate(GameFragmentDirections
                        .actionGameFragmentToGameWonFragment(requireNotNull(viewModel.questionIndex.value),
                            viewModel.selectedLevel,
                            viewModel.score))
            }
        })

        //se crea un observador para el evento de derrota
        viewModel.gameOverEvent.observe(viewLifecycleOwner, Observer { lost ->
            if (lost) {
                viewModel.onGameOverEventFinished()

                requireNotNull(view).findNavController()
                    .navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment(
                        requireNotNull(viewModel.questionIndex.value),
                        viewModel.selectedLevel,
                        viewModel.score
                    ))
            }
        })

        //se crea un observador para el evento de "usar pista"
        viewModel.usedHintEvent.observe(viewLifecycleOwner, Observer{ used ->
            if (used) {
                showHint(requireNotNull(view))
                viewModel.onHintEventFinished()
            }
        })

        //se observa el índice de pregunta: si cambia, hay que actualizar el título.
        //en realidad esto se podría haber hecho también observando la pregunta actual
        viewModel.questionIndex.observe(viewLifecycleOwner, Observer { index ->
            setTitle(index, viewModel.selectedLevel.numOfQuestions, viewModel.score)
        })


        return binding.root
    }


    /**
     * Establece el título de la actionBar
     */
    private fun setTitle(questionIndex: Int, numQuestions: Int, score: Int)  {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question,
                                                                questionIndex + 1, numQuestions, score)
    }

    /**
     * Muestra la pista de la pregunta actual haciendo uso de un snackbar y establece el valor de usedHint a true.
     */
    private fun showHint (view: View) {
        //se crea y muestra un snackbar con la pista
        Snackbar.make(view, viewModel.currentQuestion.value?.hint.toString(), Snackbar.LENGTH_LONG).show()
    }


}

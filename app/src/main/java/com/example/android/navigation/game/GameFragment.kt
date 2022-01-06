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
import androidx.navigation.findNavController
import com.example.android.navigation.GameFragmentArgs
import com.example.android.navigation.GameFragmentDirections
import com.example.android.navigation.databinding.FragmentGameBinding
import com.example.android.navigation.utils.Level
import com.google.android.material.snackbar.Snackbar
import com.example.android.navigation.R


private const val LAST_DEFINED_QUESTION : Int = 9
private const val FIRST_DEFINED_QUESTION: Int = 0

class GameFragment : Fragment() {
    data class Question(
            val text: String,
            val answers: List<String>,
            val hint: String)

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (Or better yet, don't define the questions in code...)
    private val questions: MutableList<Question> = mutableListOf()
    //es el orden de las preguntas antes de randomizar su orden. Esto se utiliza para manejar
    //la persistencia de datos más adelante de una manera un tanto burda.
    private lateinit var preShuffleQuestionOrder: Array<Int>

    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private var numQuestions: Int = Level.NO_SELECTED.numOfQuestions
    //puntuación
    private var score: Int = 0
    //puntos base por pregunta acertada
    private val baseQuestionPoints: Int = 10
    //penalización (divisoria) para la puntuación conseguida por pregunta en caso de usar la pista
    private val hintPenalty: Int = 2
    //flag para saber si se ha usado la pista
    private var usedHint: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)


        val args = GameFragmentArgs.fromBundle(requireArguments())

        //se establece el valor del textView que muestra el nivel de la partida en curso
        binding.tvGameLevelInfo.text = getString(R.string.currentLevel, getString(args.currentLevel.stringId))
        //se establece el número de preguntas en base al nivel de la partida
        setQuestionNumber(args.currentLevel)

        //crea las preguntas
        populateQuestions()

        //Este condicional se utiliza para arreglar los problemas de persistencia de estado de los fragmentos
        //al rotar el dispositivo
        if (savedInstanceState == null) {
            // Shuffles the questions and sets the question index to the first question.
            preShuffleQuestionOrder = shuffleQuestions(true)
        }
        else {
            //establece las preguntas nuevamente, respetando el orden en que estaban previamente
            //esto es necesario para los casos en que se cambia de idioma en medio de la partida
            shuffleQuestions(preShuffleQuestionOrder)
            setQuestion(false)
            //TODO: arreglar que al entrar a la primera pregunta del fragmento, al girar la pantalla aún se cambia la pregunta
            binding.invalidateAll()
        }




        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }
                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    questionIndex++
                    //se calcula la puntuación tras el incremento del index para evitar tener que hacer cálculos de más
                    //además, así también se evita usar una variable extra
                    score += computeCurrentQuestionValue(questionIndex)

                    // Advance to the next question
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        // We've won!  Navigate to the gameWonFragment.
                        view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameWonFragment(questionIndex, numQuestions, args.currentLevel, score))
                    }
                } else {
                    // Game over! A wrong answer sends us to the gameOverFragment.
                    view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment(questionIndex, numQuestions, args.currentLevel, score))
                }
            }
        }

        //se añade un listener al botón de las pistas
        binding.hintButton.setOnClickListener { view: View ->
            showHint(view)
        }

        /*
          Hace que Android gestione la persistencia de datos del fragmento.
          Para más info: https://medium.com/hootsuite-engineering/handling-orientation-changes-on-android-41a6b62cb43f
                         https://developer.android.com/guide/topics/resources/runtime-changes.html
         */
        retainInstance = true

        return binding.root
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    /**
     * Randomiza las preguntas pero devuelve un array de enteros que representan los índices que ocupaba
     * cada pregunta en la lista mutable original.
     *
     * **Ejemplo:**
     * + lista inicial { pregunta1, pregunta2, pregunta3}
     *
     * + lista tras randomizar { pregunta2, pregunta1, pregunta3}
     *
     * + array devuelto: { 1, 0, 2 }
     *
     * @param initialize si se establece a true, el método hará una llama a randomizeQuestions() para randomizar el orden de las preguntas
     *
     * @return un array de enteros que representan los índices que ocupaba
     * cada pregunta en la lista mutable original.
     */
    private fun shuffleQuestions(initialize: Boolean = false) : Array<Int> {
        val old = questions.toMutableList()
        if (initialize)
            randomizeQuestions()
        else
            questions.shuffle()

        val order: Array<Int> = Array(old.size) {-1}
        for (i in 0 until old.size) {
            var found: Boolean = false
            var pos = 0
            while (!found && pos < old.size) {
                found = questions[i] == old[pos++]
            }
            //no tiene sentido comparar que la encuentre, porque contienen los mismos elementos y solo saldrá al encontrarla
            order[i] = pos - 1
        }
        return order
    }

    /**
     * Ordena las preguntas siguiendo el orden del array introducido.
     *
     * **Ejemplo:**
     * + lista de preguntas inicial { pregunta1, pregunta2, pregunta3}
     * + array de ordenación: { 1, 0, 2 }
     * + lista de preguntas tras ordenar { pregunta2, pregunta1, pregunta3}
     *
     * @param givenOrder array con los índices que ocuparán las preguntas en la MutableList tras reordenarlas.
     * @throws IndexOutOfBoundsException si el tamaño del array **givenOrder** no se corresponde con el número de preguntas existentes.
     */
    private fun shuffleQuestions(givenOrder: Array<Int>) {
        if (questions.size != givenOrder.size)
            throw IndexOutOfBoundsException("Size of givenOrder is invalid: ${givenOrder.size}!=${questions.size}")
        val newQ = questions.toMutableList()
        for (i in givenOrder.indices){
            newQ[givenOrder[i]] = questions[i]
        }
        questions.clear()
        questions.addAll(newQ)
    }


    /**
     * Establece la pregunta y randomiza las respuestas. Esto solo cambia los datos, no la interfaz gráfica.
     * Hay que llamar a invalitadeAll() en el FragmentGameBinding para actualizar los datos.
     *
     * @param resetHintUsage True si se desea que el valor "usedHint" sea devuelto a "no usado". Por defecto está establecido a true.
     */
    private fun setQuestion(resetHintUsage: Boolean = true) {
        //restablece el uso de pistas a false si fuese necesario
        usedHint = if (resetHintUsage) false else usedHint
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        answers.shuffle()
        //se modifica el título para que incluya el score
        setTitle()
    }

    /**
     * Establece el título de la actionBar
     */
    private fun setTitle()  {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question,
                                                                questionIndex + 1, numQuestions, score)
    }

    /**
     * Establece el número de preguntas en base al nivel seleccionado.
     */
    private fun setQuestionNumber (level: Level) {
        numQuestions = level.numOfQuestions
    }

    /**
     * Muestra la pista de la pregunta actual haciendo uso de un snackbar y establece el valor de usedHint a true.
     */
    private fun showHint (view: View) {
        //se usó pista
        usedHint = true
        //se crea y muestra un snackbar con la pista
        Snackbar.make(view, currentQuestion.hint, Snackbar.LENGTH_LONG).show()
    }

    /**
     * Calcula el valor de los puntos dados por responder correctamente a la pregunta actual en base a la racha.
     * @return el valor de los puntos dados por responder correctamente a la pregunta actual en base a la racha.
     */
    private fun computeCurrentQuestionValue(streak: Int) : Int = if (!usedHint) baseQuestionPoints * streak else (baseQuestionPoints * streak)/hintPenalty


    /**
     * Busca en el recurso strings.xml todos los textos de las preguntas en base a su nombre identificativo,
     * crea las preguntas y las almacena en la variable **questions**.
     *
     * Esto permite añadir tantas preguntas como se quiera al strings.xml y que se puedan cargar cómodamente cambiando únicamente las
     * constantes del top level de este mismo archivo.
     *
     * **Si ya existían preguntas dentro de questions antes de llamar a este método, serán descartadas.**
     */
    private fun populateQuestions () {
        if (questions.isNotEmpty())
            questions.clear()

        for (i in FIRST_DEFINED_QUESTION..LAST_DEFINED_QUESTION) {
            questions.add (Question (text = findString("question$i"),
                answers = listOf( findString("q${i}_answ0"), findString("q${i}_answ1"),
                                  findString("q${i}_answ2"), findString("q${i}_answ3")),
                hint = findString("q${i}_hint")
            ))
        }
    }

    private fun findString(name: String) : String = getString(resources.getIdentifier(name, "string", requireActivity().packageName))
}

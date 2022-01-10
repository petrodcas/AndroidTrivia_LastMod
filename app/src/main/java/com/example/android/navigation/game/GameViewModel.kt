package com.example.android.navigation.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.navigation.R
import com.example.android.navigation.database.Question
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.Level
import kotlinx.coroutines.launch
import kotlin.Exception


class GameViewModel(val selectedLevel: Level, val database: QuestionsDatabaseDao, application: Application) : AndroidViewModel(application) {


    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (Or better yet, don't define the questions in code...)
    private val _questions = MutableLiveData<MutableList<Question>>()
    private val questions: MutableList<Question> //= mutableListOf()
    get() = requireNotNull(_questions.value)

    private var _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
    get() = _currentQuestion

    private var _answers = MutableLiveData<MutableList<String>>()
    val answers: LiveData<MutableList<String>>
    get() = _answers

    private var _questionIndex = MutableLiveData<Int>()
    val questionIndex: LiveData<Int>
    get() = _questionIndex

    private var numQuestions: Int = Level.NO_SELECTED.numOfQuestions

    //puntuación
    private var _score: Int = 0
    val score: Int
    get() = _score


    //evento de partida ganada
    private var _wonGameEvent = MutableLiveData<Boolean>()
    val wonGameEvent: LiveData<Boolean>
    get() = _wonGameEvent


    //evento de partida perdida
    private var _gameOverEvent = MutableLiveData<Boolean>()
    val gameOverEvent: LiveData<Boolean>
    get() = _gameOverEvent


    //evento de pista usada
    private var _usedHintEvent = MutableLiveData<Boolean>()
    val usedHintEvent: LiveData<Boolean>
    get() = _usedHintEvent



    //puntos base por pregunta acertada
    private val baseQuestionPoints: Int = 10
    //penalización (divisoria) para la puntuación conseguida por pregunta en caso de usar la pista
    private val hintPenalty: Int = 2
    //flag para saber si se ha usado la pista
    private var usedHint: Boolean = false



    init {
        //se establece el número de preguntas en base al nivel de la partida
        setQuestionNumber(selectedLevel)
        onGameWonEventFinished()
        onGameOverEventFinished()
        onHintEventFinished()

        //crea las preguntas
        //populateQuestions()
        _questions.value = mutableListOf()
        getRandomQuestions(numQuestions)
    }


    private fun getRandomQuestions(num: Int) {
        viewModelScope.launch {
            _questions.value?.addAll(loadRandomQuestions(num))
            _questionIndex.value = if (questions.size > 0) 0 else throw Exception("Questions size is 0")
            setQuestion()
        }
    }

    private suspend fun loadRandomQuestions(num: Int) : List<Question> {
        return database.getRandomQuestions(num)
    }


    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        _questionIndex.value = 0
        setQuestion()
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
        _currentQuestion.value = questions[requireNotNull(questionIndex.value)]
        // randomize the answers into a copy of the array
        _answers.value = currentQuestion.value?.answers?.toMutableList()
        // and shuffle them
        answers.value?.shuffle()
    }

    /**
     * Establece el número de preguntas en base al nivel seleccionado.
     */
    private fun setQuestionNumber (level: Level) {
        numQuestions = level.numOfQuestions
    }

    /**
     * Calcula el valor de los puntos dados por responder correctamente a la pregunta actual en base a la racha.
     * @return el valor de los puntos dados por responder correctamente a la pregunta actual en base a la racha.
     */
    private fun computeCurrentQuestionValue(streak: Int) : Int = if (!usedHint) baseQuestionPoints * streak else (baseQuestionPoints * streak)/hintPenalty


    private fun onGameWonEvent () {
        _wonGameEvent.value = true
        onGameWonEventFinished()
    }

    private fun onGameWonEventFinished () {
        _wonGameEvent.value = false
    }

    private fun onGameOverEvent () {
        _gameOverEvent.value = true
        onGameOverEventFinished()
    }

    private fun onGameOverEventFinished () {
        _gameOverEvent.value = false
    }

    fun onHintEvent () {
        usedHint = true
        _usedHintEvent.value = true
        onHintEventFinished()
    }

    private fun onHintEventFinished () {
        _usedHintEvent.value = false
    }


    fun onSubmit(checkedId: Int) {
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
            if (answers.value?.get(answerIndex) == currentQuestion.value?.answers?.get(0)) {
                _questionIndex.value = _questionIndex.value?.plus(1)
                //se calcula la puntuación tras el incremento del index para evitar tener que hacer cálculos de más
                //además, así también se evita usar una variable extra
                _score += (computeCurrentQuestionValue(requireNotNull(_questionIndex.value)))

                // Advance to the next question
                if (requireNotNull(questionIndex.value) < numQuestions) {
                    _currentQuestion.value = questions[requireNotNull(questionIndex.value)]
                    setQuestion()
                } else {
                    // We've won!  Navigate to the gameWonFragment.
                    onGameWonEvent()
                }
            } else {
                // Game over! A wrong answer sends us to the gameOverFragment.
                onGameOverEvent()
            }
        }
    }
}
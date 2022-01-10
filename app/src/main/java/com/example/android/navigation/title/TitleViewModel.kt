package com.example.android.navigation.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.navigation.database.Question
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.Level
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



class TitleViewModel(val database: QuestionsDatabaseDao) : ViewModel() {

    /** Nivel seleccionado */
    var selectedLevel: Level
    private set

    /** constructor inicializando el nivel al valor de no seleccionado */
    init {
        selectedLevel = Level.NO_SELECTED
    }

    /** Cambia el nivel de dificultad */
    fun changeDifficulty (newLevel: Level) {
        selectedLevel = newLevel
    }


    //estos dos métodos se usaron para debug, solo borran la base de datos por completo
    fun clearQuestions() {
        viewModelScope.launch { clearDatabase() }
    }

    private suspend fun clearDatabase() {
        database.clearQuestions()
    }


    /** Añade todas las preguntas de la lista a la base de datos. En caso de conflicto, simplemente
     * la que lo provoca. */
    fun addQuestions(list: List<Question>) {
        viewModelScope.launch { insertQuestions(list) }
    }

    private suspend fun insertQuestions(list: List<Question>) {
        database.insertQuestions(list)
    }

    /** Determina si hay suficientes preguntas como para poder jugar en el nivel de dificultad actualmente seleccionado.
     * @return verdadero si hay suficientes preguntas para jugar.
     * */
    fun gotEnoughStoredQuestionsToPlay() : Boolean = countQuestionsInDataBase() >= selectedLevel.numOfQuestions


    /** Devuelve el número de registros de la tabla de la base de datos de preguntas.
     * @return el número de registros de la tabla de preguntas de la base de datos. */
    private fun countQuestionsInDataBase() : Int = runBlocking {
        val counter = async {
            database.countStoredQuestions()
        }
        counter.start()
        counter.await()
    }


}
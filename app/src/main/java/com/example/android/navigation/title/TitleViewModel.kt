package com.example.android.navigation.title

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.*
import com.example.android.navigation.database.Question
import com.example.android.navigation.database.QuestionsDatabase
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.Level
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TitleViewModel(val database: QuestionsDatabaseDao) : ViewModel() {

    var selectedLevel: Level
    private set


    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
    get() = _questions


    init {
        selectedLevel = Level.NO_SELECTED
    }

    fun changeDifficulty (newLevel: Level) {
        selectedLevel = newLevel
    }


    fun addQuestion(question: Question) {
        viewModelScope.launch {
            insertQuestion(question)
        }
    }

    private suspend fun insertQuestion(question: Question) {
        database.insertQuestion(question)
    }


    fun clearQuestions() {
        viewModelScope.launch { clearDatabase() }
    }

    private suspend fun clearDatabase() {
        database.clearQuestions()
    }


    fun addQuestions(list: List<Question>) {
        viewModelScope.launch { insertQuestions(list) }
    }

    private suspend fun insertQuestions(list: List<Question>) {
        database.insertQuestions(list)
    }


    fun gotEnoughStoredQuestionsToPlay() : Boolean = countQuestionsInDataBase() >= selectedLevel.numOfQuestions


    private fun countQuestionsInDataBase() : Int = runBlocking {
        val counter = async {
            database.countStoredQuestions()
        }
        counter.start()
        counter.await()
    }


}
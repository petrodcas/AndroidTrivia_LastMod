package com.example.android.navigation.title

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.navigation.database.Question
import com.example.android.navigation.database.QuestionsDatabase
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.Level
import kotlinx.coroutines.launch

class TitleViewModel(val database: QuestionsDatabaseDao) : ViewModel() {

    lateinit var selectedLevel: Level
    private set


    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
    get() = _questions


//    private val _questionsAddedEvent = MutableLiveData<Boolean>()
//    val questionsAddedEvent: LiveData<Boolean>
//    get() = _questionsAddedEvent


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

    fun addQuestions(list: List<Question>): Boolean {
        return try {
            viewModelScope.launch {
                insertQuestions(list)
            }
            true
        }
        catch (ex: SQLiteConstraintException) {
            false
        }
    }

    private suspend fun insertQuestions(list: List<Question>) {
        database.insertQuestions(list)
    }

}
package com.example.android.navigation.questionAdder

import android.app.Application
import android.text.Spanned
import androidx.lifecycle.*
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.questionListFormatter
import kotlinx.coroutines.launch

class QuestionAdderViewModel(private val database: QuestionsDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _questionList = database.getAllQuestionsAsLiveData()
    val questionListString: LiveData<Spanned>
    get() = Transformations.map(_questionList) { question ->
        questionListFormatter(question, getApplication<Application>().resources) }


    //estos dos métodos se usaron para debug, solo borran la base de datos por completo
    fun clearQuestions() {
        viewModelScope.launch { clearDatabase() }
    }

    private suspend fun clearDatabase() {
        database.clearQuestions()
    }
}
package com.example.android.navigation.questionAdder

import android.app.Application
import android.text.Spanned
import androidx.lifecycle.*
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.questionListFormatter

class QuestionAdderViewModel(private val database: QuestionsDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _questionList = database.getAllQuestionsAsLiveData()
    val questionListString: LiveData<Spanned>
    get() = Transformations.map(_questionList) { question ->
        questionListFormatter(question, getApplication<Application>().resources) }

}
package com.example.android.navigation.questionAdder

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.database.QuestionsDatabaseDao
import java.lang.IllegalArgumentException

class QuestionAdderViewModelFactory(private val database: QuestionsDatabaseDao, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionAdderViewModel::class.java)) {
            return QuestionAdderViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
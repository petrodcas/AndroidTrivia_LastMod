package com.example.android.navigation.questionAdder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.database.QuestionsDatabaseDao
import java.lang.IllegalArgumentException

class QuestionAdderViewModelFactory(private val database: QuestionsDatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionAdderViewModel::class.java)) {
            return QuestionAdderViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
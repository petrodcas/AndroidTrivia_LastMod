package com.example.android.navigation.questionMaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.database.QuestionsDatabaseDao
import java.lang.IllegalArgumentException

class QuestionMakerViewModelFactory(val database: QuestionsDatabaseDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionMakerViewModel::class.java)) {
            return QuestionMakerViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
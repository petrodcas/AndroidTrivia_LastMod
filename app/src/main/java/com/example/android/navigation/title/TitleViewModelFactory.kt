package com.example.android.navigation.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.database.QuestionsDatabaseDao
import java.lang.IllegalArgumentException

class TitleViewModelFactory(val database: QuestionsDatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TitleViewModel::class.java)) {
            return TitleViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
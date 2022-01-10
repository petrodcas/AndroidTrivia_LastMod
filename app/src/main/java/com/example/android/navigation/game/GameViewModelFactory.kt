package com.example.android.navigation.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.Level
import java.lang.IllegalArgumentException

class GameViewModelFactory(
    private val selectedLevel: Level,
    private val dataSource: QuestionsDatabaseDao,
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(selectedLevel, dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.android.navigation.gameWon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.utils.Level
import java.lang.IllegalArgumentException

class GameWonViewModelFactory(
    private val numAciertos: Int,
    private val selectedLevel: Level,
    private val score: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameWonViewModel::class.java)) {
            return GameWonViewModel(numAciertos, selectedLevel, score) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
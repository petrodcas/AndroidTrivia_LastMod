package com.example.android.navigation.gameOver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.navigation.utils.Level
import java.lang.IllegalArgumentException

class GameOverViewModelFactory(
    val numAciertos: Int,
    val selectedLevel: Level,
    val score: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameOverViewModel::class.java)) {
            return GameOverViewModel(numAciertos, selectedLevel, score) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
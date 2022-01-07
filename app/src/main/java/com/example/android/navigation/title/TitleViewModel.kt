package com.example.android.navigation.title

import androidx.lifecycle.ViewModel
import com.example.android.navigation.utils.Level

class TitleViewModel() : ViewModel() {

    lateinit var selectedLevel: Level
    private set

    init {
        selectedLevel = Level.NO_SELECTED
    }

    fun changeDifficulty (newLevel: Level) {
        selectedLevel = newLevel
    }

}
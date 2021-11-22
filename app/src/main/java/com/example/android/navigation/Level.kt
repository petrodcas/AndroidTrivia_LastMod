package com.example.android.navigation

import androidx.annotation.StringRes


enum class Level(@StringRes val stringId: Int, val numOfQuestions: Int) {
    NO_SELECTED(R.string.noSelectedLevel, 0),
    EASY(R.string.easyLevel, 2),
    MEDIUM(R.string.mediumLevel, 4),
    HARD(R.string.hardLevel, 6)
}
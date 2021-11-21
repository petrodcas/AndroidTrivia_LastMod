package com.example.android.navigation

import androidx.annotation.StringRes


enum class Level(@StringRes val stringId: Int) {
    NO_SELECTED(R.string.noSelectedLevel),
    EASY(R.string.easyLevel),
    MEDIUM(R.string.mediumLevel),
    HARD(R.string.hardLevel);
}
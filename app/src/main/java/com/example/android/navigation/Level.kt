package com.example.android.navigation

import androidx.annotation.StringRes

/**
 * Enum con los posibles Niveles de dificultad de las partidas.
 *
 * Todos los niveles tienen por defecto una referencia al string (*stringID*) en strings.xml para poder cargar fácilmente su nombre y también tienen
 * tienen asignados el número de preguntas (*numOfQuestions*) que hay que responder si son seleccionados.
 *
 * Los posibles valores son:**NO_SELECTED**,**EASY**,**MEDIUM** y **HARD**.
 */
enum class Level(@StringRes val stringId: Int, val numOfQuestions: Int) {
    NO_SELECTED(R.string.noSelectedLevel, 0),
    EASY(R.string.easyLevel, 2),
    MEDIUM(R.string.mediumLevel, 4),
    HARD(R.string.hardLevel, 6)
}
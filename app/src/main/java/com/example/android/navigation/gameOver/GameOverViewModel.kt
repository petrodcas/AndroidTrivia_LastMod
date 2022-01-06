package com.example.android.navigation.gameOver

import androidx.lifecycle.ViewModel
import com.example.android.navigation.utils.Level

class GameOverViewModel(
    val numAciertos: Int,
    val selectedLevel: Level,
    val score: Int
) : ViewModel() {
    //no hace falta realizar ninguna acción dentro de este viewmodel, ya que no es necesario que
    //los datos sean LiveData ni se realizan transformaciones sobre los mismos. Además, los propios
    //argumentos ya están protegidos por la palabra reservada val, así que se puede acceder directamente
    //a ellos sin inconvenientes mediante referencias directas (aunque en kotlin se traten de getters
    //implícitos).
}
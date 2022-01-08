package com.example.android.navigation.questionAdder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.navigation.database.QuestionsDatabaseDao

class QuestionAdderViewModel(private val database: QuestionsDatabaseDao) : ViewModel() {

    private var _onAddEvent = MutableLiveData<Boolean>()
    val onAddEvent: LiveData<Boolean>
        get() = _onAddEvent


    init {
        onAddFinished()
    }



    fun onAdd() {
        _onAddEvent.value = true
        onAddFinished()
    }

    private fun onAddFinished() {
        _onAddEvent.value = false
    }

}
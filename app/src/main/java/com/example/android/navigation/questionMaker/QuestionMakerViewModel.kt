package com.example.android.navigation.questionMaker

import android.database.sqlite.SQLiteConstraintException
import android.text.BoringLayout
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.navigation.R
import com.example.android.navigation.database.Question
import com.example.android.navigation.database.QuestionsDatabaseDao
import com.example.android.navigation.utils.areValidTextEditValues
import com.example.android.navigation.utils.createQuestionFromListOfStrings
import com.example.android.navigation.utils.editTextListToStringList
import kotlinx.coroutines.launch


class QuestionMakerViewModel(private val database: QuestionsDatabaseDao) : ViewModel() {

    companion object {
        private val TEXTEDIT_IDS = listOf(
            R.id.textEdit_newQuestionText,
            R.id.textEdit_correctAnswer,
            R.id.textEdit_wrongQuestion1,
            R.id.textEdit_wrongQuestion2,
            R.id.textEdit_wrongQuestion3,
            R.id.textEdit_hintText
        )
    }

    private val _questionWasAddedEvent = MutableLiveData<Boolean>()
    val questionWasAddedEvent: LiveData<Boolean>
    get() = _questionWasAddedEvent

    private val _addQuestionErrorDuplicateEvent = MutableLiveData<Boolean>()
    val addQuestionErrorDuplicateEvent: LiveData<Boolean>
    get() = _addQuestionErrorDuplicateEvent

    private val _addQuestionErrorUnfilledEvent = MutableLiveData<Boolean>()
    val addQuestionErrorUnfilledEvent: LiveData<Boolean>
    get() = _addQuestionErrorUnfilledEvent


    init {
        onQuestionAddedEventFinish()
        onDuplicateQuestionEventFinish()
        onUnfilledFieldEventFinish()
    }


    fun onMakeQuestion (view: View) {
        //se obtienen todos los textEdit de la vista que conformarían la pregunta
        val textEditList = getAllEditText(view)
        //se comprueba que los textEdit tengan campos válidos: que no estén vacíos, conformados por
        //espacios en blanco o sean nulos
        val gotValidFields = areValidTextEditValues(textEditList)

        //si no todos los campos son correctos, se activa el evento de error correspondiente
        if (!gotValidFields) {
            onUnfilledFieldEvent()
            onDuplicateQuestionEventFinish()
        }
        else {
            //si los campos eran correctos, entonces se crea una pregunta en base a los textos de los textedit
            val newQuestion = createQuestionFromListOfStrings(editTextListToStringList(textEditList))
            //se intenta agregar la pregunta a la base de datos.
            if (!addQuestion(newQuestion)) {
                //si no se agregó a la base de datos, entonces es que la pregunta estaba duplicada y se activa el evento de error
                onDuplicatedQuestionEvent()
                //se reinicia el valor del evento
                onDuplicateQuestionEventFinish()
            }
            else {
                //si la pregunta se agregó correctamente, se activa el evento y después se reinicia
                onQuestionAddedEvent()
                onQuestionAddedEventFinish()
            }
        }
    }



    private fun onUnfilledFieldEvent () { _addQuestionErrorUnfilledEvent.value = true }
    private fun onUnfilledFieldEventFinish () { _addQuestionErrorUnfilledEvent.value = false }

    private fun onDuplicatedQuestionEvent () { _addQuestionErrorDuplicateEvent.value = true }
    private fun onDuplicateQuestionEventFinish () { _addQuestionErrorDuplicateEvent.value = false }

    private fun onQuestionAddedEvent () { _questionWasAddedEvent.value = true }
    private fun onQuestionAddedEventFinish () { _questionWasAddedEvent.value = false }

    private fun getAllEditText (view: View) : List<EditText> {
        return TEXTEDIT_IDS.map { editId -> view.findViewById(editId) }
    }


    private fun addQuestion(question: Question): Boolean {
        return try {
            viewModelScope.launch {
                insertQuestion(question)
            }
            true
        }
        catch (ex: SQLiteConstraintException) {
            false
        }
    }

    private suspend fun insertQuestion(question: Question) {
        database.insertQuestion(question)
    }



}
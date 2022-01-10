package com.example.android.navigation.questionMaker

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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


class QuestionMakerViewModel(private val database: QuestionsDatabaseDao) : ViewModel() {

    companion object {
        /** IDs de los EditText que recogen los datos para formar la pregunta */
        private val TEXTEDIT_IDS = listOf(
            R.id.textEdit_newQuestionText,
            R.id.textEdit_correctAnswer,
            R.id.textEdit_wrongQuestion1,
            R.id.textEdit_wrongQuestion2,
            R.id.textEdit_wrongQuestion3,
            R.id.textEdit_hintText
        )
    }

    //Evento para determinar que la pregunta se añadió correctamente
    private val _questionWasAddedEvent = MutableLiveData<Boolean>()
    val questionWasAddedEvent: LiveData<Boolean>
    get() = _questionWasAddedEvent

    //Evento para determinar error de pregunta duplicada en la base de datos
    private val _addQuestionErrorDuplicateEvent = MutableLiveData<Boolean>()
    val addQuestionErrorDuplicateEvent: LiveData<Boolean>
    get() = _addQuestionErrorDuplicateEvent

    //Evento para determinar error de formulario incompleto para formar pregunta
    private val _addQuestionErrorUnfilledEvent = MutableLiveData<Boolean>()
    val addQuestionErrorUnfilledEvent: LiveData<Boolean>
    get() = _addQuestionErrorUnfilledEvent


    init {
        //se inicializan los eventos a su valor de apagado
        onQuestionAddedEventFinish()
        onDuplicateQuestionEventFinish()
        onUnfilledFieldEventFinish()
    }

    fun onMakeQuestion (view : View) {
        //se obtienen todos los textEdit de la vista que conformarían la pregunta
        val textEditList = getAllEditText(view)

        //se comprueba que los textEdit tengan campos válidos: que no estén vacíos, conformados por
        //espacios en blanco o sean nulos
        val gotValidFields = areValidTextEditValues(textEditList)

        //si no todos los campos son correctos, se activa el evento de error correspondiente
        if (!gotValidFields) {
            onUnfilledFieldEvent()
        }
        else {
            //si los campos eran correctos, entonces se crea una pregunta en base a los textos de los textedit
            val newQuestion = createQuestionFromListOfStrings(editTextListToStringList(textEditList))
            //se intenta agregar la pregunta a la base de datos.
            addQuestion(newQuestion)
        }
    }


    /* Métodos para gestionar el estado de los eventos */

    private fun onUnfilledFieldEvent () { _addQuestionErrorUnfilledEvent.value = true }
    fun onUnfilledFieldEventFinish () { _addQuestionErrorUnfilledEvent.value = false }

    private fun onDuplicatedQuestionEvent () { _addQuestionErrorDuplicateEvent.value = true }
    fun onDuplicateQuestionEventFinish () { _addQuestionErrorDuplicateEvent.value = false }

    private fun onQuestionAddedEvent () { _questionWasAddedEvent.value = true }
    fun onQuestionAddedEventFinish () { _questionWasAddedEvent.value = false }



    /** Obtiene todos los EditText del Layout a través de buscarlos por su ID en la vista dada.
     * @param view La vista donde están como hijos los EditText que toman los datos para formar la
     * pregunta.
     * @return Una lista con todos esos EditText
     */
    private fun getAllEditText (view: View) : List<EditText> {
        return TEXTEDIT_IDS.map { editId -> view.findViewById(editId) }
    }


    /** Añade una pregunta a la base de datos.
     * @param question La pregunta a añadir.
     */
    private fun addQuestion(question: Question){
       //si se captura una excepción, entonces es que la pregunta ya existía en la base de datos
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            run {
                onDuplicatedQuestionEvent()
            }
        }
        //si no se captura la excepción, entonces se ejecutará el evento de pregunta añadida
        viewModelScope.launch(exceptionHandler) {
            insertQuestion(question)
            onQuestionAddedEvent()
        }
    }

    private suspend fun insertQuestion(question: Question) {
        database.insertQuestion(question)
    }



}
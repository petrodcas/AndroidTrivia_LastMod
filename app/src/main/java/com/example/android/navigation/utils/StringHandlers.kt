package com.example.android.navigation.utils

import android.app.Application
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.EditText
import androidx.core.text.HtmlCompat
import com.example.android.navigation.R
import com.example.android.navigation.database.Question
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

/* Este archivo contiene una variedad de funciones de utilidad usadas en otras partes del código */

/** Último número de la pregunta por defecto definida en strings.xml */
private const val LAST_DEFINED_QUESTION : Int = 9
/** Primer número de la pregunta por defecto definida en strings.xml */
private const val FIRST_DEFINED_QUESTION: Int = 0


/** Devuelve un objecto Spanned con la lista de preguntas introducida formateada,
 * usando para ello un string de strings.xml, de tal manera que se pueda traducir.
 *
 * @param list Lista de preguntas con la que formar el Spanned
 * @param resource Una referencia a los recursos de la aplicación
 *
 * @return Un Spanned con la lista de preguntas formateada.
 */
fun questionListFormatter (list: List<Question>, resource: Resources) : Spanned {
    val sb = StringBuilder()
    var i = 0

    sb.apply {
        list.forEach { question ->
            append("<br>")
            append("<span><b>")
            append(resource.getString(R.string.question_list_label, ++i))
            append(":</b></span> <span>")
            append(question.text)
            append("</span><br>")
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    }
    else {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

/** Verifica que el String sea válido: que no sea ni null, ni esté conformado por espacios en blanco
 * o esté vacío.
 *
 * @param str El string a comprobar.
 *
 * @return true si es válido o false en caso contrario.
 */
fun verifyValidString (str : String?) : Boolean {
    return !str.isNullOrBlank()
}

/**
 * Verifica que todos los strings de la lista sean válidos conforme al método verifyValidString(str: String).
 *
 * @param strList Lista de strings a comprobar.
 *
 * @return True si todos los strings de la lista son válidos.
 *
 * @see [verifyValidString]
 */
fun verifyValidStrings (strList : List<String>) : Boolean {
    var isValid = true
    var index = 0
    while (isValid && index < strList.size) {
        isValid = verifyValidString(strList[index++])
    }
    return isValid
}

/**
 * Determina si todos los EditText de la lista contienen textos considerados como strings válidos según el
 * método [verifyValidStrings].
 *
 * @param list Lista de EditText a comprobar.
 * @return Verdadero si todos tienen textos válidos.
 *
 */
fun areValidTextEditValues (list: List<EditText>) : Boolean {
    return verifyValidStrings(editTextListToStringList(list))
}

/**
 * Devuelve una lista de strings conformada por los textos de la lista entrante de EditText.
 *
 * @param Lista de EditText de los que se extraerán los textos.
 *
 * @return una lista de Strings conformada por los textos de la listo entrante de EditText.
 */
fun editTextListToStringList (list: List<EditText>) : List<String> {
    return list.map{editText -> editText.text.toString()}
}

/** Devuelve una pregunta creada a partir de la lista de strings entrante.
 *
 * La lista de strings debe tener los Strings en orden conforme a la estructura de la pregunta:
 * 1. Texto de la pregunta.
 * 2. Respuesta correcta.
 * 3. Respuesta incorrecta 1.
 * 4. Respuesta incorrecta 2.
 * 5. Respuesta incorrecta 3.
 * 6. Texto de la pista.
 *
 * @param strings Lista de strings que conformarán la pregunta.
 *
 * @return Una pregunta conformada por los strings de la lista de entrada.
 *
 * @throws IllegalArgumentException Si el tamaño de la lista no es 6.
 */
fun createQuestionFromListOfStrings (strings: List<String>) : Question {
    if (strings.size != 6) throw IllegalArgumentException("Questions are made out of 6 strings but list size is ${strings.size}")

    return Question(
        text = strings[0],
        answers = listOf(strings[1], strings[2], strings[3], strings[4]),
        hint = strings[5]
    )
}



/**
 * Busca en el recurso strings.xml todos los textos de las preguntas por defecto en base a su nombre identificativo,
 * crea las preguntas y las devuelve como una lista de preguntas.
 *
 * Esto permite añadir tantas preguntas como se quiera al strings.xml y que se puedan cargar cómodamente cambiando únicamente las
 * constantes del top level de este mismo archivo.
 *
 * @param app Referencia a la aplicación.
 * @param packageName Nombre del paquete de la actividad que usará el método. Se puede extraer a través de [android.content.ContextWrapper.getPackageName].
 *
 * @return Una lista con las preguntas por defecto.
 */
fun getDefaultQuestions (app: Application, packageName: String) : List<Question> {
    val questions = mutableListOf<Question>()

    for (i in FIRST_DEFINED_QUESTION..LAST_DEFINED_QUESTION) {
        val strings = listOf(
            findString("question$i", app, packageName),
            findString("q${i}_answ0", app, packageName),
            findString("q${i}_answ1", app, packageName),
            findString("q${i}_answ2", app, packageName),
            findString("q${i}_answ3", app, packageName),
            findString("q${i}_hint", app, packageName)
        )
        questions.add(createQuestionFromListOfStrings(strings))
    }

    return questions.toList()
}

/** Obtiene un String de strings.xml a través de su nombre identificativo.
 *
 * Hay que usar este método con cuidado, ya que es muy ineficiente, aunque es conveniente para
 * el uso que se le da en esta práctica en concreto.
 *
 * @param name Nombre identificativo del String.
 * @param app Referencia a la aplicación.
 * @param packageName Nombre del paquete de la actividad que llama al método.
 */
fun findString(name: String, app: Application, packageName: String) : String {
    return app.getString(app.resources.getIdentifier(name, "string", packageName))
}

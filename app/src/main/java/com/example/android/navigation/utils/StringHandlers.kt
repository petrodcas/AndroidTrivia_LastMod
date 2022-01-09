package com.example.android.navigation.utils

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


fun verifyValidString (str : String) : Boolean {
    return !str.isNullOrBlank()
}

fun verifyValidStrings (strList : List<String>) : Boolean {
    var isValid = true
    var index = 0
    while (isValid && index < strList.size) {
        isValid = verifyValidString(strList[index])
    }
    return isValid
}

fun areValidTextEditValues (list: List<EditText>) : Boolean {
    return verifyValidStrings(editTextListToStringList(list))
}

fun editTextListToStringList (list: List<EditText>) : List<String> {
    return list.map{editText -> editText.text.toString()}
}

fun createQuestionFromListOfStrings (strings: List<String>) : Question {
    if (strings.size != 6) throw IllegalArgumentException("Questions are made out of 6 strings but list size is ${strings.size}")

    return Question(
        text = strings[0],
        answers = listOf(strings[1], strings[2], strings[3], strings[4]),
        hint = strings[5]
    )
}
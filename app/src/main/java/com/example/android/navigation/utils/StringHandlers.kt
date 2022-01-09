package com.example.android.navigation.utils

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.example.android.navigation.R
import com.example.android.navigation.database.Question
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
package com.example.android.navigation.database

data class Question(
    val text: String,
    val answers: List<String>,
    val hint: String)
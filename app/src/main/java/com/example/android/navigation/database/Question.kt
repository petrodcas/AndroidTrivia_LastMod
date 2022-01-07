package com.example.android.navigation.database

import androidx.room.*

@Entity(tableName="questions_table", indices = [Index(value=["text"], unique = true)])
//@Entity(tableName="questions_table")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val questionID: Long = 0L,
    @ColumnInfo(name="text")
    val text: String,
    @ColumnInfo(name="answers")
    @TypeConverters(TypeConverterHelper::class)
    val answers: List<String>,
    @ColumnInfo(name="hint")
    val hint: String)
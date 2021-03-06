package com.example.android.navigation.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QuestionsDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertQuestion(question: Question)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestions(list: List<Question>)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun removeQuestion(question: Question)

    @Query("DELETE FROM questions_table")
    suspend fun clearQuestions()

    @Query("SELECT * FROM questions_table")
    fun getAllQuestionsAsLiveData() : LiveData<List<Question>>

    @Query("SELECT * FROM questions_table ORDER BY RANDOM() LIMIT :number")
    suspend fun getRandomQuestions(number: Int) : List<Question>

    @Query("SELECT COUNT(questionID) FROM questions_table")
    suspend fun countStoredQuestions() : Int

}
package com.example.android.navigation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Question::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(TypeConverterHelper::class)
abstract class QuestionsDatabase : RoomDatabase() {

    //referencia a la interfaz DAO
    abstract val questionsDatabaseDao: QuestionsDatabaseDao

    companion object {
        //referencia a la base de datos
        @Volatile
        private var instance: QuestionsDatabase? = null


        fun getInstance(context: Context): QuestionsDatabase {
            synchronized(this) {

                var instanceHolder = instance
                if (instanceHolder == null) {
                    instanceHolder = Room.databaseBuilder(
                        context.applicationContext,
                        QuestionsDatabase::class.java,
                        "questions_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    instance = instanceHolder
                }

                return instanceHolder
            }
        }

    }

}
package com.example.android.navigation.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TypeConverterHelper {

    @TypeConverter
    fun listOfStringsToString (list: List<String>) : String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToListOfStrings(str: String) : List<String> {
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(str, listType)
    }
}
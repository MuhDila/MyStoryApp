package com.muhdila.mystoryapp.data.helper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.muhdila.mystoryapp.data.remote.response.ListStory

@Database(
    entities = [ListStory::class], // List of entities (database tables) in the database
    version = 1,
    exportSchema = false
)
abstract class StoryData : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: StoryData? = null

        // Create a singleton instance of the StoryData database
        @JvmStatic
        fun getDataStory(context: Context): StoryData {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, StoryData::class.java, "story_data"
                )
                    .fallbackToDestructiveMigration() // Handles database schema migrations
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
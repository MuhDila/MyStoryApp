package com.muhdila.mystoryapp.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Class responsible for handling user preferences using DataStore
class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Function to get the user's data as a Flow
    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[TOKEN_KEY] ?: "", // Retrieve token from preferences
                preferences[STATE_KEY] ?: false // Retrieve login state from preferences
            )
        }
    }

    // Function to save user data to preferences
    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = user.token // Save user's token to preferences
            preferences[STATE_KEY] = user.isLogin // Save login state to preferences
        }
    }

    // Function to log out the user by updating the login state
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
            preferences[STATE_KEY] = false // Set login state to false to log out
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val TOKEN_KEY = stringPreferencesKey("token") // Define the key for token preference
        private val STATE_KEY = booleanPreferencesKey("state") // Define the key for login state preference

        // Create a singleton instance of UserPreferences
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
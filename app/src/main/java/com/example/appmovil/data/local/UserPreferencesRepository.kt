package com.example.appmovil.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val USER_TOKEN = stringPreferencesKey("user_token")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val userToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN]
        }

    val userRole: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ROLE]
        }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL]
        }

    suspend fun saveUserSession(token: String, role: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
            preferences[USER_ROLE] = role
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

package com.example.worklink.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_USER_ID = longPreferencesKey("user_id")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_ROLE_ID = longPreferencesKey("role_id")
    }

    suspend fun saveSession(token: String, email: String, userId: Long, name: String, roleId: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_EMAIL] = email
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_NAME] = name
            prefs[KEY_ROLE_ID] = roleId
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val email: Flow<String?> = context.dataStore.data.map { it[KEY_EMAIL] }
    val userId: Flow<Long?> = context.dataStore.data.map { it[KEY_USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[KEY_USER_NAME] }
    val roleId: Flow<Long?> = context.dataStore.data.map { it[KEY_ROLE_ID] }

    suspend fun isLoggedIn(): Boolean {
        var loggedIn = false
        context.dataStore.data.map { it[KEY_TOKEN] }.collect {
            loggedIn = !it.isNullOrEmpty()
        }
        return loggedIn
    }
}
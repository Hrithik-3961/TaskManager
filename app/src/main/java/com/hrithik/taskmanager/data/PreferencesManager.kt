package com.hrithik.taskmanager.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder { BY_TIME_ADDED, BY_DUE_DATE }

data class Preferences(val sortOrder: SortOrder)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext cntxt: Context) {
    private val context = cntxt

    private val Context.dataStore by preferencesDataStore("user_preferences")
    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_TIME_ADDED.name
            )
            Preferences(sortOrder)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }
}
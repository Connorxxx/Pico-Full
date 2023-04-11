package com.connor.picofull.datastores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("language")

class LanguageDatastore @Inject constructor(@ApplicationContext context: Context) {
    private val getDataStore = context.dataStore

    private val languageID = intPreferencesKey("language_id")

    val languageFlow = getDataStore.data.map { it[languageID] }.flowOn(Dispatchers.IO)

    suspend fun storeLanguage(value: Int) {
        getDataStore.edit {
            withContext(Dispatchers.IO) { it[languageID] = value }
        }
    }
}
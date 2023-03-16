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
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext context: Context) {

    private val getDataStore = context.dataStore

    private val pureState = intPreferencesKey("wave_id")

    val idFlow = getDataStore.data.map { it[pureState] }
        .flowOn(Dispatchers.IO)

    suspend fun storeWaveId(value: Int) {
        getDataStore.edit {
            withContext(Dispatchers.IO) { it[pureState] = value }
        }
    }
}
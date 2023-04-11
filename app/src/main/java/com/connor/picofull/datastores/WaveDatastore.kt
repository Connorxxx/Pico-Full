package com.connor.picofull.datastores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("wave")

@Singleton
class WaveDatastore  @Inject constructor(@ApplicationContext context: Context) {
    private val getDataStore = context.dataStore

    private val waveState = booleanPreferencesKey("wave_states")

    val waveFLow = getDataStore.data.map { it[waveState] ?: false }.flowOn(Dispatchers.IO)

    suspend fun storeWave(value: Boolean) {
        getDataStore.edit {
            withContext(Dispatchers.IO) { it[waveState] = value}
        }
    }

}
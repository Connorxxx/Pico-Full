package com.connor.picofull.datastores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.connor.picofull.utils.logCat
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

    private val languageID = intPreferencesKey("language_id")

    private val waveState = booleanPreferencesKey("wave_state")

    private val serialPort = stringPreferencesKey("serial_port")

    val languageFlow = getDataStore.data.map { it[languageID] }.flowOn(Dispatchers.IO)

    val waveFLow = getDataStore.data.map { it[waveState] ?: false }.flowOn(Dispatchers.IO)

    val serialFlow = getDataStore.data.map { it[serialPort]}

    suspend fun storeLanguage(value: Int) {
        getDataStore.edit {
            withContext(Dispatchers.IO) { it[languageID] = value }
        }
    }

    suspend fun storeWave(value: Boolean) {
        getDataStore.edit {
            withContext(Dispatchers.IO) { it[waveState] = value}
        }
    }

    suspend fun storeSerial(value: String) {
        getDataStore.edit {
            it[serialPort] = value
        }
    }
}
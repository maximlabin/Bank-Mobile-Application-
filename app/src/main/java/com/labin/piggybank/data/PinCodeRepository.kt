package com.labin.piggybank.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class PinCodeRepository @Inject constructor (
    private val dataStore: DataStore<Preferences>
) {
    private val PIN_HASH_KEY = stringPreferencesKey("pin_hash")

    val hasPinCode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PIN_HASH_KEY] != null
    }

    suspend fun clearPin() {
        dataStore.edit { preferences ->
            preferences.remove(PIN_HASH_KEY)
        }
    }
}

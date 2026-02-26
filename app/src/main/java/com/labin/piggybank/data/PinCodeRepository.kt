package com.labin.piggybank.data

import androidx.datastore.core.DataStore
import com.labin.piggybank.api.AuthApi
import androidx.datastore.preferences.core.*
import com.labin.piggybank.utilities.PinCodeManager
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

    suspend fun savePin(pin: String) {
        val hashed = PinCodeManager.hashPin(pin)
        dataStore.edit { preferences ->
            preferences[PIN_HASH_KEY] = hashed
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val hashed = dataStore.data.first()[PIN_HASH_KEY] ?: return false
        return PinCodeManager.verifyPin(pin, hashed)
    }

    suspend fun clearPin() {
        dataStore.edit { preferences ->
            preferences.remove(PIN_HASH_KEY)
        }
    }
}

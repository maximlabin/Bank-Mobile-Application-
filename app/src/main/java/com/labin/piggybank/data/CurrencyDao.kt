package com.labin.piggybank.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies WHERE baseCurrency = :baseCurrency ORDER BY code ASC")
    fun getCurrenciesByBase(baseCurrency: String): Flow<List<Currency>>

    @Query("DELETE FROM currencies WHERE baseCurrency = :baseCurrency")
    suspend fun deleteByBase(baseCurrency: String)

    @Query("SELECT COUNT(*) FROM currencies")
    suspend fun getCount(): Int

    @Query("SELECT id FROM currencies WHERE code = :code LIMIT 1")
    suspend fun getIdByCode(code: String): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrencies(currencies: List<Currency>)

    @Query("SELECT MAX(lastUpdate) FROM currencies WHERE baseCurrency = :baseCurrency")
    suspend fun getLastUpdateTime(baseCurrency: String): Long?
}
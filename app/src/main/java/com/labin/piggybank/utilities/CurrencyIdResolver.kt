package com.labin.piggybank.utilities

import com.labin.piggybank.data.CurrencyDao

object CurrencyIdResolver {
    private const val FALLBACK_RUB_ID = 1L

    /**
     * Если передан ID == 1, возвращает реальный ID рубля из БД.
     * Иначе возвращает исходный ID без изменений.
     */
    suspend fun resolve(id: Long, dao: CurrencyDao): Long {
        return if (id == FALLBACK_RUB_ID) {
            dao.getIdByCode("RUB") ?: FALLBACK_RUB_ID
        } else {
            id
        }
    }
}
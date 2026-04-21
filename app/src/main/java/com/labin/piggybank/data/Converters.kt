package com.labin.piggybank.data

import androidx.room.TypeConverter
import com.labin.piggybank.domain.CategoryType
import com.labin.piggybank.domain.TransactionType
import com.labin.piggybank.utilities.AccountType
import java.util.Date
import java.math.BigDecimal

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(type: CategoryType): String = type.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String = value.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String): BigDecimal = BigDecimal(value)

    @TypeConverter fun fromAccountType(type: AccountType): String = type.name
    @TypeConverter fun toAccountType(value: String): AccountType = AccountType.valueOf(value)
}
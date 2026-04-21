package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.labin.piggybank.domain.CategoryType

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name", "type", "isDefault"], unique = true)
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconResId: Int?,
    val type: CategoryType,
    val isDefault: Boolean = false
)
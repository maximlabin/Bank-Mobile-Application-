package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category (
    @PrimaryKey
    val id: String,
    val name: String,
    val colorHex: String,
    val color: Int,
)
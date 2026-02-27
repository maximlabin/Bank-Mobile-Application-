package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val surname: String
)
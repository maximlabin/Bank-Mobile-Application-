package com.labin.piggybank.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "attachments",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["transactionId"])]
)
data class Attachment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val fileUrl: String,
    val fileType: String,
    val uploadedAt: Date
)
package com.labin.piggybank.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.labin.piggybank.domain.TransactionType
import java.math.BigDecimal
import java.util.Date

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Merchant::class,
            parentColumns = ["id"],
            childColumns = ["merchant_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["id"],
            childColumns = ["currency_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = FinancialGoal::class,
            parentColumns = ["id"],
            childColumns = ["goal_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["account_id"]),
        Index(value = ["category_id"]),
        Index(value = ["merchant_id"]),
        Index(value = ["currency_id"]),
        Index(value = ["goal_id"]),
        Index(value = ["transaction_date"]),
        Index(value = ["account_id", "transaction_date"]),
        Index(value = ["category_id", "transaction_date"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "account_id")
    val accountId: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,

    @ColumnInfo(name = "merchant_id")
    val merchantId: Long? = null,

    @ColumnInfo(name = "currency_id")
    val currencyId: Long,

    @ColumnInfo(name = "goal_id")
    val goalId: Long? = null,

    val amount: BigDecimal,

    val type: TransactionType,

    @ColumnInfo(name = "transaction_date")
    val transactionDate: Date,

    val description: String? = null
)
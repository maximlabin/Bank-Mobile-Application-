package com.labin.piggybank.data

import androidx.room.*
import com.labin.piggybank.domain.AccountType
import java.math.BigDecimal
import java.math.RoundingMode

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["id"],
            childColumns = ["currency_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["currency_id"]),
        Index(value = ["account_type"]),
        Index(value = ["is_archived"]),
        Index(value = ["user_id", "is_archived"])
    ]
)
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "currency_id")
    val currencyId: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "balance")
    val balance: BigDecimal,
    @ColumnInfo(name = "account_type")
    val type: AccountType = AccountType.CASH,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "account_number")
    val accountNumber: String? = null,
    @ColumnInfo(name = "bank_name")
    val bankName: String? = null,
    @ColumnInfo(name = "card_color")
    val cardColor: String? = null
) {
    fun isActive(): Boolean = !isArchived

    fun getFormattedBalance(): String {
        return balance.setScale(2, RoundingMode.HALF_UP).toPlainString()
    }
}
package com.labin.piggybank.ui.model.mapper


import com.labin.piggybank.data.TransactionEntity
import com.labin.piggybank.ui.model.Transaction as UiTransaction

object TransactionMapper {
    fun toUi(entity: TransactionEntity): UiTransaction = UiTransaction(
        id = entity.id,
        description = entity.description,
        amount = entity.amount,
        isIncome = entity.isIncome
    )

    fun toUiList(transactions: List<TransactionEntity>): List<UiTransaction> =
        transactions.map { toUi(it) }
}
package com.example.tsetmc

data class Market (
    val symbol: String?,
    val name: String?,
    val no: Int?,
    val value: Int?,
    val yesterday: Int?,
    val first: Int?,
    val lastTransactionValue: Int?,
    val lastTransactionChanges: Float?,
    val lastTransactionPercent: Float?
) {

    override fun toString(): String {
        return "نماد: ${symbol}، آخرین معامله درصد: $lastTransactionPercent\n"
    }

}
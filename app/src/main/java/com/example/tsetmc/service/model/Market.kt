package com.example.tsetmc.service.model

data class Market(
    val symbol: String?,
    val lastTransactionPercent: Float?,
    val lastPriceValue: Float?,
    val lastPricePercent: Float?,
    val eps: Int?,
    val p_e: Float?,
    val buyNo: Int?,
    val buyVolume: Int?,
    val buyPrice: Float?,
    val sellPrice: Float?,
    val sellVolume: Int?,
    val sellNo: Int?
)
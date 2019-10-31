package com.example.tsetmc.ui

import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.ui.adapter.item.MarketItem

private fun filter(from: Float, to: Float, filterField: Float?): Boolean {
    if (filterField == null)
        return false

    return filterField in from..to
}

fun findChosenFieldValueInMarket(item: Market, i: Int): Float? =
    when(i){
        0 -> item.lastTransactionPercent
        1 -> item.lastPriceValue
        2 -> item.lastPricePercent
        3 -> item.eps?.toFloat()
        4 -> item.p_e
        5 -> item.buyNo?.toFloat()
        6 -> item.buyVolume?.toFloat()
        7 -> item.buyPrice
        8 -> item.sellPrice
        9 -> item.sellVolume?.toFloat()
        else -> item.sellNo?.toFloat()
    }

fun setAppropriateFilter(i: Int, from: String, to: String, item: Market): Boolean {
    return filter(from.toFloat(), to.toFloat(), findChosenFieldValueInMarket(item, i))
}

class MarketComparator(var field: Int): Comparator<MarketItem>{
    override fun compare(o1: MarketItem?, o2: MarketItem?): Int {
        return (((findChosenFieldValueInMarket(o1!!.market, field) ?: 0.0f) - (findChosenFieldValueInMarket(o2!!.market, field) ?: 0.0f)) * 1000.0).toInt()
    }

}
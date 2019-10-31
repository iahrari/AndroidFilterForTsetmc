package com.example.tsetmc.service.utils

import android.util.Log
import com.example.tsetmc.service.model.HistoryItem
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.ui.adapter.item.MarketItem
import java.util.*
import kotlin.collections.ArrayList

fun generateDynamicFolderName(): Long{
    val calendar = GregorianCalendar()

    val year = calendar.get(JalaliCalendar.YEAR)
    val month = calendar.get(JalaliCalendar.MONTH)
    val day = calendar.get(JalaliCalendar.DAY_OF_MONTH)

    val convert =
        GregorianCalendar(year, month, day, 4, 8, 7)
    Log.i("currentTime", (Date().time == convert.time.time).toString())
    return convert.time.time
}

fun getLongArrayOfDataSubDirectoriesSorted(subDirectories: Array<String>): List<HistoryItem>{
    val longSubDirectories = ArrayList<HistoryItem>()
    for(sub in subDirectories)
        if (sub.toLongOrNull() != null)
            longSubDirectories.add(HistoryItem().apply { dateLong = sub.toLong() })

    longSubDirectories.sortByDescending(HistoryItem::dateLong)

    @Suppress("UNCHECKED_CAST")
    return longSubDirectories
}

fun prepareMarketItemList(list: List<Market>): List<MarketItem>{
    val itemList = arrayListOf<MarketItem>()
    list.forEach {
        itemList.add(MarketItem(it))
    }

    return itemList
}
package com.example.tsetmc.service.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tsetmc.ui.adapter.MainListViewHolder
import com.mikepenz.fastadapter.items.AbstractItem

data class Market (
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
) : AbstractItem<MainListViewHolder>(){
    override val layoutRes: Int
        get() = MainListViewHolder.layout
    override val type: Int
        get() = MainListViewHolder.id

    override fun getViewHolder(v: View): MainListViewHolder = MainListViewHolder(v)


    override fun toString(): String {
        return "نماد: ${symbol}، آخرین معامله درصد: $lastTransactionPercent\n"
    }

}
package com.example.tsetmc.ui.adapter.item

import android.view.View
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.ui.adapter.viewholder.MainListViewHolder
import com.mikepenz.fastadapter.items.AbstractItem

data class MarketItem (
    val market: Market
) : AbstractItem<MainListViewHolder>(){
    override val layoutRes: Int
        get() = MainListViewHolder.layout
    override val type: Int
        get() = MainListViewHolder.id

    override fun getViewHolder(v: View): MainListViewHolder =
        MainListViewHolder(v)
}
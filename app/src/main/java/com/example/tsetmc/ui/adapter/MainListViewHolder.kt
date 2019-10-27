package com.example.tsetmc.ui.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.tsetmc.R
import com.example.tsetmc.databinding.MarketItemBinding
import com.example.tsetmc.service.model.Market
import com.mikepenz.fastadapter.FastAdapter

class MainListViewHolder(view: View): FastAdapter.ViewHolder<Market>(view) {
    private var binding: MarketItemBinding? = DataBindingUtil.bind(view)

    override fun unbindView(item: Market) {
        binding?.market = null
    }

    override fun bindView(item: Market, payloads: MutableList<Any>) {
        binding?.market = item
    }

    companion object {
        const val layout = R.layout.market_item
        const val id = R.id.market_item
    }
}
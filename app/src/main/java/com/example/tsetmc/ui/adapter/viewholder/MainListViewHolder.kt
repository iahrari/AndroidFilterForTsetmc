package com.example.tsetmc.ui.adapter.viewholder

import android.view.View
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import com.example.tsetmc.R
import com.example.tsetmc.databinding.MarketItemBinding
import com.example.tsetmc.ui.adapter.item.MarketItem
import com.mikepenz.fastadapter.FastAdapter

class MainListViewHolder(view: View): FastAdapter.ViewHolder<MarketItem>(view) {
    private var binding: MarketItemBinding? = DataBindingUtil.bind(view)
    override fun unbindView(item: MarketItem) {}

    override fun bindView(item: MarketItem, payloads: MutableList<Any>) {
        binding?.market = item.market
        binding?.marketItemScroll?.post {
            binding?.marketItemScroll?.fullScroll(ScrollView.FOCUS_RIGHT)
        }
    }

    companion object {
        const val layout = R.layout.market_item
        const val id = R.id.market_item
    }
}
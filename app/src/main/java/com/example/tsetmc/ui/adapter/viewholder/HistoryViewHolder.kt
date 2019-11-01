package com.example.tsetmc.ui.adapter.viewholder

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.tsetmc.databinding.HistoryItemBinding
import com.example.tsetmc.service.model.HistoryItem
import com.mikepenz.fastadapter.FastAdapter

class HistoryViewHolder(view: View): FastAdapter.ViewHolder<HistoryItem>(view) {
    val binding: HistoryItemBinding? = DataBindingUtil.bind(view)
    private var observer : Observer<Boolean>? = null
    override fun bindView(item: HistoryItem, payloads: MutableList<Any>) {
        observer = Observer { binding?.historyRadio!!.isChecked = it }
        item.liveIsSelected.observeForever(observer!!)
        binding?.date = item
    }

    override fun unbindView(item: HistoryItem) {
        binding?.date = null
        observer = null
    }
}
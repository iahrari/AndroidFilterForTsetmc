package com.example.tsetmc.ui.adapter.clickevent

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tsetmc.service.model.HistoryItem
import com.example.tsetmc.ui.adapter.viewholder.HistoryViewHolder
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook

class HistoryClickEvent(private val itemAdapter: ItemAdapter<HistoryItem>, private val action: (Long) -> Unit) : ClickEventHook<HistoryItem>() {
    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<HistoryItem>,
        item: HistoryItem
    ) {
        if (!item.isSelected) {
            for (i in itemAdapter.adapterItems)
                i.setIsSelected(false)

            item.setIsSelected(true)
            action(item.dateLong)
        }
    }

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        return (viewHolder as HistoryViewHolder).binding?.historyRadio
    }
}
package com.example.tsetmc.ui.adapter.clickevent

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tsetmc.service.model.HistoryItem
import com.example.tsetmc.ui.adapter.viewholder.HistoryViewHolder
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook

class HistoryDeleteEvent(private val action: (HistoryItem, Int) -> Unit): ClickEventHook<HistoryItem>() {
    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<HistoryItem>,
        item: HistoryItem
    ) {
        action(item, position)
    }

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        return if(viewHolder is HistoryViewHolder) viewHolder.binding!!.deleteButton else null
    }
}
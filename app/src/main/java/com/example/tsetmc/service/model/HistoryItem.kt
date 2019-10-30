package com.example.tsetmc.service.model

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tsetmc.R
import com.example.tsetmc.service.utils.JalaliCalendar
import com.example.tsetmc.ui.adapter.viewholder.HistoryViewHolder
import com.mikepenz.fastadapter.items.AbstractItem
import java.util.*

data class HistoryItem(var dateString: String = ""): AbstractItem<HistoryViewHolder>() {
    override var isSelectable: Boolean = true
    private val _liveIsSelected = MutableLiveData<Boolean>()
    override var isSelected: Boolean = false


    fun setIsSelected(value: Boolean, i: Int){
        _liveIsSelected.postValue(value)
        isSelected = value
    }

    fun setIsSelected(value: Boolean){
        _liveIsSelected.value = value
        isSelected = value
    }

    val liveIsSelected: LiveData<Boolean> get() = _liveIsSelected
    var dateLong: Long = 0
        set(value) {
            val gCalendar = GregorianCalendar().apply { time = Date(value)}
            val calendar = JalaliCalendar.gregorianToJalali(
                JalaliCalendar.YearMonthDate(
                    gCalendar.get(GregorianCalendar.YEAR),
                    gCalendar.get(GregorianCalendar.MONTH),
                    gCalendar.get(GregorianCalendar.DAY_OF_MONTH)
            ))

            val year = calendar.year
            val month = calendar.month + 1
            val day = calendar.date
            dateString = "$year/$month/$day"
            field = value
        }


    override val layoutRes: Int
        get() = R.layout.history_item
    override val type: Int
        get() = R.id.history_item

    override fun getViewHolder(v: View): HistoryViewHolder =
        HistoryViewHolder(v)
}
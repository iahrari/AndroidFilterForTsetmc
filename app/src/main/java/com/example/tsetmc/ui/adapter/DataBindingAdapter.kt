package com.example.tsetmc.ui.adapter

import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tsetmc.ui.adapter.viewholder.MainListViewHolder

object DataBindingAdapter {
    @JvmStatic
    @BindingAdapter("setAdapter", requireAll = true)
    fun setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<MainListViewHolder>){
        view.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("setSpinnerAdapter")
    fun setSpinner(spinner: Spinner, dummyData: Boolean){
        val array = arrayListOf(
            "آخرین معامله - درصد",
            "آخرین قیمت مقدار",
            "آخرین قیمت درصد",
            "EPS",
            "P/E",
            "خرید - تعداد",
            "خرید - حجم",
            "خرید - قیمت",
            "فروش - قیمت",
            "فروش - حجم",
            "فروش - تعداد"
        )

        val adapter = ArrayAdapter<String>(spinner.context!!, android.R.layout.simple_spinner_dropdown_item, array)
        spinner.adapter = adapter
    }
}
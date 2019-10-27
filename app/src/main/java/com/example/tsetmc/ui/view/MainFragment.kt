package com.example.tsetmc.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tsetmc.R
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.viewmodel.MainFragmentViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment() {
    lateinit var viewModel: MainFragmentViewModel
    lateinit var itemAdapter: ItemAdapter<Market>
    lateinit var fastAdapter: FastAdapter<Market>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setViewModel()
    }

    private fun setRecyclerView(){
        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter)

        recycler_main_list.adapter = fastAdapter
    }

    private fun setViewModel(){
        viewModel = ViewModelProvider(
            this,
            MainFragmentViewModel.Factory(context!!.applicationContext)
        ).get(MainFragmentViewModel::class.java)
        viewModel.itemsLiveData.observe(this, Observer {
            Log.i("observeCalled", it.size.toString())
            itemAdapter.add(it)
            fastAdapter.notifyAdapterDataSetChanged()
        })
    }
}
package com.example.tsetmc.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tsetmc.R
import com.example.tsetmc.databinding.FilterDialogBinding
import com.example.tsetmc.databinding.HistoryDialogBinding
import com.example.tsetmc.databinding.SortDialogBinding
import com.example.tsetmc.service.model.HistoryItem
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.ui.*
import com.example.tsetmc.ui.adapter.clickevent.HistoryClickEvent
import com.example.tsetmc.viewmodel.MainFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import kotlinx.android.synthetic.main.content_fragment_main.*

class MainFragment : Fragment() {
    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var itemAdapter: ItemAdapter<Market>
    private lateinit var fastAdapter: FastAdapter<Market>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun setFilterBottomSheet() {
        val dBinding: FilterDialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.filter_dialog, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dBinding.root)
        setSpinner(dBinding.spinnerDialog)

        dBinding.apply {
            toEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.handleItemsFiltering(
                        spinnerDialog.selectedItemPosition,
                        fromEditText.text.toString(),
                        toEditText.text.toString(),
                        toEditText.isThereAnyKindOfError()
                    )
                    dialog.dismiss()
                    true
                } else false
            }
        }
        dialog.show()
    }

    private fun setSortBottomSheet() {
        val dBinding: SortDialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.sort_dialog, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dBinding.root)
        setSpinner(dBinding.spinnerDialog)
        dBinding.submitSorting.setOnClickListener {
            viewModel.handleItemsSorting(dBinding.spinnerDialog.selectedItemPosition)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        setRecyclerView()
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.filter_menu -> setFilterBottomSheet()
                R.id.sort_menu -> setSortBottomSheet()
                R.id.history_menu -> setHistoryBottomSheet()
            }

            true
        }
    }

    private fun setHistoryBottomSheet() {
        val dBinding: HistoryDialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.history_dialog, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dBinding.root)
        val hAdapter = FastAdapter.with(viewModel.historyItemAdapter)
//        hAdapter.onClickListener = { v: View?, _: IAdapter<HistoryItem>, item: HistoryItem, _: Int ->
//            v?.let {
//                Toast.makeText(v.context, item.dateString, Toast.LENGTH_LONG).show()
//            }
//            false
//        }
//
//        hAdapter.onPreClickListener = {_, _, _, _ -> true }

        hAdapter.addEventHook(HistoryClickEvent(viewModel.historyItemAdapter) {viewModel.retrieveDataByTime(it)})
        dBinding.recyclerHistory.adapter = hAdapter
        dialog.show()
    }

    private fun setRecyclerView() {
        fastAdapter = FastAdapter.with(itemAdapter)
        recycler_main_list.adapter = fastAdapter
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainFragmentViewModel.Factory(context!!.applicationContext)
        ).get(MainFragmentViewModel::class.java)

        itemAdapter = viewModel.itemAdapter
    }
}
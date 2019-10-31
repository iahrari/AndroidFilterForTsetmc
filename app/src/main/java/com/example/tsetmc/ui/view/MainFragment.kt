package com.example.tsetmc.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tsetmc.R
import com.example.tsetmc.databinding.*
import com.example.tsetmc.ui.*
import com.example.tsetmc.ui.adapter.clickevent.HistoryClickEvent
import com.example.tsetmc.viewmodel.MainFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.FastAdapter

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var hDBinding: HistoryDialogBinding
    private lateinit var sDBinding: SortDialogBinding
    private lateinit var dBinding: FilterDialogBinding

    private lateinit var filterDialog: BottomSheetDialog
    private lateinit var sortDialog: BottomSheetDialog
    private lateinit var historyDialog: BottomSheetDialog

    private lateinit var viewModel: MainFragmentViewModel
    
    private fun setDataBindings(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        hDBinding = DataBindingUtil.inflate(layoutInflater, R.layout.history_dialog, null, false)
        sDBinding = DataBindingUtil.inflate(layoutInflater, R.layout.sort_dialog, null, false)
        dBinding = DataBindingUtil.inflate(layoutInflater, R.layout.filter_dialog, null, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setDataBindings(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        setBottomNavigation()
    }

    private fun setDialogs(){
        filterDialog = BottomSheetDialog(context!!).apply { setContentView(dBinding.root) }
        sortDialog = BottomSheetDialog(context!!).apply { setContentView(sDBinding.root) }
        historyDialog = BottomSheetDialog(context!!).apply { setContentView(hDBinding.root) }
    }

    private fun setBottomNavigation() {
        setDialogs()
        binding.fragmentContent.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.filter_menu -> setFilterBottomSheet()
                R.id.sort_menu -> setSortBottomSheet()
                R.id.history_menu -> setHistoryBottomSheet()
            }

            true
        }
    }

    private fun setFilterBottomSheet() {
        dBinding.apply {
            toEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.handleItemsFiltering(
                        spinnerDialog.selectedItemPosition,
                        fromEditText.text.toString(),
                        toEditText.text.toString(),
                        toEditText.isThereAnyKindOfError()
                    )
                    filterDialog.dismiss()
                    true
                } else false
            }
        }
        filterDialog.show()
    }

    private fun setSortBottomSheet() {
        sDBinding.submitSorting.setOnClickListener {
            viewModel.handleItemsSorting(sDBinding.spinnerSortDialog.selectedItemPosition)
            sortDialog.dismiss()
        }
        sortDialog.show()
    }

    private fun setHistoryBottomSheet() {
        val hAdapter = FastAdapter.with(viewModel.historyItemAdapter)
        hAdapter.addEventHook(HistoryClickEvent(viewModel.historyItemAdapter) {viewModel.retrieveDataByTime(it)})
        hDBinding.recyclerHistory.adapter = hAdapter
        historyDialog.show()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainFragmentViewModel.Factory(context!!.applicationContext)
        ).get(MainFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.fragmentContent.viewModel = viewModel
    }
}
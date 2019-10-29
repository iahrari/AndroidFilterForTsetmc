package com.example.tsetmc.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tsetmc.R
import com.example.tsetmc.databinding.FilterDialogBinding
import com.example.tsetmc.databinding.SortDialogBinding
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.ui.*
import com.example.tsetmc.viewmodel.MainFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import kotlinx.android.synthetic.main.content_fragment_main.*

class MainFragment : Fragment() {
    private val comparator: MarketComparator = MarketComparator(0)
    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var itemAdapter: ItemAdapter<Market>
    private lateinit var fastAdapter: FastAdapter<Market>
    private lateinit var itemListImpl: ComparableItemListImpl<Market>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun setFilterDoneByEditText(
        i: Spinner, from: EditText,
        to: EditText, dismiss: () -> Unit) {
        to.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                itemAdapter.filter(from.text.toString())
                itemAdapter.itemFilter.filterPredicate = { item, char ->
                    if (char == "" || char == null || to.isThereAnyKindOfError())
                        true
                    else
                        setAppropriateFilter(
                            i.selectedItemPosition,
                            char.toString(),
                            to.text.toString(),
                            item
                        )
                }
                dismiss()
                true
            } else false
        }
    }

    private fun setFilterBottomSheet() {
        val dBinding: FilterDialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.filter_dialog, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dBinding.root)
        setSpinner(dBinding.spinnerDialog)
        setFilterDoneByEditText(
            dBinding.spinnerDialog,
            dBinding.fromEditText,
            dBinding.toEditText
        ) { dialog.dismiss() }
        dialog.show()
    }

    private fun setSortBottomSheet() {
        val dBinding: SortDialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.sort_dialog, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dBinding.root)
        setSpinner(dBinding.spinnerDialog)
        dBinding.submitSorting.setOnClickListener {
            comparator.field = dBinding.spinnerDialog.selectedItemPosition
            itemListImpl.withComparator(comparator)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setViewModel()
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

    }

    private fun setRecyclerView() {
        itemListImpl = ComparableItemListImpl(comparator)
        itemAdapter = ItemAdapter(itemListImpl)
        fastAdapter = FastAdapter.with(itemAdapter)
        recycler_main_list.adapter = fastAdapter
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainFragmentViewModel.Factory(context!!.applicationContext)
        ).get(MainFragmentViewModel::class.java)
        viewModel.itemsLiveData.observe(this, Observer {
            itemAdapter.add(it)
        })
    }
}
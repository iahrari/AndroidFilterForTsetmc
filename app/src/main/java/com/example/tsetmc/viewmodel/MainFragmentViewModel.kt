package com.example.tsetmc.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.tsetmc.service.externalDataDir
import com.example.tsetmc.service.model.HistoryItem
import com.example.tsetmc.service.utils.SharedPreferencesUtil
import com.example.tsetmc.ui.adapter.item.MarketItem
import com.example.tsetmc.service.utils.generateDynamicFolderName
import com.example.tsetmc.ui.MarketComparator
import com.example.tsetmc.ui.setAppropriateFilter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class MainFragmentViewModel(private val context: Context): BaseViewModel() {
    private val _isDataProcessed = MutableLiveData<Boolean>()
    private val comparator: MarketComparator = MarketComparator(0)
    private val itemListImpl = ComparableItemListImpl(comparator)
    private val sharedPreferencesUtil = SharedPreferencesUtil(context)
    val lastUpdateLive = sharedPreferencesUtil.observableLastUpdate
    val itemAdapter = ItemAdapter (itemListImpl)
    val historyItemAdapter = ItemAdapter<HistoryItem>()

    val isDataProcessed: LiveData<Boolean> get() = _isDataProcessed

    init {
        repository.historyItem.observeForever { list ->
            historyItemAdapter.adapterItems.removeAll { true }
            historyItemAdapter.add(list)
        }
        repository.modifyHistoryList(context.externalDataDir())
        retrieveDataByTime()
    }

    fun retrieveDataByTime(date: Long = generateDynamicFolderName()){
        scope.launch {
            _isDataProcessed.value = false
            val list = arrayListOf<MarketItem>()
            withContext(Dispatchers.Default) {
                list.addAll(
                    repository.retrieveMarketDataList(
                        date,
                        context.externalDataDir(
                            "/$date"
                        ), sharedPreferencesUtil
                    )
                )
            }
            itemAdapter.setNewList(list)
            _isDataProcessed.value = true
        }
    }

    fun handleItemsFiltering(i: Int, from: String, to: String){
        val toFilter = if (to == "") from else to
        itemAdapter.filter(from)
        itemAdapter.itemFilter.filterPredicate = { item, char ->
            if (char == "" || char == null)
                true
            else
                setAppropriateFilter(
                    i,
                    char.toString(),
                    toFilter,
                    item.market
                )
        }
    }

    fun refreshData(){
        repository.deleteFromList(generateDynamicFolderName())
        retrieveDataByTime()
    }

    fun deleteData(date: Long, position: Int){
        historyItemAdapter.remove(position)
        repository.deleteData(context.externalDataDir("/$date"))
    }

    fun handleItemsSorting(i: Int){
        comparator.field = i
        itemListImpl.withComparator(comparator)
    }

    class Factory(private val context: Context): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(MainFragmentViewModel::class.java))
                return MainFragmentViewModel(context) as T

            throw IllegalArgumentException("Unable to cast ${modelClass.name} to ${MainFragmentViewModel::class}")
        }

    }
}
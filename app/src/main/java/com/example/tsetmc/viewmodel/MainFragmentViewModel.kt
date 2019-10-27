package com.example.tsetmc.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tsetmc.service.model.Market
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class MainFragmentViewModel(private val context: Context): BaseViewModel() {
    private val _itemsLiveData: MutableLiveData<List<Market>> = MutableLiveData()
    val itemsLiveData: LiveData<List<Market>>
        get() = _itemsLiveData

    init {
        scope.launch {
          withContext(Dispatchers.Default){
              _itemsLiveData.postValue(
                  repository.retrieveMarketDataList(context)
              )

              Log.i("observer",_itemsLiveData.value?.size.toString())
          }
        }
    }

    class Factory(private val context: Context): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(MainFragmentViewModel::class.java))
                return MainFragmentViewModel(context) as T

            throw IllegalArgumentException("Unable to cast ${modelClass.name} to ${MainFragmentViewModel::class.simpleName}")
        }

    }
}
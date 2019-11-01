package com.example.tsetmc.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tsetmc.service.model.HistoryItem
import com.example.tsetmc.service.utils.*
import com.example.tsetmc.ui.adapter.item.MarketItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException

class Repository {
    private val historyList = mutableListOf<HistoryItem>()
    private val _historyItems = MutableLiveData<MutableList<HistoryItem>>()
    val historyItem: LiveData<MutableList<HistoryItem>>
        get() = _historyItems

    fun modifyHistoryList(directory: File){
        historyList.addAll(ArrayList<HistoryItem>().apply {
            addAll(
                getLongArrayOfDataSubDirectoriesSorted(
                    FileUtil.getListOfSubDirectory(directory)
                )
            )
        })
        _historyItems.postValue(historyList)
    }

    suspend fun retrieveMarketDataList(directory: File): MutableList<MarketItem> {
        val list: MutableList<MarketItem> = ArrayList()
        var dataIsAvailable = false
        val dLong = generateDynamicFolderName()
        for(item in historyList)
            if (item.dateLong == dLong) {
                dataIsAvailable = true
                if (directory.name == dLong.toString())
                    item.setIsSelected(true, 0)
                break
            }

        try {
            withContext(Dispatchers.Default) {
                val destinationDir =
                    if (!dataIsAvailable)
                        FileUtil.parse(
                            dataDownloadLink(),
                            directory.toString()
                        )
                    else
                        File("$directory/xl/worksheets/sheet.xml")
                list.addAll(prepareMarketItemList(XmlParser.parse(destinationDir)))
                if (!dataIsAvailable) {
                    historyList.add(0, HistoryItem().apply {
                        dateLong = generateDynamicFolderName()
                        setIsSelected(true, 0)
                    })

                    _historyItems.postValue(historyList)
                }
            }
        } catch (e: IOException) {
            Log.e("RetrievingData", e.message)
        } catch (e: FileNotFoundException) {
            Log.e("RetrievingData", e.message)
        } catch (e: MalformedURLException) {
            Log.e("RetrievingData", e.message)
        }

        return list
    }

    fun deleteFromList(date: Long){
        historyList.remove(HistoryItem().apply { dateLong = date })
        _historyItems.value = historyList
    }

    fun deleteData(directory: File){
        directory.deleteRecursively()
    }
}
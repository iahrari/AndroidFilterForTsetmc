package com.example.tsetmc

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.example.tsetmc.service.dataDownloadLink
import com.example.tsetmc.service.externalDataDir
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.service.utils.FileUtil
import com.example.tsetmc.service.utils.XmlParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.*
import java.net.MalformedURLException

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val list: MutableList<Market> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scope.launch {
            list.addAll(retrieveDataList()).apply { sortList() }
            data_list_text.text = list.toString()
            to_edit_text.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val from = from_edit_text.text.toString().toFloat()
                    val to = to_edit_text.text.toString().toFloat()
                    val filteredList = filterList(from, to)

                    data_list_text.text = filteredList.toString()
                    true
                } else false
            }
        }
    }

    private suspend fun retrieveDataList(): MutableList<Market>{
        val list: MutableList<Market> = ArrayList()

        try {
            withContext(Dispatchers.Default) {
                val destinationDir = FileUtil.parse(
                    dataDownloadLink(),
                    externalDataDir().toString()
                )
                list.addAll(XmlParser.parse(destinationDir))
            }
        } catch (e: IOException) {
            Log.i("logRetrieving", e.message)
        } catch (e: FileNotFoundException) {
            Log.i("logRetrieving", e.message)
        } catch (e: MalformedURLException) {
            Log.i("logRetrieving", e.message)
        }

        return list
    }

    private fun sortList(){
        list.sortWith(Comparator { o1, o2 ->
            ((o1.lastTransactionPercent!! - o2.lastTransactionPercent!!) * 1000.0).toInt()
        })
    }

    private fun filterList(from: Float, to: Float): List<Market>{
        return list.filter {
            from <= it.lastTransactionPercent!! && to >= it.lastTransactionPercent
        }
    }
}
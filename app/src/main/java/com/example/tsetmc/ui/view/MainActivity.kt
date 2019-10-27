package com.example.tsetmc.ui.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.example.tsetmc.R
import com.example.tsetmc.service.model.Market
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

//    private val job = Job()
//    private val scope = CoroutineScope(Dispatchers.Main + job)
//    private val list: MutableList<Market> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        scope.launch {
//            //list.addAll(retrieveDataList()).apply { sortList() }
//            data_list_text.text = list.toString()
//            to_edit_text.setOnKeyListener { _, keyCode, event ->
//                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    val from = from_edit_text.text.toString().toFloat()
//                    val to = to_edit_text.text.toString().toFloat()
//                    val filteredList = filterList(from, to)
//
//                    data_list_text.text = filteredList.toString()
//                    true
//                } else false
//            }
//        }
    }



//    private fun sortList(){
//        list.sortWith(Comparator { o1, o2 ->
//            ((o1.lastTransactionPercent!! - o2.lastTransactionPercent!!) * 1000.0).toInt()
//        })
//    }

//    private fun filterList(from: Float, to: Float): List<Market>{
//        return list.filter {
//            from <= it.lastTransactionPercent!! && to >= it.lastTransactionPercent
//        }
//    }
}
package com.example.tsetmc.service

import android.content.Context
import android.os.Environment
import java.io.File

fun Context.externalDataDir(): File{
    return File(Environment.getExternalStorageDirectory().toString(), "Android/data/$packageName/data")
}

fun dataDownloadLink(): String{
    return "http://members.tsetmc.com/tsev2/excel/MarketWatchPlus.aspx?d=0"
}
package com.example.tsetmc.service

import android.content.Context
import android.util.Log
import com.example.tsetmc.service.model.Market
import com.example.tsetmc.service.utils.FileUtil
import com.example.tsetmc.service.utils.XmlParser
import com.example.tsetmc.service.utils.generateDynamicFolderName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException

class Repository {

    suspend fun retrieveMarketDataList(context: Context): MutableList<Market>{
        val list: MutableList<Market> = ArrayList()
        var dataIsAvailable = false
        val dataSubDirectories = context.externalDataDir().list { dir, name ->
            File(dir, name).isDirectory
        }

        if (dataSubDirectories.contains(generateDynamicFolderName()))
            dataIsAvailable = true

        try {
            withContext(Dispatchers.Default) {
                val destinationDir =
                    if (!dataIsAvailable)
                    FileUtil.parse(
                        dataDownloadLink(),
                        context.externalDataDir().toString())
                else
                        File("${context.externalDataDir()}/${generateDynamicFolderName()}/xl/worksheets/sheet.xml")
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
}
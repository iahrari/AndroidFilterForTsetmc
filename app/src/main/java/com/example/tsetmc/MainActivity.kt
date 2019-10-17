package com.example.tsetmc

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Xml
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.zip.ZipInputStream

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val list: MutableList<Market> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadFile()
        to_edit_text.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                val from = from_edit_text.text.toString().toFloat()
                val to = to_edit_text.text.toString().toFloat()
                val filteredList = filterList(from, to)
                Log.i("filterFrom", from.toString())
                Log.i("filterTo", to.toString())
                Log.i("filterList", filteredList.toString())

                data_list_text.text = filteredList.toString()
                true
            } else false

        }
    }

    private fun downloadFile() {
        var message = "Done"
        val destinationDir = "Android/data/${applicationContext.packageName}/data"
        var file: File? = null
        scope.launch {
            val url = "http://members.tsetmc.com/tsev2/excel/MarketWatchPlus.aspx?d=0"
            withContext(Dispatchers.Default) {
                val directory =
                    File(Environment.getExternalStorageDirectory().toString(), destinationDir)
                directory.mkdir()
                file = File(directory, "data.xlsx")

                try {
                    file?.createNewFile()
                    val fileOutputStream = FileOutputStream(file)

                    val urlD = URL(url)

                    val huc = urlD.openConnection() as HttpURLConnection
                    huc.requestMethod = "GET"
                    huc.doOutput = true
                    huc.connect()
                    val input = huc.inputStream
                    val buffer = ByteArray(1024)
                    var len1 = input.read(buffer)

                    while ((len1) > 0) {
                        fileOutputStream.write(buffer, 0, len1)
                        len1 = input.read(buffer)
                    }

                    fileOutputStream.close()
                    input.close()

                } catch (e: IOException) {
                    e.printStackTrace()
                    message = e.message!!
                } catch (e: FileNotFoundException) {
                    message = e.message!!
                } catch (e: MalformedURLException) {
                    message = e.message!!
                }
            }
            if (message == "Done")
                unZipData(file!!, File(Environment.getExternalStorageDirectory(), destinationDir))
        }
    }

    private fun unZipData(zipFile: File, destinationDir: File) {
        scope.launch {
            val fis: FileInputStream
            val buffer = ByteArray(1024)
            try {
                fis = FileInputStream(zipFile)
                val zip = ZipInputStream(fis)
                var ze = zip.nextEntry
                while (ze != null) {
                    if (ze.name == "xl/worksheets/sheet.xml") {
                        val newFile = File(destinationDir.toString() + File.separator + ze.name)
                        File(newFile.parent).mkdirs()
                        val fos = FileOutputStream(newFile)
                        var len = zip.read(buffer)
                        while (len > 0) {
                            fos.write(buffer, 0, len)
                            len = zip.read(buffer)
                        }

                        fos.close()
                        zip.closeEntry()
                    }
                    ze = zip.nextEntry
                }

                zip.closeEntry()
                zip.close()
                fis.close()

                readDocument("$destinationDir/xl/worksheets/sheet.xml")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun readDocument(dataFilePath: String) {
        scope.launch {

            val dataFile = File(dataFilePath)
            val parser = Xml.newPullParser()
            val stream: InputStream = dataFile.inputStream()

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(stream, null)
            parser.nextTag()

            readWorkSheet(parser)
            sortList()
            data_list_text.text = list.toString()
            Log.i("parsedData", list.toString())
            Log.i("parsedData", list.size.toString())
        }
    }

    private fun readWorkSheet(parser: XmlPullParser): List<Market> {
        val markets = mutableListOf<Market>()
        parser.require(XmlPullParser.START_TAG, null, "x:worksheet")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            if (parser.name == "x:sheetData")
                list.addAll(readSheets(parser))
            else
                skip(parser)
        }

        return markets
    }

    private fun readSheets(parser: XmlPullParser): List<Market> {

        val markets = mutableListOf<Market>()
        parser.require(XmlPullParser.START_TAG, null, "x:sheetData")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            val row = parser.getAttributeValue(null, "r")
            if (parser.name == "x:row" && row != "1" && row != "2" && row != "3")
                markets.add(readMarketElements(parser))
            else
                skip(parser)
        }

        return markets
    }

    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0)
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
    }

    private fun readMarketElements(parser: XmlPullParser): Market {
        parser.require(XmlPullParser.START_TAG, null, "x:row")
        var symbol: String? = null
        var lastTransactionPercent: Float? = null
        var lastPriceValue: Float? = null
        var lastPricePercent: Float? = null
        var eps: Int? = null
        var pE: Float? = null
        var buyNo: Int? = null
        var buyVolume: Int? = null
        var buyPrice: Float? = null
        var sellPrice: Float? = null
        var sellVolume: Int? = null
        var sellNo: Int? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            val attribute = parser.getAttributeValue(null, "r")
            if (parser.name == "x:c")
                when {
                    attribute.startsWith("A") -> symbol = readColumnsData(parser)
                    attribute.startsWith("J") -> lastTransactionPercent =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("K") -> lastPriceValue =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("M") -> lastPricePercent =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("P") -> eps =
                        readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("Q") -> pE =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("R") -> buyNo =
                        readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("S") -> buyVolume =
                        readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("T") -> buyPrice =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("U") -> sellPrice =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("V") -> sellVolume =
                        readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("W") -> sellNo =
                        readColumnsData(parser).toIntOrNull()
                    else -> skip(parser)
                }
        }

        return Market(
            symbol, lastTransactionPercent, lastPriceValue,
            lastPricePercent, eps, pE, buyNo, buyVolume,
            buyPrice, sellPrice, sellVolume, sellNo
        )
    }

    private fun readColumnsData(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "x:c")
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, null, "x:v")
        val symbol = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, "x:v")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, "x:c")
        return symbol
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }

        return result
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
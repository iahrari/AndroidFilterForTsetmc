package com.example.tsetmc


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Xml
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import java.io.*
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.zip.ZipInputStream

class MainActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadFile()
    }

    @SuppressLint("SetTextI18n")
    private fun downloadFile() {
        text.text = "Start Downloading File ..."
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

                    var total = 0
                    val input = huc.inputStream
                    val buffer = ByteArray(1024)
                    var len1 = input.read(buffer)
                    total += len1

                    while ((len1) > 0) {
                        fileOutputStream.write(buffer, 0, len1)
                        len1 = input.read(buffer)
                        total += len1
                    }

                    fileOutputStream.close()

                } catch (e: IOException) {
                    e.printStackTrace()
                    message = e.message!!
                } catch (e: FileNotFoundException) {
                    message = e.message!!
                } catch (e: MalformedURLException) {
                    message = e.message!!
                }
            }
            text.text = message
            if (message == "Done")
                unZipData(file!!, File(Environment.getExternalStorageDirectory(), destinationDir))
        }
    }

    private fun unZipData(zipFile: File, destinationDir: File) {
        scope.launch {
            text.text = "Start UnZipping Data File"
            val fis: FileInputStream
            val buffer = ByteArray(1024)
            try {
                fis = FileInputStream(zipFile)
                val zip = ZipInputStream(fis)
                var ze = zip.nextEntry
                while (ze != null) {
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
                    ze = zip.nextEntry
                }

                zip.closeEntry()
                zip.close()
                fis.close()

                text.text = "Files UnZipped"
                readDocument("$destinationDir/xl/worksheets/sheet.xml")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun readDocument(dataFilePath: String) {
        scope.launch {
            text.text = "Start Parsing Data"
            val dataFile = File(dataFilePath)
            val parser = Xml.newPullParser()
            val stream: InputStream = dataFile.inputStream()

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(stream, null)
            parser.nextTag()
            val data = readWorkSheet(parser)
            Log.i("parsedData", data.toString())
            Log.i("parsedData", data.size.toString())
            text.text = data.toString()
        }
    }

    private fun readWorkSheet(parser: XmlPullParser): List<Market> {
        val markets = mutableListOf<Market>()
        parser.require(XmlPullParser.START_TAG, null, "x:worksheet")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            if (parser.name == "x:sheetData")
                markets.addAll(readSheets(parser))
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
//            Log.i("tag name", parser.name)
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
        var name: String? = null
        var no: Int? = null
        var value: Int? = null
        var yesterday: Int? = null
        var first: Int? = null
        var lastTransactionValue: Int? = null
        var lastTransactionChanges: Float? = null
        var lastTransactionPercent: Float? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            val attribute = parser.getAttributeValue(null, "r")
            if (parser.name == "x:c")
                when {
                    attribute.startsWith("A") -> symbol = readColumnsData(parser)
                    attribute.startsWith("B") -> name = readColumnsData(parser)
                    attribute.startsWith("C") -> no = readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("E") -> value = readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("F") -> yesterday = readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("G") -> first = readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("H") -> lastTransactionValue =
                        readColumnsData(parser).toIntOrNull()
                    attribute.startsWith("I") -> lastTransactionChanges =
                        readColumnsData(parser).toFloatOrNull()
                    attribute.startsWith("J") -> lastTransactionPercent =
                        readColumnsData(parser).toFloatOrNull()
                    else -> skip(parser)
                }
        }

        return Market(
            symbol,
            name,
            no,
            value,
            yesterday,
            first,
            lastTransactionValue,
            lastTransactionChanges,
            lastTransactionPercent
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
}
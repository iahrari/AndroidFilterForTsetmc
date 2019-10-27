package com.example.tsetmc.service.utils

import android.util.Log
import android.util.Xml
import com.example.tsetmc.service.model.Market
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.InputStream

class XmlParser private constructor(private val dataFile: File){
    private var list: MutableList<Market> = ArrayList()

    private fun readDocument(): List<Market> {
        val parser = Xml.newPullParser()
        val stream: InputStream = dataFile.inputStream()

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)
        parser.nextTag()

        readWorkSheet(parser)
        Log.i("parsedData", list.size.toString())
        return list
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

    companion object {
        fun parse(dataFilePath: File): List<Market>{
            return XmlParser(dataFilePath).readDocument()
        }
    }
}
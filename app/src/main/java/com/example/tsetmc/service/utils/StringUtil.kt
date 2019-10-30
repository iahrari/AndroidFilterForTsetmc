package com.example.tsetmc.service.utils

class StringUtil {
    companion object {
        @JvmStatic
        fun convertEnglishNumberToPersian(number: String): String{
            return number
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹")
                .replace(".", ".")
                .replace("-", "-")
                .replace("/", "/")
        }

        @JvmStatic
        fun convertEnglishNumberToPersian(number: Int): String{
            return convertEnglishNumberToPersian(number.toString())
        }

        @JvmStatic
        fun convertEnglishNumberToPersian(number: Float): String{
            return convertEnglishNumberToPersian(number.toString())
        }

        @JvmStatic
        fun convertEnglishNumberToPersian(number: Double): String{
            return convertEnglishNumberToPersian(number.toString())
        }
    }
}
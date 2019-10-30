package com.example.tsetmc.service.utils

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class FileUtil private constructor(private val url: String, private val destinationDir: String) {

    @Throws
    private fun downloadFile(): File {
        val directory =
            File(destinationDir)
        directory.mkdir()
        val file = File(directory, "data.xlsx")

        file.createNewFile()
        val fileOutputStream = FileOutputStream(file)

        val input = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            doOutput = true
            connect()
        }.inputStream

        val buffer = ByteArray(1024)
        var len1 = input.read(buffer)

        while ((len1) > 0) {
            fileOutputStream.write(buffer, 0, len1)
            len1 = input.read(buffer)
        }

        fileOutputStream.close()
        input.close()

        return unZipData(file)
    }

    @Throws
    private fun unZipData(zipFile: File): File {
        val fis = FileInputStream(zipFile)
        val buffer = ByteArray(1024)
        val zip = ZipInputStream(fis)
        var ze = zip.nextEntry


        while (ze != null) {
            if (ze.name == "xl/worksheets/sheet.xml") {
                val newFile = File(destinationDir + "/" + File.separator + ze.name)
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

        return File("$destinationDir/xl/worksheets/sheet.xml")
    }

    companion object {
        fun parse(url: String, destinationDir: String): File{
            return FileUtil(url, destinationDir).downloadFile()
        }

        fun getListOfSubDirectory(directory: File): Array<String> {
            return directory.list { dir, name ->
                File(dir, name).isDirectory
            }
        }
    }
}
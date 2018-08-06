package hr.atoscvc.salesforcemobile

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {

    fun buildUrl(link: String): URL {
        return URL(link)
    }

    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL?, postData: String): String? {

        val httpURLConnection: HttpURLConnection? = url?.openConnection() as HttpURLConnection

        if (httpURLConnection != null) {
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            httpURLConnection.doInput = true

            val outputStream: OutputStream? = httpURLConnection.outputStream
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))

            bufferedWriter.write(postData)
            bufferedWriter.flush()
            bufferedWriter.close()
            outputStream?.close()

            val inputStream: InputStream? = httpURLConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "iso-8859-1"))
            var result = ""
            for (line in bufferedReader.readLines()) {
                result += line
            }

            bufferedReader.close()
            inputStream?.close()

            httpURLConnection.disconnect()

            return result
        }

        return null

    }
}
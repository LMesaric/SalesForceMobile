package hr.atoscvc.salesforcemobile

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

class BackgroundWorker(
        private val context: WeakReference<Context>,
        @Suppress("unused") private val header: String,
        private val response: AsyncResponse,
        private val progressBar: WeakReference<ProgressBar>
) : AsyncTask<String, Void, String>() {

    interface AsyncResponse {
        fun processFinish(output: String)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.get()?.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: String): String {
        val type: String = p0[0]
        val resetPasswordURL = context.get()?.getString(R.string.resetPasswordURL)

        when (type) {
            context.get()?.getString(R.string.typePasswordReset) -> try {
                val username: String = p0[1]
                val email: String = p0[2]

                val postData: String = URLEncoder.encode(context.get()?.getString(R.string.username), context.get()?.getString(R.string.UTF8)) + "=" + URLEncoder.encode(username, context.get()?.getString(R.string.UTF8)) + "&" +
                        URLEncoder.encode(context.get()?.getString(R.string.email), context.get()?.getString(R.string.UTF8)) + "=" + URLEncoder.encode(email, context.get()?.getString(R.string.UTF8))


                val url: URL = NetworkUtils.buildUrl(resetPasswordURL!!)

                return NetworkUtils.getResponseFromHttpUrl(url, postData)
                        ?: return context.get()?.getString(R.string.serverNoResponse)!!

            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return context.get()?.getString(R.string.malformedURL)!!
            } catch (e: IOException) {
                e.printStackTrace()
                return context.get()?.getString(R.string.exceptionIO)!!
            }
        }
        return context.get()?.getString(R.string.typeNotRecognized)!!
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        //progressBar.get()?.visibility = View.INVISIBLE
        response.processFinish(result)
    }
}
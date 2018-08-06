package hr.atoscvc.salesforcemobile

import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import java.util.*

class MyApp : Application() {

    private var instance: Bitmap? = null
    private var listener: LogoutListener? = null
    private lateinit var timer: Timer

    fun registerSessionListener(listener: LogoutListener) {
        if (this.listener == null) {
            this.listener = listener
        }
    }

    fun startUserSession() {
        cancelTimer()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                listener?.onSessionTimeout()
            }

        }, 30000)    //FIXME Use a normal value
    }

    fun cancelTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    fun onUserInteracted() {
        startUserSession()
    }

    fun getInstance(resources: Resources): Bitmap {
        if (instance == null) {
            instance = BitmapManager.decodeSampledBitmapFromResource(
                    resources,
                    R.drawable.background_test,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels
            )
        }

        return instance as Bitmap

    }

}
package hr.atoscvc.salesforcemobile

import android.app.Application
import java.util.*

class MyApp : Application() {

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

        }, 7200000)    // 2 hours
    }

    fun cancelTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    fun onUserInteracted() {
        startUserSession()
    }
}

package hr.atoscvc.salesforcemobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //LUKA - zatvoriti search setting na fling up
        //LUKA - recyclerview item - na klik podizanje i expand layouta iznad svega
        //LUKA - settings layout -> Preferences

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            activity?.fabAdd?.hide()
        }
    }
}

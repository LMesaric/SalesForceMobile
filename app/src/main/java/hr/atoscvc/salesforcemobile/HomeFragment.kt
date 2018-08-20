package hr.atoscvc.salesforcemobile


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*


class HomeFragment : Fragment() {

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity?.fabAdd?.visibility = View.INVISIBLE

        return inflater.inflate(R.layout.fragment_home, container, false)
    }


}

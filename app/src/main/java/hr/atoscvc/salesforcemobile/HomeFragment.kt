package hr.atoscvc.salesforcemobile


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class HomeFragment : Fragment() {

    private var fabAddHome: FloatingActionButton? = null

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        fabAddHome = activity?.findViewById(R.id.fabAdd)
        fabAddHome?.visibility = View.INVISIBLE

        return inflater.inflate(R.layout.fragment_home, container, false)
    }


}

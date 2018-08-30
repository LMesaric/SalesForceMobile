package hr.atoscvc.salesforcemobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.search_settings.view.*

class HomeFragment : Fragment() {

    private var isExpanded: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        view.btnTestExpand.setOnClickListener {
            TransitionManager.beginDelayedTransition(view.searchSettingsContainer, Slide(Gravity.TOP).setDuration(600))
            view.searchSettingsContainer.visibility = if (isExpanded) View.GONE else View.VISIBLE
            isExpanded = !isExpanded
            view.searchSettingsContainer.isClickable = isExpanded   //LUKA - testirati forwardiranje clicka za View.INVISIBLE
            //LUKA - recyclerview na klik podizanje i expand
            //LUKA - settings layout resources i Preferences
        }

        view.btnTestBelow.setOnClickListener {
            Toast.makeText(activity, "TOAST", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            activity?.fabAdd?.hide()
        }
    }
}

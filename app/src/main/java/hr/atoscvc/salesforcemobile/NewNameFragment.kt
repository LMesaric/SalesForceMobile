package hr.atoscvc.salesforcemobile

import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_new_name.*
import kotlinx.android.synthetic.main.fragment_new_name.view.*

class NewNameFragment : Fragment(), View.OnClickListener {

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    private var lastClickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_new_name, container, false)

        replaceFragmentListener = activity as ReplaceFragmentListener

        view.btnNewNameNext.setOnClickListener(this)

        view.etRegisterFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etRegisterFirstName.setText(view.etRegisterFirstName.text.toString().trim())
            }
        }

        view.etRegisterLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etRegisterLastName.setText(view.etRegisterLastName.text.toString().trim())
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.registerImageView?.setImageResource(R.drawable.name_page)
    }

    override fun onClick(view: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        val firstName = etRegisterFirstName.text.toString().trim()
        val lastName = etRegisterLastName.text.toString().trim()
        var thereAreNoErrors = true

        if (firstName.isBlank()) {
            etRegisterFirstName.error = getString(R.string.firstNameEmptyMessage)
            thereAreNoErrors = false
        }
        if (lastName.isBlank()) {
            etRegisterLastName.error = getString(R.string.lastNameEmptyMessage)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            ActiveUserSingleton.user = User(firstName, lastName, "")
            replaceFragmentListener.replaceFragment(EmailFragment())
        }
    }
}

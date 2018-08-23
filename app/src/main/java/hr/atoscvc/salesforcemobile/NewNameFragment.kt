package hr.atoscvc.salesforcemobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_new_name_layout.*
import kotlinx.android.synthetic.main.fragment_new_name_layout.view.*

class NewNameFragment : Fragment(), View.OnClickListener {

    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_new_name, container, false)

        replaceFragmentListener = activity as ReplaceFragmentListener

        view.btnNextNewName.setOnClickListener(this)

        view.etFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etFirstName.setText(view.etFirstName.text.toString().trim())
            }
        }

        view.etLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                view.etLastName.setText(view.etLastName.text.toString().trim())
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.registerImageView?.setImageResource(R.drawable.name_page)
    }

    override fun onClick(view: View?) {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        var thereAreNoErrors = true

        if (firstName.isBlank()) {
            etFirstName.error = getString(R.string.firstNameEmptyMessage)
            thereAreNoErrors = false
        }
        if (lastName.isBlank()) {
            etLastName.error = getString(R.string.lastNameEmptyMessage)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            ActiveUserSingleton.user = User(firstName, lastName, "")
            replaceFragmentListener.replaceFragment(EmailFragment())
        }
    }
}

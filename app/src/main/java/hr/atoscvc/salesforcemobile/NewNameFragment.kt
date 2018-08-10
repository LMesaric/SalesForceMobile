package hr.atoscvc.salesforcemobile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


class NewNameFragment : Fragment(), View.OnClickListener {

    private lateinit var btnNext: Button
    private lateinit var replaceFragmentListener: ReplaceFragmentListener
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_new_name, container, false)

        btnNext = view.findViewById(R.id.btnNextNewName)
        replaceFragmentListener = activity as ReplaceFragmentListener

        btnNext.setOnClickListener(this)

        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)

        etFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etFirstName.setText(etFirstName.text.toString().trim())
            }
        }

        etLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etLastName.setText(etLastName.text.toString().trim())
            }
        }

        return view
    }

    override fun onClick(p0: View?) {
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

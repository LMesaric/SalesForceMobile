package hr.atoscvc.salesforcemobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_email_layout.*
import kotlinx.android.synthetic.main.fragment_email_layout.view.*

//LUKA - provjera za register (i Contact) http://emailregex.com/

class EmailFragment : Fragment(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var replaceFragmentListener: ReplaceFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_email, container, false)
        replaceFragmentListener = activity as ReplaceFragmentListener

        mAuth = FirebaseAuth.getInstance()

        view.btnNextEmail.setOnClickListener(this)

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.registerImageView?.setImageResource(R.drawable.email_page)
    }

    override fun onClick(view: View?) {
        val email: String = etRegisterEmail.text.toString().trim()

        if (email.isBlank()) {
            etRegisterEmail.error = getString(R.string.emailEmptyMessage)
        } else {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result.signInMethods?.isEmpty() != false) {
                                ActiveUserSingleton.user?.email = email
                                replaceFragmentListener.replaceFragment(PasswordFragment())
                            } else {
                                etRegisterEmail.error = getString(R.string.emailAlreadyInUse)
                            }
                        } else {
                            etRegisterEmail.error = task.exception?.message
                        }
                    }
        }
    }
}

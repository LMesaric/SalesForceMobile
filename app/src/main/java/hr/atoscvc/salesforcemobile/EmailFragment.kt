package hr.atoscvc.salesforcemobile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class EmailFragment : Fragment(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var replaceFragmentListener: ReplaceFragmentListener
    private lateinit var btnNext: Button
    private lateinit var etEmail: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_email, container, false)
        replaceFragmentListener = activity as ReplaceFragmentListener

        mAuth = FirebaseAuth.getInstance()

        etEmail = view.findViewById(R.id.etEmail)

        btnNext = view.findViewById(R.id.btnNextEmail)

        btnNext.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        val email = etEmail.text.toString().trim()

        if (email.isBlank()) {
            etEmail.error = getString(R.string.emailEmptyMessage)
        } else {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val check = task.result.signInMethods?.isEmpty() ?: true
                            if (check) {
                                ActiveUserSingleton.user?.email = email     //FILIP - sto ako user?.email ne prodje, je li onda "null = email" -> try/catch mozda?
                                replaceFragmentListener.replaceFragment(PasswordFragment())
                            } else {
                                etEmail.error = getString(R.string.emailAlreadyInUse)
                            }
                        } else {
                            etEmail.error = task.exception?.message
                        }

                    }
        }

    }
}

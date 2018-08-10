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

    private lateinit var replaceFragmentListener: ReplaceFragmentListener
    private lateinit var btnNext: Button
    private lateinit var etEmail: EditText
    private lateinit var mAuth: FirebaseAuth

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

    override fun onClick(p0: View?) {
        val email = etEmail.text.toString()

        if (email.isBlank()) {
            etEmail.error = getString(R.string.emailEmptyMessage)
        } else {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            val check = task.result.signInMethods?.isEmpty() ?: true
                            if (check) {
                                ActiveUserSingleton.user?.email = email
                                replaceFragmentListener.replaceFragment(PasswordFragment())
                            } else {
                                etEmail.error = "Email already exists"
                            }
                        } else {
                            etEmail.error = task.exception?.message
                        }

                    }
        }

    }
}

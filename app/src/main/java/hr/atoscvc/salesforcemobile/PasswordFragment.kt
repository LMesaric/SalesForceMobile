package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.atoscvc.salesforcemobile.CheckPasswordConstraints.checkPasswordConstraints

class PasswordFragment : Fragment(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var btnFinish: Button
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_password, container, false)

        btnFinish = view.findViewById(R.id.btnFinishPass)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)

        btnFinish.setOnClickListener(this)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        etPassword.addTextChangedListener(PasswordTextWatcher(ActiveUserSingleton.user?.email.toString(), etPassword))  //FILIP - null se ne smije poslati u PasswordTextWatcher
        //LUKA - u PasswordTextWatcher email nije koristan umjesto usernamea -> izbaciti username dio watchera?

        return view
    }

    override fun onClick(view: View?) {
        val password = etPassword.text.toString()   // Do NOT trim the password
        var thereAreNoErrors = true

        val passwordStatus = checkPasswordConstraints(ActiveUserSingleton.user?.email.toString(), password)

        if (!passwordStatus.success) {
            etPassword.error = passwordStatus.message
            thereAreNoErrors = false
        }

        if (password != etConfirmPassword.text.toString()) {
            etConfirmPassword.error = getString(R.string.wrongConfirmPassword)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            mAuth.createUserWithEmailAndPassword(ActiveUserSingleton.user?.email.toString(), HashSHA3.getHashedValue(password))
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            addUserToDatabase()
                        } else {
                            Toast.makeText(activity, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }

    private fun sendToMain() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }

    private fun addUserToDatabase() {
        mAuth.currentUser?.uid?.let {
            ActiveUserSingleton.user?.let { it1 ->
                db.collection("Users").document(it).set(it1)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                sendToMain()
                            } else {
                                ActiveUserSingleton.user = null
                                mAuth.signOut()
                                Toast.makeText(activity, "Error: " + task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
            }
        }
    }
}
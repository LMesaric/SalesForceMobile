package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    lateinit var username: String
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = FirebaseFirestore.getInstance()

        mAuth = FirebaseAuth.getInstance()

        /* Listeners reporting password constraints errors on every change of username or password */
        etUsername.addTextChangedListener(PasswordTextWatcher(etUsername, etPassword))
        etPassword.addTextChangedListener(PasswordTextWatcher(etUsername, etPassword))

        /* Listeners trimming user input (it is trimmed again before being sent to the database) and changing icon color */
        etFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etFirstName.setText(etFirstName.text.toString().trim())
                etFirstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile, 0, 0, 0)
            } else {
                etFirstName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile_accent, 0, 0, 0)
            }
        }
        etLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etLastName.setText(etLastName.text.toString().trim())
                etLastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile, 0, 0, 0)
            } else {
                etLastName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_smile_accent, 0, 0, 0)
            }
        }
        etUsername.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etUsername.setText(etUsername.text.toString().trim())
                etUsername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_username_outline, 0, 0, 0)
            } else {
                etUsername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_username_outline_accent, 0, 0, 0)
            }
        }
        etEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etEmail.setText(etEmail.text.toString().trim())
                etEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline, 0, 0, 0)
            } else {
                etEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_email_outline_accent, 0, 0, 0)
            }
        }
        /* Passwords are NOT trimmed */
        etPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline, 0, 0, 0)
            } else {
                etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline_accent, 0, 0, 0)
            }
        }
        etConfirm.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                etConfirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline, 0, 0, 0)
            } else {
                etConfirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_password_outline_accent, 0, 0, 0)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(BitmapDrawable(
                applicationContext.resources,
                (application as MyApp).getInstance(applicationContext.resources)
        ))
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            sendToMain()
        }
    }

    private fun sendToMain() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
        finish()
    }

    fun onRegister(@Suppress("UNUSED_PARAMETER") view: View) {
        var thereAreNoErrors = true

        val password = etPassword.text.toString()       // Do NOT trim the password
        firstName = etFirstName.text.toString().trim()
        lastName = etLastName.text.toString().trim()
        username = etUsername.text.toString().trim()
        email = etEmail.text.toString().trim()

        val passwordStatus = CheckPasswordConstraints.checkPasswordConstraints(username, password)

        if (!passwordStatus.success) {
            etPassword.error = passwordStatus.message
            thereAreNoErrors = false
        }
        if (username.isBlank()) {
            etUsername.error = getString(R.string.usernameEmptyMessage)
            thereAreNoErrors = false
        }
        if (firstName.isBlank()) {
            etFirstName.error = getString(R.string.firstNameEmptyMessage)
            thereAreNoErrors = false
        }
        if (lastName.isBlank()) {
            etLastName.error = getString(R.string.lastNameEmptyMessage)
            thereAreNoErrors = false
        }
        if (email.isBlank()) {
            etEmail.error = getString(R.string.emailEmptyMessage)
            thereAreNoErrors = false
        }
        if (password != etConfirm.text.toString()) {
            etConfirm.error = getString(R.string.wrongConfirmPassword)
            thereAreNoErrors = false
        }

        if (thereAreNoErrors) {
            btnRegister.visibility = View.INVISIBLE
            registerProgress.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, HashSHA3.getHashedValue(password))
                    .addOnCompleteListener(this) { task1 ->
                        if (task1.isSuccessful) {
                            addUserToDatabase()
                        } else {
                            val errorMessage: String = task1.exception?.message.toString()
                            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                        registerProgress.visibility = View.INVISIBLE
                        btnRegister.visibility = View.VISIBLE
                    }
        }
    }

    private fun addUserToDatabase() {
        val user = User(firstName, lastName, username, email)
        mAuth.currentUser?.uid?.let {
            db.collection("Users").document(it).set(user)
                    .addOnCompleteListener(this) { task2 ->
                        if (task2.isSuccessful) {
                            db.collection("Users").document(mAuth.uid.toString()).get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            val activeUser = User(documentSnapshot.getString("firstName").toString(), documentSnapshot.getString("lastName").toString(), documentSnapshot.getString("username").toString(), mAuth.currentUser!!.email.toString())
                                            ActiveUserSingleton.setActiveUser(activeUser)
                                            sendToMain()
                                        } else {
                                            Toast.makeText(this, "No user found", Toast.LENGTH_LONG).show()
                                        }
                                    }
                        } else {
                            ActiveUserSingleton.setActiveUser(null)
                            mAuth.signOut()
                            Toast.makeText(this, "Error: " + task2.exception?.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }
}

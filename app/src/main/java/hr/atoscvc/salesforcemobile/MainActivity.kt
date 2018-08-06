package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.firestore.DocumentSnapshot
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener





class MainActivity : AppCompatActivity(), LogoutListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MyApp).registerSessionListener(this)
        (application as MyApp).startUserSession()

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        db.collection("Users").document(mAuth.uid.toString()).get()
                .addOnSuccessListener { documentSnapshot ->
                    tvUsername.text = documentSnapshot.getString("username")
                }

    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(BitmapDrawable(
                applicationContext.resources,
                (application as MyApp).getInstance(applicationContext.resources)
        ))
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    fun onLogout(@Suppress("UNUSED_PARAMETER") view: View) {
        mAuth.signOut()
        sendToLogin()
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    fun onAddContact(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ContactEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
        }
        startActivity(intent)
    }

    fun onViewContacts(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ContactListActivity::class.java)
        startActivity(intent)
    }

    fun onAddCompany(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, CompanyEditorActivity::class.java).apply {
            putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
        }
        startActivity(intent)
    }

    fun onViewCompanies(@Suppress("UNUSED_PARAMETER") view: View) {
        /*val intent = Intent(this, CompanyListActivity::class.java)
        startActivity(intent)**/
    }


    //FIXME Method should be moved elsewhere
    //FIXME In Manifest: android:parentActivityName=".MainMenuActivity" should be changed accordingly
    fun onChangePassword(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    override fun onSessionTimeout() {
        mAuth.signOut()
        sendToLogin()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

}

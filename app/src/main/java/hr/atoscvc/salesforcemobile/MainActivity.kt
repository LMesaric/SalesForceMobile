package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


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

    private fun sendToLogin() {
        ActiveUserSingleton.setActiveUser(null)
        mAuth.signOut()
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
        val intent = Intent(this, CompanyListActivity::class.java)
        startActivity(intent)
    }

    override fun onSessionTimeout() {
        sendToLogin()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == R.id.action_logout) {
                sendToLogin()
            } else if (item.itemId == R.id.action_account_settings) {
                //FIXME Method should be moved elsewhere
                //FIXME In Manifest: android:parentActivityName=".MainMenuActivity" should be changed accordingly
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

}

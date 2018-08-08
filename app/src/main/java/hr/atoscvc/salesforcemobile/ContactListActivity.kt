package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_contact_list.*

class ContactListActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        mAuth = FirebaseAuth.getInstance()

        recyclerViewContacts.setHasFixedSize(true)
        recyclerViewContacts.layoutManager = LinearLayoutManager(this)

        val contactList = ArrayList<Contact>()

        contactList.add(Contact(
                "akjssgddad",
                0,
                1,
                "Ana",
                "Peric",
                "Atos",
                "091251244",
                "email@email.com",
                "asdjbajhbdsnadjhas asda sda safsd fasdgasdg asdga sdgasdg asdg sdgasd ggasdg asdg asdgas dgasdg sadg sdg asdgg asdg sadg"
        ))
        contactList.add(Contact(
                "tffcvbh",
                1,
                0,
                "Matej",
                "Bubic",
                "Siemens",
                "087654567899",
                "email@gmail.com",
                "alksnbdabnsd"
        ))
        contactList.add(Contact(
                "klejwf",
                1,
                3,
                "Tea",
                "Bubic",
                "Google",
                "56456789867",
                "yahoo@gmail.com",
                "masjdban asdaisd"
        ))

        val adapter = ContactAdapter(contactList, applicationContext)
        recyclerViewContacts.adapter = adapter
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
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

}

package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_contact_list.*

class ContactListActivity : AppCompatActivity() {

    //TODO Implementirati Search funkcionalnost

    companion object {
        const val requestCodeRefresh = 3
    }

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
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "091251244",
                "email@email.com",
                1,
                "asdjbajhbdsnadjhas asda sda safsd fasdgasdg asdga sdgasdg asdg sdgasd ggasdg asdg asdgas dgasdg sadg sdg asdgg asdg sadg"
        ))
        contactList.add(Contact(
                "tffcvbh",
                1,
                0,
                "Matej",
                "Bubic",
                Company(
                        "knksgnf",
                        1,
                        "45678934",
                        "Siemens",
                        "siemens.hr",
                        1,
                        "Very great company",
                        "01484567",
                        0,
                        4,
                        "100000"
                ),
                "087654567899",
                "email@gmail.com",
                0,
                "alksnbdabnsd"
        ))
        contactList.add(Contact(
                "klejwf",
                1,
                3,
                "Tea",
                "Bubic",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "56456789867",
                "yahoo@gmail.com",
                1,
                "masjdban asdaisd"
        ))

        val adapter = ContactAdapter(
                contactList,
                this,
                intent.getBooleanExtra(
                        getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT),
                        false
                )
        )
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
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestCodeRefresh) {
            if (resultCode == RESULT_OK) {
                recyclerViewContacts.adapter?.notifyDataSetChanged()
            }
        }
    }
}
